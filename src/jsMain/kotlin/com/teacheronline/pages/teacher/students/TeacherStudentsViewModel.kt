package com.teacheronline.pages.teacher.students

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.usecase.StudentUseCase
import com.teacheronline.domain.usecase.TeacherUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeacherStudentsViewModel(
    private val teacherUseCase: TeacherUseCase,
    private val studentUseCase: StudentUseCase
) : BaseViewModel<TeacherStudentsState, TeacherStudentsEvent>() {

    override val initialState: TeacherStudentsState get() = TeacherStudentsState()

    override val state: StateFlow<TeacherStudentsState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: TeacherStudentsEvent) {
        when (event) {
            TeacherStudentsEvent.Load -> load()
            is TeacherStudentsEvent.SelectSubject -> {
                updateState { it.copy(selectedSubjectId = event.id) }
                loadStudentsForSubject(event.id)
            }
        }
    }

    private fun load() = screenModelScope.launch {
        val myUserId = AppState.user?.id
        teacherUseCase.showAll { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    val me = data.data?.find { it.user.id == myUserId }
                    val subjects = me?.subjects ?: emptyList()
                    updateState { it.copy(mySubjects = subjects, isLoading = false) }
                    subjects.firstOrNull()?.let { onEvent(TeacherStudentsEvent.SelectSubject(it.id)) }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun loadStudentsForSubject(subjectId: Long) = screenModelScope.launch {
        studentUseCase.byCourse(subjectId) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(students = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }
}
