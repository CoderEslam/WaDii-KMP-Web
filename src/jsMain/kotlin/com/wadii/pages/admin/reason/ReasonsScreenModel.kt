package com.wadii.pages.admin.reason

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.order.CancelReason
import com.wadii.domain.model.order.InsertReason
import com.wadii.state.AppState
import com.wadii.viewmodel.ReasonsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReasonsViewModel(
    private val reasonsUseCase: ReasonsUseCase
) : BaseViewModel<ReasonsState, ReasonsEvent>() {

    override val initialState: ReasonsState get() = ReasonsState()

    override val state: StateFlow<ReasonsState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ReasonsEvent) {
        when (event) {
            ReasonsEvent.Load -> load()
            is ReasonsEvent.StartEdit -> updateState {
                it.copy(
                    editingId = event.reason.id,
                    editReason = event.reason.reason
                )
            }

            ReasonsEvent.CancelEdit -> updateState { it.copy(editingId = 0, editReason = "") }
            is ReasonsEvent.SetEditReason -> updateState { it.copy(editReason = event.reason) }
            is ReasonsEvent.SaveEdit -> saveEdit(event.reason)
            is ReasonsEvent.Delete -> delete(event.reasonId)
            is ReasonsEvent.SetNewReason -> updateState { it.copy(newReason = event.reason) }
            ReasonsEvent.Add -> add()
        }
    }

    private fun load() = screenModelScope.launch {
        reasonsUseCase.getReasonList { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            reasons = data.data,
                            isLoading = false
                        )
                    }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun saveEdit(reason: CancelReason) = screenModelScope.launch {
        reasonsUseCase.updateReason(
            insertReason = InsertReason(
                id = reason.id,
                reason = _state.value.editReason
            )
        ) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(saving = true) }
            }, onSuccess = {
                updateState { it.copy(saving = false, editingId = 0, editReason = "") }
                AppState.toast("Updated!")
                load()
            }, onError = { error, code ->
                updateState { it.copy(error = error, saving = false) }
                AppState.toast("Failed to update", true)
            })
        }
    }

    private fun delete(id: Int) = screenModelScope.launch {
        reasonsUseCase.deleteReason(id) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(isLoading = true) }
            }, onSuccess = {
                updateState { it.copy(isLoading = false) }
                AppState.toast("Deleted")
                load()
            }, onError = { error, code ->
                updateState { it.copy(error = error, isLoading = false) }
                AppState.toast("Failed to delete", true)
            })
        }
    }

    private fun add() = screenModelScope.launch {
        reasonsUseCase.addReason(insertReason = InsertReason(reason = _state.value.newReason)) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(adding = true) }
            }, onSuccess = {
                updateState { it.copy(newReason = "", adding = false) }
                AppState.toast("Reason added!")
                load()
            }, onError = { error, code ->
                updateState { it.copy(error = error, adding = false) }
                AppState.toast("Failed to add", true)
            })
        }
    }

}
