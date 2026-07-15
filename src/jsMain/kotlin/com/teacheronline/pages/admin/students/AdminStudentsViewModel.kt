package com.teacheronline.pages.admin.students

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.StudentRequest
import com.teacheronline.domain.usecase.LevelUseCase
import com.teacheronline.domain.usecase.StudentUseCase
import com.teacheronline.domain.usecase.SubjectUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminStudentsViewModel(
    private val studentUseCase: StudentUseCase,
    private val subjectUseCase: SubjectUseCase,
    private val levelUseCase: LevelUseCase
) : BaseViewModel<AdminStudentsState, AdminStudentsEvent>() {

    override val initialState: AdminStudentsState get() = AdminStudentsState()

    override val state: StateFlow<AdminStudentsState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: AdminStudentsEvent) {
        when (event) {
            AdminStudentsEvent.Load -> load()
            is AdminStudentsEvent.SetFirstName -> updateState { it.copy(firstName = event.value) }
            is AdminStudentsEvent.SetLastName -> updateState { it.copy(lastName = event.value) }
            is AdminStudentsEvent.SetEmail -> updateState { it.copy(email = event.value) }
            is AdminStudentsEvent.SetPassword -> updateState { it.copy(password = event.value) }
            is AdminStudentsEvent.SetPhone -> updateState { it.copy(phone = event.value) }
            is AdminStudentsEvent.SetParentId -> updateState { it.copy(parentId = event.value) }
            is AdminStudentsEvent.SetParentContact -> updateState { it.copy(parentContact = event.value) }
            is AdminStudentsEvent.SetLevel -> updateState { it.copy(levelId = event.id) }
            is AdminStudentsEvent.ToggleSubject -> updateState {
                val ids = it.selectedSubjectIds.toMutableSet()
                if (!ids.add(event.id)) ids.remove(event.id)
                it.copy(selectedSubjectIds = ids)
            }
            AdminStudentsEvent.Create -> create()
            is AdminStudentsEvent.Delete -> delete(event.id)
        }
    }

    private fun load() = screenModelScope.launch {
        subjectUseCase.showAll { r -> r.handelState(onSuccess = { data -> updateState { it.copy(subjects = data.data ?: emptyList()) } }) }
        levelUseCase.all { r -> r.handelState(onSuccess = { data -> updateState { it.copy(levels = data.data ?: emptyList()) } }) }
        studentUseCase.all { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(students = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun create() = screenModelScope.launch {
        val s = _state.value
        val request = StudentRequest(
            parentId = s.parentId.toLongOrNull() ?: 0,
            parentContact = s.parentContact,
            phone = s.phone,
            subjectIds = s.selectedSubjectIds,
            scheduleIds = emptySet(),
            levelId = s.levelId,
            firstName = s.firstName,
            lastName = s.lastName,
            email = s.email,
            password = s.password,
            fcmToken = ""
        )
        studentUseCase.create(request) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = {
                    updateState {
                        it.copy(
                            saving = false, firstName = "", lastName = "", email = "", password = "", phone = "",
                            parentId = "", parentContact = "", levelId = 0, selectedSubjectIds = emptySet()
                        )
                    }
                    AppState.toast("Student created")
                    load()
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Create failed", true) }
            )
        }
    }

    private fun delete(id: Long) = screenModelScope.launch {
        studentUseCase.delete(id) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { updateState { it.copy(isLoading = false) }; AppState.toast("Deleted"); load() },
                onError = { e, _ -> updateState { it.copy(isLoading = false, error = e) }; AppState.toast("Delete failed", true) }
            )
        }
    }
}
