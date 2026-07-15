package com.teacheronline.pages.secretary.dashboard

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.AttendanceRequest
import com.teacheronline.domain.model.PaymentStudentDto
import com.teacheronline.domain.model.StudentRequest
import com.teacheronline.domain.usecase.AttendanceUseCase
import com.teacheronline.domain.usecase.LevelUseCase
import com.teacheronline.domain.usecase.PaymentUseCase
import com.teacheronline.domain.usecase.StudentUseCase
import com.teacheronline.domain.usecase.SubjectUseCase
import com.teacheronline.domain.usecase.TeacherUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SecretaryDashboardViewModel(
    private val studentUseCase: StudentUseCase,
    private val subjectUseCase: SubjectUseCase,
    private val levelUseCase: LevelUseCase,
    private val teacherUseCase: TeacherUseCase,
    private val attendanceUseCase: AttendanceUseCase,
    private val paymentUseCase: PaymentUseCase
) : BaseViewModel<SecretaryDashboardState, SecretaryDashboardEvent>() {

    override val initialState: SecretaryDashboardState get() = SecretaryDashboardState()

    override val state: StateFlow<SecretaryDashboardState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: SecretaryDashboardEvent) {
        when (event) {
            SecretaryDashboardEvent.Load -> load()
            is SecretaryDashboardEvent.SetTab -> updateState { it.copy(tab = event.index) }
            is SecretaryDashboardEvent.SetFirstName -> updateState { it.copy(firstName = event.value) }
            is SecretaryDashboardEvent.SetLastName -> updateState { it.copy(lastName = event.value) }
            is SecretaryDashboardEvent.SetEmail -> updateState { it.copy(email = event.value) }
            is SecretaryDashboardEvent.SetPassword -> updateState { it.copy(password = event.value) }
            is SecretaryDashboardEvent.SetPhone -> updateState { it.copy(phone = event.value) }
            is SecretaryDashboardEvent.SetParentId -> updateState { it.copy(parentId = event.value) }
            is SecretaryDashboardEvent.SetParentContact -> updateState { it.copy(parentContact = event.value) }
            is SecretaryDashboardEvent.SetLevel -> updateState { it.copy(levelId = event.id) }
            is SecretaryDashboardEvent.ToggleSubject -> updateState {
                val ids = it.selectedSubjectIds.toMutableSet()
                if (!ids.add(event.id)) ids.remove(event.id)
                it.copy(selectedSubjectIds = ids)
            }
            SecretaryDashboardEvent.CreateStudent -> createStudent()
            is SecretaryDashboardEvent.DeleteStudent -> deleteStudent(event.id)
            is SecretaryDashboardEvent.SetAttendanceStudent -> updateState { it.copy(attendanceStudentId = event.id) }
            is SecretaryDashboardEvent.SetAttendanceSubject -> updateState { it.copy(attendanceSubjectId = event.id) }
            is SecretaryDashboardEvent.SetAttendanceTeacher -> updateState { it.copy(attendanceTeacherId = event.id) }
            is SecretaryDashboardEvent.SetAttendanceStatus -> updateState { it.copy(attendanceStatus = event.status) }
            SecretaryDashboardEvent.SubmitAttendance -> submitAttendance()
            is SecretaryDashboardEvent.SetPaymentStudent -> updateState { it.copy(paymentStudentId = event.id) }
            is SecretaryDashboardEvent.SetPaymentSubject -> updateState { it.copy(paymentSubjectId = event.id) }
            is SecretaryDashboardEvent.SetPaymentTeacher -> updateState { it.copy(paymentTeacherId = event.id) }
            is SecretaryDashboardEvent.SetPaymentPrice -> updateState { it.copy(paymentPrice = event.value) }
            is SecretaryDashboardEvent.SetPaymentNotes -> updateState { it.copy(paymentNotes = event.value) }
            SecretaryDashboardEvent.SubmitPayment -> submitPayment()
        }
    }

    private fun load() = screenModelScope.launch {
        subjectUseCase.showAll { r -> r.handelState(onSuccess = { data -> updateState { it.copy(subjects = data.data ?: emptyList()) } }) }
        levelUseCase.all { r -> r.handelState(onSuccess = { data -> updateState { it.copy(levels = data.data ?: emptyList()) } }) }
        teacherUseCase.showAll { r -> r.handelState(onSuccess = { data -> updateState { it.copy(teachers = data.data ?: emptyList()) } }) }
        attendanceUseCase.all { r -> r.handelState(onSuccess = { data -> updateState { it.copy(attendanceHistory = data.data ?: emptyList()) } }) }
        studentUseCase.all { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(students = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun createStudent() = screenModelScope.launch {
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
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Failed", true) }
            )
        }
    }

    private fun deleteStudent(id: Long) = screenModelScope.launch {
        studentUseCase.delete(id) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { updateState { it.copy(isLoading = false) }; AppState.toast("Deleted"); load() },
                onError = { e, _ -> updateState { it.copy(isLoading = false, error = e) }; AppState.toast("Failed", true) }
            )
        }
    }

    private fun submitAttendance() = screenModelScope.launch {
        val s = _state.value
        if (s.attendanceStudentId == 0L || s.attendanceSubjectId == 0L || s.attendanceTeacherId == 0L) return@launch
        val request = AttendanceRequest(status = s.attendanceStatus, studentId = s.attendanceStudentId, subjectId = s.attendanceSubjectId, teacherId = s.attendanceTeacherId)
        attendanceUseCase.create(request) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = { data ->
                    val created = data.data
                    updateState { it.copy(saving = false, attendanceHistory = if (created != null) listOf(created) + it.attendanceHistory else it.attendanceHistory) }
                    AppState.toast("Attendance recorded")
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Failed", true) }
            )
        }
    }

    private fun submitPayment() = screenModelScope.launch {
        val s = _state.value
        if (s.paymentStudentId == 0L || s.paymentSubjectId == 0L || s.paymentTeacherId == 0L) return@launch
        val dto = PaymentStudentDto(
            price = s.paymentPrice.toDoubleOrNull() ?: 0.0,
            notes = s.paymentNotes.ifBlank { null },
            studentId = s.paymentStudentId,
            teacherId = s.paymentTeacherId,
            subjectId = s.paymentSubjectId
        )
        paymentUseCase.createStudentPayment(dto) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = {
                    updateState { it.copy(saving = false, paymentPrice = "", paymentNotes = "") }
                    AppState.toast("Payment recorded")
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Failed", true) }
            )
        }
    }
}
