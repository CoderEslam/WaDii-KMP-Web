package com.teacheronline.pages.parent.dashboard

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.usecase.PaymentUseCase
import com.teacheronline.domain.usecase.StudentUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// The API has no "students by parent" endpoint (doc gap) — children are found by filtering
// all-students client-side where student.parentId matches the logged-in parent's own user id.
class ParentDashboardViewModel(
    private val studentUseCase: StudentUseCase,
    private val paymentUseCase: PaymentUseCase
) : BaseViewModel<ParentDashboardState, ParentDashboardEvent>() {

    override val initialState: ParentDashboardState get() = ParentDashboardState()

    override val state: StateFlow<ParentDashboardState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ParentDashboardEvent) {}

    private fun load() = screenModelScope.launch {
        val myUserId = AppState.user?.id ?: return@launch
        studentUseCase.all { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    val children = (data.data ?: emptyList()).filter { it.parentId == myUserId }
                    updateState { it.copy(children = children, isLoading = false) }
                    screenModelScope.launch {
                        children.forEach { child ->
                            paymentUseCase.subjectsStatusList(child.id) { pr ->
                                pr.handelState(onSuccess = { pd ->
                                    updateState { it.copy(statusByStudentId = it.statusByStudentId + (child.id to (pd.data ?: emptyList()))) }
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
