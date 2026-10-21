package com.wadii.pages.admin.provider

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.provider.BranchRequest
import com.wadii.domain.model.provider.EditableBranch
import com.wadii.domain.model.provider.EditableLink
import com.wadii.domain.model.provider.EditableWorkTime
import com.wadii.domain.model.provider.OfferRequest
import com.wadii.domain.model.provider.ProviderModel
import com.wadii.domain.model.provider.UpdateProviderRequest
import com.wadii.domain.model.provider.WorkTimeRequest
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

private val WEEK_DAYS = listOf("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

private fun emptyWeekWorkTimes() = WEEK_DAYS.map { day -> EditableWorkTime(id = 0, day = day, startTime = "", closeTime = "") }

class EditProviderViewModel(
    private val provider: ProviderModel,
    private val providerUseCase: ProviderUseCase,
    private val servicesUseCase: ServicesUseCase
) : BaseViewModel<EditProviderState, EditProviderEvent>() {

    override val initialState: EditProviderState get() = EditProviderState()

    override val state: StateFlow<EditProviderState> = _state
        .onStart { seed() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: EditProviderEvent) {
        when (event) {
            is EditProviderEvent.SetRate -> updateState { it.copy(rate = event.value) }
            is EditProviderEvent.SetFollowersCount -> updateState { it.copy(followersCount = event.value) }

            is EditProviderEvent.ToggleService -> updateState {
                val ids = it.selectedServiceIds
                it.copy(selectedServiceIds = if (event.serviceId in ids) ids - event.serviceId else ids + event.serviceId)
            }

            EditProviderEvent.AddBranch -> updateState {
                it.copy(branches = it.branches + EditableBranch(id = 0, name = "", address = "", workTimes = emptyWeekWorkTimes()))
            }
            is EditProviderEvent.RemoveBranch -> updateState {
                it.copy(branches = it.branches.filterIndexed { i, _ -> i != event.branchIndex })
            }
            is EditProviderEvent.SetBranchName -> updateBranch(event.branchIndex) { it.copy(name = event.value) }
            is EditProviderEvent.SetBranchAddress -> updateBranch(event.branchIndex) { it.copy(address = event.value) }

            is EditProviderEvent.SetWorkTimeStart -> updateWorkTime(event.branchIndex, event.workTimeIndex) { it.copy(startTime = event.value) }
            is EditProviderEvent.SetWorkTimeClose -> updateWorkTime(event.branchIndex, event.workTimeIndex) { it.copy(closeTime = event.value) }

            EditProviderEvent.AddLink -> updateState { it.copy(links = it.links + EditableLink(id = 0, link = "")) }
            is EditProviderEvent.RemoveLink -> updateState {
                it.copy(links = it.links.filterIndexed { i, _ -> i != event.linkIndex })
            }
            is EditProviderEvent.SetLink -> updateState {
                it.copy(links = it.links.mapIndexed { i, l -> if (i == event.linkIndex) EditableLink(l.id, event.value) else l })
            }

            EditProviderEvent.Submit -> submit()
        }
    }

    private fun updateBranch(branchIndex: Int, reducer: (EditableBranch) -> EditableBranch) = updateState {
        it.copy(branches = it.branches.mapIndexed { i, b -> if (i == branchIndex) reducer(b) else b })
    }

    private fun updateWorkTime(branchIndex: Int, workTimeIndex: Int, reducer: (EditableWorkTime) -> EditableWorkTime) =
        updateBranch(branchIndex) { branch ->
            branch.copy(workTimes = branch.workTimes.mapIndexed { i, wt -> if (i == workTimeIndex) reducer(wt) else wt })
        }

    private suspend fun seed() {
        updateState {
            it.copy(
                id = provider.id,
                userId = provider.user.id,
                name = provider.name,
                rate = provider.rate.toString(),
                followersCount = provider.followersCount.toString(),
                selectedServiceIds = provider.services.map { s -> s.id }.toSet(),
                branches = provider.branches.map { b ->
                    EditableBranch(
                        id = b.id,
                        name = b.name,
                        address = b.address,
                        workTimes = WEEK_DAYS.map { day ->
                            val existing = b.workTimes.find { wt -> wt.day == day }
                            EditableWorkTime(
                                id = existing?.id ?: 0,
                                day = day,
                                startTime = existing?.startTime ?: "",
                                closeTime = existing?.closeTime ?: ""
                            )
                        }
                    )
                },
                links = provider.links.map { l -> EditableLink(l.id, l.link) },
                user = provider.user,
                offersPassthrough = provider.offers.map { o -> OfferRequest(o.id, o.title, o.description, o.endDate) }
            )
        }
        loadServices()
    }

    private fun loadServices() = screenModelScope.launch {
        servicesUseCase.getServiceList { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoadingServices = true) } },
                onSuccess = { data -> updateState { it.copy(allServices = data.data ?: emptyList(), isLoadingServices = false) } },
                onError = { _, _ -> updateState { it.copy(isLoadingServices = false) } }
            )
        }
    }

    private fun submit() {
        val s = _state.value
        val errors = buildMap<String, String> {
            Validation.name(s.user.firstName, "First name")?.let { put("firstName", it) }
            Validation.name(s.user.lastName, "Last name")?.let { put("lastName", it) }
            Validation.email(s.user.email)?.let { put("email", it) }
            Validation.phone(s.user.phone, required = false)?.let { put("phone", it) }
            if (s.id <= 0) put("id", "We couldn't identify this provider")
            val rate = s.rate.toDoubleOrNull()
            if (rate == null || rate < 0 || rate > 5) put("rate", "Rate is a number from 0 to 5")
            val followers = s.followersCount.toLongOrNull()
            if (followers == null || followers < 0) put("followers", "Followers is a whole number, 0 or more")
            if (s.selectedServiceIds.isEmpty()) put("services", "Pick at least one service")
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
            s.links.forEachIndexed { i, l -> Validation.url(l.link, required = true)?.let { put("link$i", it) } }
        }
        updateState { it.copy(errors = errors) }
        if (errors.isNotEmpty()) {
            AppState.toast(errors.firstMessage(), true)
            return
        }
        screenModelScope.launch {
            updateState { it.copy(isSaving = true, error = null) }
            providerUseCase.updateProvider(
                UpdateProviderRequest(
                    id = s.id,
                    firstName = s.user.firstName,
                    lastName = s.user.lastName,
                    phone = s.user.phone,
                    email = s.user.email,
                    serviceIds = s.selectedServiceIds.map { it.toInt() },
                    branches = s.branches.map { b ->
                        BranchRequest(
                            id = b.id,
                            name = b.name,
                            address = b.address,
                            workTimes = b.workTimes.map { wt -> WorkTimeRequest(wt.id, wt.day, wt.startTime, wt.closeTime) }
                        )
                    },
                    links = s.links,
                    offers = s.offersPassthrough
                )
            ) { r ->
                r.handelState(
                    onSuccess = {
                        updateState { it.copy(isSaving = false, savedSuccessfully = true) }
                        AppState.toast("Provider updated!")
                    },
                    onError = { e, _ ->
                        updateState { it.copy(isSaving = false, error = e) }
                        AppState.toast("Failed to update provider", true)
                    }
                )
            }
        }
    }
}
