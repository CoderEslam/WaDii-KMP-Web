package com.wadii.screens.providerDetail

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.data.api.to1dp
import com.wadii.screens.orders.new.NewOrderScreen
import com.wadii.ui.LoadingScreen
import org.jetbrains.compose.web.dom.*
import org.koin.core.parameter.parametersOf

class ProviderDetailScreen(val providerId: Int) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<ProviderDetailViewModel> { parametersOf(providerId) }
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            Button(attrs = {
                classes("text-sm", "text-slate-500", "hover:text-slate-700")
                onClick { navigator.pop() }
            }) { Text("← Back") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center", "text-slate-500") }) { Text(state.error!!) }
                state.provider != null -> {
                    val provider = state.provider!!
                    val following = state.following
                    Div(attrs = { classes("bg-white", "rounded-2xl", "overflow-hidden", "shadow-sm") }) {
                        Div(attrs = { classes("h-32", "bg-gradient-to-r", "from-amber-400", "to-orange-400") }) {}
                        Div(attrs = { classes("px-6", "pb-6") }) {
                            Div(attrs = { classes("flex", "items-end", "justify-between", "-mt-10", "mb-4") }) {
                                Div(attrs = { classes("w-20", "h-20", "rounded-2xl", "bg-white", "border-4", "border-white", "shadow", "flex", "items-center", "justify-center", "text-3xl", "font-bold", "text-amber-600") }) {
                                    Text(provider.name.firstOrNull()?.toString() ?: "?")
                                }
                                Button(attrs = {
                                    classes("flex", "items-center", "gap-2", "px-4", "py-2", "rounded-xl", "text-sm", "font-medium", "transition-colors", "mt-10")
                                    if (following) classes("bg-slate-100", "text-slate-700", "hover:bg-slate-200")
                                    else classes("bg-amber-500", "text-white", "hover:bg-amber-600")
                                    onClick { model.onEvent(ProviderDetailEvent.ToggleFollow(providerId)) }
                                }) { Text(if (following) "✓ Unfollow" else "+ Follow") }
                            }
                            H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text(provider.name) }
                            Div(attrs = { classes("flex", "items-center", "gap-4", "mt-2", "text-sm", "text-slate-600") }) {
                                Span { Text("⭐ ${provider.rate.to1dp()} rating") }
                                Span { Text("${provider.followersCount} followers") }
                            }
                            provider.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                                Div(attrs = { classes("flex", "flex-wrap", "gap-2", "mt-4") }) {
                                    svcs.forEach { s ->
                                        Span(attrs = { classes("px-3", "py-1", "bg-amber-50", "text-amber-700", "text-sm", "rounded-full") }) { Text(s.name) }
                                    }
                                }
                            }
                        }
                    }
                    provider.branches?.takeIf { it.isNotEmpty() }?.let { branches ->
                        Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                            H2(attrs = { classes("font-semibold", "text-slate-800", "mb-3") }) { Text("Branches") }
                            Div(attrs = { classes("space-y-2") }) {
                                branches.forEach { b ->
                                    Div(attrs = { classes("flex", "items-center", "gap-3", "py-2", "border-b", "border-slate-50", "last:border-0") }) {
                                        Span(attrs = { classes("text-slate-400") }) { Text("📍") }
                                        Div {
                                            P(attrs = { classes("font-medium", "text-slate-800", "text-sm") }) { Text(b.name) }
                                            P(attrs = { classes("text-xs", "text-slate-500") }) { Text(b.address) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                        Button(attrs = {
                            classes("w-full", "py-3", "bg-amber-500", "text-white", "font-semibold", "rounded-xl", "hover:bg-amber-600", "transition-colors")
                            onClick { navigator.push(NewOrderScreen()) }
                        }) { Text("Request Service") }
                    }
                }
            }
        }
    }
}
