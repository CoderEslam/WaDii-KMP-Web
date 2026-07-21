package com.teacheronline.pages.teacher.dashboard

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.usecase.TeacherUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeacherDashboardViewModel(
    private val teacherUseCase: TeacherUseCase
) : BaseViewModel<TeacherDashboardState, TeacherDashboardEvent>() {

    override val initialState: TeacherDashboardState get() = TeacherDashboardState()

    override val state: StateFlow<TeacherDashboardState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: TeacherDashboardEvent) {}

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
    }
}
