package com.wadii.pages.admin.provider

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.provider.CreateBranchRequest
import com.wadii.domain.model.provider.CreateLinkRequest
import com.wadii.domain.model.provider.CreateProviderByAdminRequest
import com.wadii.domain.model.provider.CreateWorkTimeRequest
import com.wadii.domain.usecase.CountryUseCase
import com.wadii.domain.usecase.ProviderUseCase
import com.wadii.state.AppState
import com.wadii.utils.RequestValidators
import com.wadii.utils.Validation
import com.wadii.utils.firstMessage
import com.wadii.viewmodel.ServicesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

val PROVIDER_WEEK_DAYS = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")

private fun emptyWeekWorkTimes() = PROVIDER_WEEK_DAYS.map { day -> NewWorkTime(day = day) }

class AddProviderViewModel(
    private val providerUseCase: ProviderUseCase,
    private val servicesUseCase: ServicesUseCase,
    private val countryUseCase: CountryUseCase
) : BaseViewModel<AddProviderState, AddProviderEvent>() {

    override val initialState: AddProviderState get() = AddProviderState()

    override val state: StateFlow<AddProviderState> = _state
        .onStart { loadOptions() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: AddProviderEvent) {
        when (event) {
            is AddProviderEvent.SetFirstName -> updateState { it.copy(firstName = event.value) }
            is AddProviderEvent.SetLastName -> updateState { it.copy(lastName = event.value) }
            is AddProviderEvent.SetEmail -> updateState { it.copy(email = event.value) }
            is AddProviderEvent.SetPassword -> updateState { it.copy(password = event.value) }
            is AddProviderEvent.SetPhone -> updateState { it.copy(phone = event.value) }
            is AddProviderEvent.SetCity -> updateState { it.copy(cityId = event.city.id) }
            is AddProviderEvent.SetProviderName -> updateState { it.copy(providerName = event.value) }
            is AddProviderEvent.SetRate -> updateState { it.copy(rate = event.value) }

            is AddProviderEvent.ToggleService -> updateState {
                val ids = it.selectedServiceIds
                it.copy(selectedServiceIds = if (event.serviceId in ids) ids - event.serviceId else ids + event.serviceId)
            }

            AddProviderEvent.AddBranch -> updateState {
                it.copy(branches = it.branches + NewBranch(workTimes = emptyWeekWorkTimes()))
            }
            is AddProviderEvent.RemoveBranch -> updateState {
                it.copy(branches = it.branches.filterIndexed { i, _ -> i != event.branchIndex })
            }
            is AddProviderEvent.SetBranchName -> updateBranch(event.branchIndex) { it.copy(name = event.value) }
            is AddProviderEvent.SetBranchAddress -> updateBranch(event.branchIndex) { it.copy(address = event.value) }
            is AddProviderEvent.SetWorkTimeStart -> updateWorkTime(event.branchIndex, event.workTimeIndex) { it.copy(startTime = event.value) }
            is AddProviderEvent.SetWorkTimeClose -> updateWorkTime(event.branchIndex, event.workTimeIndex) { it.copy(closeTime = event.value) }

            AddProviderEvent.AddLink -> updateState { it.copy(links = it.links + "") }
            is AddProviderEvent.RemoveLink -> updateState {
                it.copy(links = it.links.filterIndexed { i, _ -> i != event.linkIndex })
            }
            is AddProviderEvent.SetLink -> updateState {
                it.copy(links = it.links.mapIndexed { i, l -> if (i == event.linkIndex) event.value else l })
            }

            AddProviderEvent.Submit -> submit()
        }
    }

    private fun updateBranch(branchIndex: Int, reducer: (NewBranch) -> NewBranch) = updateState {
        it.copy(branches = it.branches.mapIndexed { i, b -> if (i == branchIndex) reducer(b) else b })
    }

    private fun updateWorkTime(branchIndex: Int, workTimeIndex: Int, reducer: (NewWorkTime) -> NewWorkTime) =
        updateBranch(branchIndex) { branch ->
            branch.copy(workTimes = branch.workTimes.mapIndexed { i, wt -> if (i == workTimeIndex) reducer(wt) else wt })
        }

    private fun loadOptions() = screenModelScope.launch {
        updateState { it.copy(isLoadingOptions = true) }
        servicesUseCase.getServiceList { r ->
            r.handelState(onSuccess = { data -> updateState { it.copy(allServices = data.data ?: emptyList()) } })
        }
        countryUseCase.getCityList { r ->
            r.handelState(onSuccess = { data ->
                val cities = data.data ?: emptyList()
                updateState { it.copy(cities = cities, cityId = it.cityId.takeIf { id -> id != 0L } ?: cities.firstOrNull()?.id ?: 0L) }
            })
        }
        updateState { it.copy(isLoadingOptions = false) }
    }

    private fun submit() {
        val s = _state.value
        val errors = buildMap<String, String> {
            Validation.name(s.firstName, "First name")?.let { put("firstName", it) }
            Validation.name(s.lastName, "Last name")?.let { put("lastName", it) }
            putAll(RequestValidators.createProviderByAdmin(
                s.email, s.password, s.providerName, s.phone, s.cityId.takeIf { it != 0L },
                s.branches.map { it.name }, s.links.filter { it.isNotBlank() }
            ))
            if (s.cityId == 0L) put("city", "Choose an area")
            if (s.phone.isBlank()) put("phone", "Phone number is required")
            val rate = s.rate.toDoubleOrNull()
            if (rate == null || rate < 0 || rate > 5) put("rate", "Rate is a number from 0 to 5")
            s.branches.forEachIndexed { i, b ->
                if (b.name.isBlank()) put("branch$i", "Every branch needs a name")
                b.workTimes.forEachIndexed { j, wt ->
                    if (wt.startTime.isNotBlank() || wt.closeTime.isNotBlank()) {
                        RequestValidators.workTime(1, wt.day, wt.startTime, wt.closeTime)
                            .filterKeys { it == "startTime" || it == "closeTime" }.values.firstOrNull()
                            ?.let { put("wt${i}_$j", "${wt.day}: $it") }
                    }
                }
            }
        }
        updateState { it.copy(errors = errors) }
        if (errors.isNotEmpty()) {
            AppState.toast(errors.firstMessage(), true)
            return
        }
        screenModelScope.launch {
            updateState { it.copy(isSaving = true, error = null) }
            providerUseCase.createProviderByAdmin(
                CreateProviderByAdminRequest(
                    firstName = s.firstName,
                    lastName = s.lastName,
                    email = s.email,
                    password = s.password,
                    phone = s.phone,
                    cityId = s.cityId,
                    providerName = s.providerName,
                    rate = s.rate.toDoubleOrNull() ?: 0.0,
                    serviceIds = s.selectedServiceIds.map { it.toInt() },
                    branches = s.branches.map { b ->
                        CreateBranchRequest(
                            name = b.name,
                            address = b.address,
                            workTimes = b.workTimes
                                .filter { wt -> wt.startTime.isNotBlank() && wt.closeTime.isNotBlank() }
                                .map { wt -> CreateWorkTimeRequest(wt.day, wt.startTime, wt.closeTime) }
                        )
                    },
                    links = s.links.filter { it.isNotBlank() }.map { CreateLinkRequest(it) },
                    offers = emptyList()
                )
            ) { r ->
                r.handelState(
                    onSuccess = {
                        updateState { it.copy(isSaving = false, savedSuccessfully = true) }
                        AppState.toast("Provider created!")
                    },
                    onError = { e, _ ->
                        updateState { it.copy(isSaving = false, error = e) }
                        AppState.toast(e.ifBlank { "Failed to create provider" }, true)
                    }
                )
            }
        }
    }
}
