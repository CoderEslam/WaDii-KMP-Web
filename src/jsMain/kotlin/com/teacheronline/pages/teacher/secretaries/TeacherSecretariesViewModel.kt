package com.teacheronline.pages.teacher.secretaries

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

class TeacherSecretariesViewModel(
    private val secretaryUseCase: SecretaryUseCase,
    private val teacherUseCase: TeacherUseCase
) : BaseViewModel<TeacherSecretariesState, TeacherSecretariesEvent>() {

    override val initialState: TeacherSecretariesState get() = TeacherSecretariesState()

    override val state: StateFlow<TeacherSecretariesState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: TeacherSecretariesEvent) {
        when (event) {
            TeacherSecretariesEvent.Load -> load()
            is TeacherSecretariesEvent.SetFirstName -> updateState { it.copy(firstName = event.value) }
            is TeacherSecretariesEvent.SetLastName -> updateState { it.copy(lastName = event.value) }
            is TeacherSecretariesEvent.SetEmail -> updateState { it.copy(email = event.value) }
            is TeacherSecretariesEvent.SetPassword -> updateState { it.copy(password = event.value) }
            is TeacherSecretariesEvent.SetPhone -> updateState { it.copy(phone = event.value) }
            TeacherSecretariesEvent.Create -> create()
        }
    }

    private fun load() = screenModelScope.launch {
        val myUserId = AppState.user?.id
        teacherUseCase.showAll { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    val me = data.data?.find { it.user.id == myUserId }
                    updateState { it.copy(me = me, isLoading = false) }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
        secretaryUseCase.showAll { r ->
            r.handelState(onSuccess = { data ->
                val myUserIdVal = AppState.user?.id
                updateState { it.copy(mySecretaries = (data.data ?: emptyList()).filter { s -> s.teacher.user.id == myUserIdVal }) }
            })
        }
    }

    private fun create() = screenModelScope.launch {
        val s = _state.value
        val teacherId = s.me?.id ?: return@launch
        val dto = SecretaryDto(firstName = s.firstName, lastName = s.lastName, email = s.email, password = s.password, fcmToken = "", phone = s.phone, teacherId = teacherId)
        secretaryUseCase.create(dto) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = {
                    updateState { it.copy(saving = false, firstName = "", lastName = "", email = "", password = "", phone = "") }
                    AppState.toast("Secretary created")
                    load()
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Failed", true) }
            )
        }
    }
}
