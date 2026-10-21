package com.wadii.pages.admin.catalog

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.GhostButton
import com.wadii.ui.InputField
import com.wadii.ui.LoadingSkeletons
import com.wadii.ui.PrimaryButton
import com.wadii.ui.SecondaryButton
import org.jetbrains.compose.web.dom.*
import org.koin.core.parameter.parametersOf

class CatalogScreen(private val kind: CatalogKind) : Screen {
    // Two nav links share this class, so give each its own key (also used for the active-link highlight).
    override val key: ScreenKey = "catalog-${kind.name}"

    @Composable
    override fun Content() {
        val model = koinScreenModel<CatalogViewModel> { parametersOf(kind) }
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            Div {
                H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("${kind.icon} ${kind.title}") }
                P(attrs = { classes("text-sm", "text-body-subtle", "mt-1") }) { Text(kind.hint) }
            }

            Card(classes = "p-5") {
                Div(attrs = { classes("flex", "items-start", "gap-3") }) {
                    Div(attrs = { classes("flex-1") }) {
                        InputField("New ${kind.singular}", state.newName, "Type a name…", error = state.fieldErrors["new"]) {
                            model.onEvent(CatalogEvent.SetNew(it))
                        }
                    }
                    Div(attrs = { classes("pt-7") }) {
                        PrimaryButton("Add", loading = state.busy && state.editingId == 0L) { model.onEvent(CatalogEvent.Add) }
                    }
                }
            }

            when {
                state.isLoading && state.items.isEmpty() -> LoadingSkeletons(4, "h-16")
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                state.items.isEmpty() -> EmptyState(kind.icon, "Nothing here yet", "Add the first ${kind.singular} above.")
                else -> Div(attrs = { classes("space-y-2", "stagger") }) {
                    state.items.forEach { item ->
                        Card(classes = "p-4") {
                            if (state.editingId == item.id) {
                                Div(attrs = { classes("flex", "items-start", "gap-3") }) {
                                    Div(attrs = { classes("flex-1") }) {
                                        InputField("Name", state.editName, required = true, error = state.fieldErrors["edit"]) {
                                            model.onEvent(CatalogEvent.SetEdit(it))
                                        }
                                    }
                                    Div(attrs = { classes("flex", "gap-2", "pt-7") }) {
                                        PrimaryButton("Save", loading = state.busy) { model.onEvent(CatalogEvent.SaveEdit) }
                                        SecondaryButton("Cancel") { model.onEvent(CatalogEvent.CancelEdit) }
                                    }
                                }
                            } else {
                                Div(attrs = { classes("flex", "items-center", "justify-between", "gap-3") }) {
                                    P(attrs = { classes("font-medium", "text-heading") }) { Text(item.name) }
                                    Div(attrs = { classes("flex", "gap-1") }) {
                                        GhostButton("✏️") { model.onEvent(CatalogEvent.StartEdit(item)) }
                                        GhostButton("🗑️", disabled = state.busy) { model.onEvent(CatalogEvent.Delete(item)) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
