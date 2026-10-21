package com.wadii.pages.admin.catalog

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.shop.NameRequest
import com.wadii.domain.usecase.MarketplaceUseCase
import com.wadii.domain.usecase.OrderUseCase
import com.wadii.state.AppState
import com.wadii.utils.RequestValidators
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Admin-managed lists that are just `{ id, name }`: car makes and common spare parts. */
enum class CatalogKind(val title: String, val singular: String, val icon: String, val hint: String) {
    CarTypes("Car Types", "car type", "🚗", "Makes buyers can pick from, e.g. Toyota"),
    SpareParts("Common Spare Parts", "spare part", "🔩", "Suggested parts shown on the New Order form")
}

data class CatalogItem(val id: Long, val name: String)

data class CatalogState(
    val items: List<CatalogItem> = emptyList(),
    val newName: String = "",
    val editingId: Long = 0,
    val editName: String = "",
    val isLoading: Boolean = false,
    val busy: Boolean = false,
    val error: String? = null,
    /** Keyed "new" or "edit": whichever box the admin just submitted. */
    val fieldErrors: Map<String, String> = emptyMap()
)

sealed class CatalogEvent {
    data class SetNew(val value: String) : CatalogEvent()
    object Add : CatalogEvent()
    data class StartEdit(val item: CatalogItem) : CatalogEvent()
    data class SetEdit(val value: String) : CatalogEvent()
    object CancelEdit : CatalogEvent()
    object SaveEdit : CatalogEvent()
    data class Delete(val item: CatalogItem) : CatalogEvent()
}

class CatalogViewModel(
    private val kind: CatalogKind,
    private val orderUseCase: OrderUseCase,
    private val marketplaceUseCase: MarketplaceUseCase
) : BaseViewModel<CatalogState, CatalogEvent>() {

    override val initialState: CatalogState get() = CatalogState()

    override val state: StateFlow<CatalogState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: CatalogEvent) {
        when (event) {
            is CatalogEvent.SetNew -> updateState { it.copy(newName = event.value, fieldErrors = it.fieldErrors - "new") }
            CatalogEvent.Add -> add()
            is CatalogEvent.StartEdit -> updateState { it.copy(editingId = event.item.id, editName = event.item.name, fieldErrors = emptyMap()) }
            is CatalogEvent.SetEdit -> updateState { it.copy(editName = event.value, fieldErrors = it.fieldErrors - "edit") }
            CatalogEvent.CancelEdit -> updateState { it.copy(editingId = 0, editName = "", fieldErrors = emptyMap()) }
            CatalogEvent.SaveEdit -> saveEdit()
            is CatalogEvent.Delete -> delete(event.item)
        }
    }

    private fun load() = screenModelScope.launch {
        updateState { it.copy(isLoading = true) }
        when (kind) {
            CatalogKind.CarTypes -> orderUseCase.getCarTypeList { r ->
                r.handelState(
                    onSuccess = { d -> updateState { it.copy(items = d.data.map { c -> CatalogItem(c.id.toLong(), c.name) }, isLoading = false, error = null) } },
                    onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
                )
            }
            CatalogKind.SpareParts -> marketplaceUseCase.getCommonSpareParts { r ->
                r.handelState(
                    onSuccess = { d -> updateState { it.copy(items = d.data.map { c -> CatalogItem(c.id, c.name) }, isLoading = false, error = null) } },
                    onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
                )
            }
        }
    }

    private fun done(message: String) {
        AppState.toast(message)
        updateState { it.copy(busy = false, newName = "", editingId = 0, editName = "", fieldErrors = emptyMap()) }
        load()
    }

    private fun failed(e: String, fallback: String) {
        AppState.toast(e.ifBlank { fallback }, true)
        updateState { it.copy(busy = false) }
    }

    private val label get() = kind.singular.replaceFirstChar { it.uppercase() } + " name"

    private fun add() = screenModelScope.launch {
        val s = _state.value
        val errors = RequestValidators.name(s.newName, label)
        updateState { it.copy(fieldErrors = if (errors.isEmpty()) emptyMap() else mapOf("new" to errors.values.first())) }
        if (errors.isNotEmpty() || s.busy) return@launch
        updateState { it.copy(busy = true) }
        val request = NameRequest(name = s.newName.trim())
        when (kind) {
            CatalogKind.CarTypes -> marketplaceUseCase.insertCarType(request) { r ->
                r.handelState(onSuccess = { done("Car type added!") }, onError = { e, _ -> failed(e, "Couldn't add the car type") })
            }
            CatalogKind.SpareParts -> marketplaceUseCase.insertCommonSparePart(request) { r ->
                r.handelState(onSuccess = { done("Spare part added!") }, onError = { e, _ -> failed(e, "Couldn't add the spare part") })
            }
        }
    }

    private fun saveEdit() = screenModelScope.launch {
        val s = _state.value
        val errors = RequestValidators.name(s.editName, label, s.editingId)
        updateState { it.copy(fieldErrors = if (errors.isEmpty()) emptyMap() else mapOf("edit" to errors.values.first())) }
        if (errors.isNotEmpty() || s.busy) return@launch
        updateState { it.copy(busy = true) }
        val request = NameRequest(id = s.editingId, name = s.editName.trim())
        when (kind) {
            CatalogKind.CarTypes -> marketplaceUseCase.updateCarType(request) { r ->
                r.handelState(onSuccess = { done("Car type updated!") }, onError = { e, _ -> failed(e, "Couldn't update the car type") })
            }
            CatalogKind.SpareParts -> marketplaceUseCase.updateCommonSparePart(request) { r ->
                r.handelState(onSuccess = { done("Spare part updated!") }, onError = { e, _ -> failed(e, "Couldn't update the spare part") })
            }
        }
    }

    private fun delete(item: CatalogItem) = screenModelScope.launch {
        if (_state.value.busy) return@launch
        updateState { it.copy(busy = true) }
        when (kind) {
            CatalogKind.CarTypes -> marketplaceUseCase.deleteCarType(item.id) { r ->
                r.handelState(onSuccess = { done("Deleted \"${item.name}\"") }, onError = { e, _ -> failed(e, "Couldn't delete (it may be in use)") })
            }
            CatalogKind.SpareParts -> marketplaceUseCase.deleteCommonSparePart(item.id) { r ->
                r.handelState(onSuccess = { done("Deleted \"${item.name}\"") }, onError = { e, _ -> failed(e, "Couldn't delete (it may be in use)") })
            }
        }
    }
}
