package com.teacheronline.pages.student.dashboard

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.usecase.AttendanceUseCase
import com.teacheronline.domain.usecase.PaymentUseCase
import com.teacheronline.domain.usecase.StudentUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentDashboardViewModel(
    private val studentUseCase: StudentUseCase,
    private val paymentUseCase: PaymentUseCase,
    private val attendanceUseCase: AttendanceUseCase
) : BaseViewModel<StudentDashboardState, StudentDashboardEvent>() {

    override val initialState: StudentDashboardState get() = StudentDashboardState()

    override val state: StateFlow<StudentDashboardState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: StudentDashboardEvent) {}

    private fun load() = screenModelScope.launch {
        val email = AppState.user?.email ?: return@launch
        studentUseCase.byEmail(email) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    val me = data.data
                    updateState { it.copy(me = me, isLoading = false) }
                    if (me != null) {
                        screenModelScope.launch {
                            paymentUseCase.subjectsStatusList(me.id) { pr ->
                                pr.handelState(onSuccess = { pd -> updateState { it.copy(subjectsStatus = pd.data ?: emptyList()) } })
                            }
                            attendanceUseCase.all { ar ->
                                ar.handelState(onSuccess = { ad ->
                                    updateState { it.copy(attendanceHistory = (ad.data ?: emptyList()).filter { a -> a.student.id == me.id }) }
                                })
                            }
                        }
                    }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }
}
