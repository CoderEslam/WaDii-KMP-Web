package com.teacheronline.pages.admin.secretaries

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.SecretaryDto
import com.teacheronline.domain.usecase.SecretaryUseCase
import com.teacheronline.domain.usecase.TeacherUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SecretariesViewModel(
    private val secretaryUseCase: SecretaryUseCase,
    private val teacherUseCase: TeacherUseCase
) : BaseViewModel<SecretariesState, SecretariesEvent>() {

    override val initialState: SecretariesState get() = SecretariesState()

    override val state: StateFlow<SecretariesState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: SecretariesEvent) {
        when (event) {
            SecretariesEvent.Load -> load()
            is SecretariesEvent.SetFirstName -> updateState { it.copy(firstName = event.value) }
            is SecretariesEvent.SetLastName -> updateState { it.copy(lastName = event.value) }
            is SecretariesEvent.SetEmail -> updateState { it.copy(email = event.value) }
            is SecretariesEvent.SetPassword -> updateState { it.copy(password = event.value) }
            is SecretariesEvent.SetPhone -> updateState { it.copy(phone = event.value) }
            is SecretariesEvent.SetTeacher -> updateState { it.copy(teacherId = event.id) }
            SecretariesEvent.Create -> create()
        }
    }

    private fun load() = screenModelScope.launch {
        teacherUseCase.showAll { r -> r.handelState(onSuccess = { data -> updateState { it.copy(teachers = data.data ?: emptyList()) } }) }
        secretaryUseCase.showAll { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(secretaries = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun create() = screenModelScope.launch {
        val s = _state.value
        val dto = SecretaryDto(
            firstName = s.firstName, lastName = s.lastName, email = s.email,
            password = s.password, fcmToken = "", phone = s.phone, teacherId = s.teacherId
        )
        secretaryUseCase.create(dto) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = {
                    updateState { it.copy(saving = false, firstName = "", lastName = "", email = "", password = "", phone = "") }
                    AppState.toast("Secretary created")
                    load()
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Create failed", true) }
            )
        }
    }
}
