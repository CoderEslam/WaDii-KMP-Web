package com.wadii.pages.shared.profile

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.data.firebase.FcmService
import com.wadii.domain.model.auth.Role
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.provider.BranchRequest
import com.wadii.domain.model.provider.EditableBranch
import com.wadii.domain.model.provider.EditableLink
import com.wadii.domain.model.provider.EditableWorkTime
import com.wadii.domain.model.provider.OfferRequest
import com.wadii.domain.model.provider.UpdateProviderRequest
import com.wadii.domain.model.provider.WorkTimeRequest
import com.wadii.domain.model.user.UpdateUser
import com.wadii.domain.usecase.CountryUseCase
import com.wadii.domain.usecase.ProviderUseCase
import com.wadii.domain.usecase.UserUseCase
import com.wadii.state.AppState
import com.wadii.viewmodel.ServicesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userUseCase: UserUseCase,
    private val providerUseCase: ProviderUseCase,
    private val countryUseCase: CountryUseCase,
    private val servicesUseCase: ServicesUseCase
) : BaseViewModel<EditProfileState, EditProfileEvent>() {

    override val initialState: EditProfileState get() = EditProfileState()

    override val state: StateFlow<EditProfileState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: EditProfileEvent) {
        when (event) {
            EditProfileEvent.Load -> load()
            is EditProfileEvent.SetFirstName -> updateState { it.copy(firstName = event.value) }
            is EditProfileEvent.SetLastName -> updateState { it.copy(lastName = event.value) }
            is EditProfileEvent.SetEmail -> updateState { it.copy(email = event.value) }
            is EditProfileEvent.SetPhone -> updateState { it.copy(phone = event.value) }

            is EditProfileEvent.SelectCountry -> loadProvinces(event.id, resetChildren = true)
            is EditProfileEvent.SelectProvince -> loadCities(event.id, resetChildren = true)
            is EditProfileEvent.SelectCity -> updateState { it.copy(selectedCity = event.id) }

            is EditProfileEvent.ToggleService -> updateState {
                val ids = it.selectedServiceIds
                it.copy(selectedServiceIds = if (event.serviceId in ids) ids - event.serviceId else ids + event.serviceId)
            }

            EditProfileEvent.AddBranch -> updateState {
                it.copy(branches = it.branches + EditableBranch(id = 0, name = "", address = "", workTimes = emptyList()))
            }
            is EditProfileEvent.RemoveBranch -> updateState {
                it.copy(branches = it.branches.filterIndexed { i, _ -> i != event.branchIndex })
            }
            is EditProfileEvent.SetBranchName -> updateBranch(event.branchIndex) { it.copy(name = event.value) }
            is EditProfileEvent.SetBranchAddress -> updateBranch(event.branchIndex) { it.copy(address = event.value) }

            is EditProfileEvent.AddWorkTime -> updateBranch(event.branchIndex) { branch ->
                branch.copy(workTimes = branch.workTimes + EditableWorkTime(id = 0, day = "", startTime = "", closeTime = ""))
            }
            is EditProfileEvent.RemoveWorkTime -> updateBranch(event.branchIndex) { branch ->
                branch.copy(workTimes = branch.workTimes.filterIndexed { i, _ -> i != event.workTimeIndex })
            }
            is EditProfileEvent.SetWorkTimeDay -> updateWorkTime(event.branchIndex, event.workTimeIndex) { it.copy(day = event.value) }
            is EditProfileEvent.SetWorkTimeStart -> updateWorkTime(event.branchIndex, event.workTimeIndex) { it.copy(startTime = event.value) }
            is EditProfileEvent.SetWorkTimeClose -> updateWorkTime(event.branchIndex, event.workTimeIndex) { it.copy(closeTime = event.value) }

            EditProfileEvent.AddLink -> updateState { it.copy(links = it.links + EditableLink(id = 0, link = "")) }
            is EditProfileEvent.RemoveLink -> updateState {
                it.copy(links = it.links.filterIndexed { i, _ -> i != event.linkIndex })
            }
            is EditProfileEvent.SetLink -> updateState {
                it.copy(links = it.links.mapIndexed { i, l -> if (i == event.linkIndex) EditableLink(l.id, event.value) else l })
            }

            EditProfileEvent.Submit -> submit()
        }
    }

    private fun updateBranch(branchIndex: Int, reducer: (EditableBranch) -> EditableBranch) = updateState {
        it.copy(branches = it.branches.mapIndexed { i, b -> if (i == branchIndex) reducer(b) else b })
    }

    private fun updateWorkTime(branchIndex: Int, workTimeIndex: Int, reducer: (EditableWorkTime) -> EditableWorkTime) =
        updateBranch(branchIndex) { branch ->
            branch.copy(workTimes = branch.workTimes.mapIndexed { i, wt -> if (i == workTimeIndex) reducer(wt) else wt })
        }

    private fun load() = screenModelScope.launch {
        updateState { it.copy(loading = true) }
        val cached = AppState.user
        if (cached != null) {
            seedFromUser(cached)
        } else {
            userUseCase.userMe { r ->
                r.handelState(
                    onSuccess = { data -> data.data?.let { screenModelScope.launch { seedFromUser(it) } } },
                    onError = { e, _ -> updateState { it.copy(error = e, loading = false) } }
                )
            }
        }
    }

    private suspend fun seedFromUser(user: User) {
        val provider = user.provider

        updateState {
            it.copy(
                originalUser = user,
                role = user.role,
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email,
                phone = user.phone,
                shopName = provider?.name ?: "",
                selectedCountry = user.city?.province?.country?.id ?: 0,
                selectedProvince = user.city?.province?.id ?: 0,
                selectedCity = user.city?.id ?: 0,
                selectedServiceIds = provider?.services?.map { s -> s.id }?.toSet() ?: emptySet(),
                branches = provider?.branches?.map { b ->
                    EditableBranch(
                        id = b.id,
                        name = b.name,
                        address = b.address,
                        workTimes = b.workTimes.map { wt -> EditableWorkTime(wt.id, wt.day, wt.startTime, wt.closeTime) }
                    )
                } ?: emptyList(),
                links = provider?.links?.map { l -> EditableLink(l.id, l.link) } ?: emptyList(),
                offersPassthrough = provider?.offers?.map { o -> OfferRequest(o.id, o.title, o.description, o.endDate) } ?: emptyList()
            )
        }

        loadCountries()
        val countryId = user.city?.province?.country?.id ?: 0
        val provinceId = user.city?.province?.id ?: 0
        if (countryId != 0L) loadProvinces(countryId, resetChildren = false)
        if (provinceId != 0L) loadCities(provinceId, resetChildren = false)
        if (user.role == "PROVIDER") loadServices()

        updateState { it.copy(loading = false) }
    }

    private fun loadCountries() {
        screenModelScope.launch {
            countryUseCase.getCountryList { response ->
                response.handelState(
                    onSuccess = { data -> updateState { it.copy(countries = data.data ?: emptyList()) } }
                )
            }
        }
    }

    private fun loadProvinces(countryId: Long, resetChildren: Boolean) {
        if (resetChildren) {
            updateState {
                it.copy(selectedCountry = countryId, provinces = emptyList(), cities = emptyList(), selectedProvince = 0, selectedCity = 0)
            }
        } else {
            updateState { it.copy(selectedCountry = countryId) }
        }
        screenModelScope.launch {
            countryUseCase.getProvinceByCountryId(countryId) { response ->
                response.handelState(
                    onSuccess = { data -> updateState { it.copy(provinces = data.data ?: emptyList()) } }
                )
            }
        }
    }

    private fun loadCities(provinceId: Long, resetChildren: Boolean) {
        if (resetChildren) {
            updateState { it.copy(selectedProvince = provinceId, cities = emptyList(), selectedCity = 0) }
        } else {
            updateState { it.copy(selectedProvince = provinceId) }
        }
        screenModelScope.launch {
            countryUseCase.getCitiesByProvinceId(provinceId) { response ->
                response.handelState(
                    onSuccess = { data -> updateState { it.copy(cities = data.data ?: emptyList()) } }
                )
            }
        }
    }

    private fun loadServices() {
        screenModelScope.launch {
            servicesUseCase.getServiceList { response ->
                response.handelState(
                    onSuccess = { data -> updateState { it.copy(allServices = data.data ?: emptyList()) } }
                )
            }
        }
    }

    private fun submit() {
        val s = _state.value
        val user = s.originalUser
        if (user == null || s.firstName.isBlank() || s.lastName.isBlank() || s.email.isBlank() || s.selectedCity == 0L) {
            AppState.toast("Please fill in all required fields", true)
            return
        }

        screenModelScope.launch {
            updateState { it.copy(saving = true, error = null) }
            val fcmToken = FcmService.fetchToken().orEmpty()
            userUseCase.updateUser(
                UpdateUser(
                    id = user.id,
                    firstName = s.firstName,
                    lastName = s.lastName,
                    email = s.email,
                    password = "",
                    fcmToken = fcmToken,
                    phone = s.phone,
                    userType = Role.valueOf(s.role).userType,
                    cityId = s.selectedCity
                )
            ) { r ->
                r.handelState(
                    onSuccess = {
                        if (s.role == "PROVIDER") submitProvider(s) else finish()
                    },
                    onError = { e, _ ->
                        updateState { it.copy(saving = false, error = e) }
                        AppState.toast("Failed to update profile", true)
                    }
                )
            }
        }
    }

    private fun submitProvider(s: EditProfileState) {
        val providerId = s.originalUser?.provider?.id ?: return finish()
        screenModelScope.launch {
            providerUseCase.updateProvider(
                UpdateProviderRequest(
                    id = providerId,
                    firstName = s.firstName,
                    lastName = s.lastName,
                    phone = s.phone,
                    email = s.email,
                    serviceIds = s.selectedServiceIds.toList(),
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
                    onSuccess = { finish() },
                    onError = { e, _ ->
                        updateState { it.copy(saving = false, error = e) }
                        AppState.toast("Failed to update shop info", true)
                    }
                )
            }
        }
    }

    private fun finish() {
        screenModelScope.launch {
            userUseCase.userMe { r ->
                r.handelState(
                    onSuccess = { data ->
                        data.data?.let { AppState.saveUser(it, AppState.token ?: "") }
                        updateState { it.copy(saving = false, savedSuccessfully = true) }
                        AppState.toast("Profile updated!")
                    },
                    onError = { _, _ ->
                        updateState { it.copy(saving = false, savedSuccessfully = true) }
                        AppState.toast("Profile updated!")
                    }
                )
            }
        }
    }
}
