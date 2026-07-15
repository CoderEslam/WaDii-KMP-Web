package com.teacheronline.pages.teacher.payments

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.PaymentStudentDto
import com.teacheronline.domain.usecase.PaymentUseCase
import com.teacheronline.domain.usecase.TeacherUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeacherPaymentsViewModel(
    private val paymentUseCase: PaymentUseCase,
    private val teacherUseCase: TeacherUseCase
) : BaseViewModel<TeacherPaymentsState, TeacherPaymentsEvent>() {

    override val initialState: TeacherPaymentsState get() = TeacherPaymentsState()

    override val state: StateFlow<TeacherPaymentsState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: TeacherPaymentsEvent) {
        when (event) {
            TeacherPaymentsEvent.Load -> load()
            is TeacherPaymentsEvent.SelectSubject -> updateState {
                it.copy(selectedSubjectId = event.id, studentsInSubject = it.me?.subjects?.find { s -> s.id == event.id }?.students ?: emptyList())
            }
            is TeacherPaymentsEvent.SelectStudent -> updateState { it.copy(selectedStudentId = event.id) }
            is TeacherPaymentsEvent.SetPrice -> updateState { it.copy(price = event.value) }
            is TeacherPaymentsEvent.SetNotes -> updateState { it.copy(notes = event.value) }
            TeacherPaymentsEvent.Submit -> submit()
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
                    me?.subjects?.firstOrNull()?.let { onEvent(TeacherPaymentsEvent.SelectSubject(it.id)) }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun submit() = screenModelScope.launch {
        val s = _state.value
        val teacherId = s.me?.id ?: return@launch
        if (s.selectedStudentId == 0L || s.selectedSubjectId == 0L) return@launch
        val dto = PaymentStudentDto(
            price = s.price.toDoubleOrNull() ?: 0.0,
            notes = s.notes.ifBlank { null },
            studentId = s.selectedStudentId,
            teacherId = teacherId,
            subjectId = s.selectedSubjectId
        )
        paymentUseCase.createStudentPayment(dto) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = {
                    updateState { it.copy(saving = false, price = "", notes = "") }
                    AppState.toast("Payment recorded")
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Failed", true) }
            )
        }
    }
}
