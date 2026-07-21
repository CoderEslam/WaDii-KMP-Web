package com.teacheronline.pages.admin.payments

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.PaymentExtraDto
import com.teacheronline.domain.model.PaymentOutDto
import com.teacheronline.domain.model.PaymentStudentDto
import com.teacheronline.domain.usecase.PaymentUseCase
import com.teacheronline.domain.usecase.StudentUseCase
import com.teacheronline.domain.usecase.TeacherUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaymentsViewModel(
    private val paymentUseCase: PaymentUseCase,
    private val studentUseCase: StudentUseCase,
    private val teacherUseCase: TeacherUseCase
) : BaseViewModel<PaymentsState, PaymentsEvent>() {

    override val initialState: PaymentsState get() = PaymentsState()

    override val state: StateFlow<PaymentsState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: PaymentsEvent) {
        when (event) {
            PaymentsEvent.Load -> load()
            is PaymentsEvent.SetTab -> updateState { it.copy(tab = event.index) }
            is PaymentsEvent.SetExtraPrice -> updateState { it.copy(extraPrice = event.value) }
            is PaymentsEvent.SetExtraNotes -> updateState { it.copy(extraNotes = event.value) }
            PaymentsEvent.CreateExtra -> createExtra()
            is PaymentsEvent.SetOutPrice -> updateState { it.copy(outPrice = event.value) }
            is PaymentsEvent.SetOutNotes -> updateState { it.copy(outNotes = event.value) }
            PaymentsEvent.CreateOut -> createOut()
            is PaymentsEvent.SetStudent -> updateState { it.copy(studentId = event.id, subjectId = 0) }
            is PaymentsEvent.SetTeacher -> updateState { it.copy(teacherId = event.id) }
            is PaymentsEvent.SetSubject -> updateState { it.copy(subjectId = event.id) }
            is PaymentsEvent.SetStudentPrice -> updateState { it.copy(studentPrice = event.value) }
            is PaymentsEvent.SetStudentNotes -> updateState { it.copy(studentNotes = event.value) }
            PaymentsEvent.CreateStudentPayment -> createStudentPayment()
            PaymentsEvent.LookupStudentPayments -> lookupStudentPayments()
        }
    }

    private fun load() = screenModelScope.launch {
        studentUseCase.all { r -> r.handelState(onSuccess = { data -> updateState { it.copy(students = data.data ?: emptyList()) } }) }
        teacherUseCase.showAll { r -> r.handelState(onSuccess = { data -> updateState { it.copy(teachers = data.data ?: emptyList()) } }) }
    }

    private fun createExtra() = screenModelScope.launch {
        val s = _state.value
        paymentUseCase.createExtra(PaymentExtraDto(price = s.extraPrice.toDoubleOrNull() ?: 0.0, notes = s.extraNotes.ifBlank { null })) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = { updateState { it.copy(saving = false, extraPrice = "", extraNotes = "") }; AppState.toast("Extra payment recorded") },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Failed", true) }
            )
        }
    }

    private fun createOut() = screenModelScope.launch {
        val s = _state.value
        paymentUseCase.createOut(PaymentOutDto(price = s.outPrice.toDoubleOrNull() ?: 0.0, notes = s.outNotes.ifBlank { null })) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = { updateState { it.copy(saving = false, outPrice = "", outNotes = "") }; AppState.toast("Outgoing payment recorded") },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Failed", true) }
            )
        }
    }

    private fun createStudentPayment() = screenModelScope.launch {
        val s = _state.value
        val dto = PaymentStudentDto(
            price = s.studentPrice.toDoubleOrNull() ?: 0.0,
            notes = s.studentNotes.ifBlank { null },
            studentId = s.studentId,
            teacherId = s.teacherId,
            subjectId = s.subjectId
        )
        paymentUseCase.createStudentPayment(dto) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = {
                    updateState { it.copy(saving = false, studentPrice = "", studentNotes = "") }
                    AppState.toast("Student payment recorded")
                    lookupStudentPayments()
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Failed", true) }
            )
        }
    }

    private fun lookupStudentPayments() = screenModelScope.launch {
        val studentId = _state.value.studentId
        if (studentId == 0L) return@launch
        paymentUseCase.allForStudent(studentId) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(lookedUpPayments = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }
}
