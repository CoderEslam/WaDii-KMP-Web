package com.teacheronline.pages.admin.teachers

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.CreateSubjectsTeacher
import com.teacheronline.domain.usecase.SubjectUseCase
import com.teacheronline.domain.usecase.TeacherUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeachersViewModel(
    private val teacherUseCase: TeacherUseCase,
    private val subjectUseCase: SubjectUseCase
) : BaseViewModel<TeachersState, TeachersEvent>() {

    override val initialState: TeachersState get() = TeachersState()

    override val state: StateFlow<TeachersState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: TeachersEvent) {
        when (event) {
            TeachersEvent.Load -> load()
            is TeachersEvent.SetFirstName -> updateState { it.copy(firstName = event.value) }
            is TeachersEvent.SetLastName -> updateState { it.copy(lastName = event.value) }
            is TeachersEvent.SetEmail -> updateState { it.copy(email = event.value) }
            is TeachersEvent.SetPassword -> updateState { it.copy(password = event.value) }
            is TeachersEvent.SetPhone -> updateState { it.copy(phone = event.value) }
            is TeachersEvent.ToggleSubject -> updateState {
                val ids = it.selectedSubjectIds.toMutableSet()
                if (!ids.add(event.id)) ids.remove(event.id)
                it.copy(selectedSubjectIds = ids)
            }
            is TeachersEvent.StartEdit -> updateState {
                it.copy(
                    editingId = event.teacher.id,
                    firstName = event.teacher.user.firstName,
                    lastName = event.teacher.user.lastName,
                    email = event.teacher.user.email,
                    password = "",
                    phone = event.teacher.phone,
                    selectedSubjectIds = event.teacher.subjects.map { s -> s.id }.toSet()
                )
            }
            TeachersEvent.CancelEdit -> updateState {
                it.copy(editingId = 0, firstName = "", lastName = "", email = "", password = "", phone = "", selectedSubjectIds = emptySet())
            }
            TeachersEvent.Save -> save()
            is TeachersEvent.Delete -> delete(event.id)
        }
    }

    private fun load() = screenModelScope.launch {
        subjectUseCase.showAll { r ->
            r.handelState(onSuccess = { data -> updateState { it.copy(subjects = data.data ?: emptyList()) } })
        }
        teacherUseCase.showAll { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(teachers = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun save() = screenModelScope.launch {
        val s = _state.value
        val request = CreateSubjectsTeacher(
            id = s.editingId.takeIf { it != 0L },
            firstName = s.firstName,
            lastName = s.lastName,
            email = s.email.takeIf { it.isNotBlank() },
            password = s.password.takeIf { it.isNotBlank() },
            phone = s.phone,
            subjectIds = s.selectedSubjectIds
        )
        val onResult: (com.teacheronline.utils.RequestState<com.teacheronline.domain.model.BaseResponse<com.teacheronline.domain.model.Teacher>>) -> Unit = { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = {
                    updateState { it.copy(saving = false, editingId = 0, firstName = "", lastName = "", email = "", password = "", phone = "", selectedSubjectIds = emptySet()) }
                    AppState.toast(if (s.editingId != 0L) "Teacher updated" else "Teacher added")
                    load()
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Save failed", true) }
            )
        }
        if (s.editingId != 0L) teacherUseCase.update(request, onResult) else teacherUseCase.insert(request, onResult)
    }

    private fun delete(id: Long) = screenModelScope.launch {
        teacherUseCase.delete(id) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { updateState { it.copy(isLoading = false) }; AppState.toast("Deleted"); load() },
                onError = { e, _ -> updateState { it.copy(isLoading = false, error = e) }; AppState.toast("Delete failed", true) }
            )
        }
    }
}
