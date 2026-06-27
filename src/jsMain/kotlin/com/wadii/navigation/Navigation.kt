//package com.wadii.navigation
//
//import androidx.compose.runtime.*
//
//interface Screen {
//    @Composable
//    fun Content()
//}
//
//class Navigator(initialScreen: Screen) {
//    private val stack = mutableStateListOf(initialScreen)
//
//    val lastItem: Screen get() = stack.last()
//    val canPop: Boolean get() = stack.size > 1
//
//    fun push(screen: Screen) {
//        stack.add(screen)
//    }
//
//    fun pop() {
//        if (canPop) stack.removeAt(stack.lastIndex)
//    }
//
//    fun replaceAll(screen: Screen) {
//        stack.clear(); stack.add(screen)
//    }
//}
//
//val LocalNavigator = compositionLocalOf<Navigator?> { null }
//
//val CompositionLocal<Navigator?>.currentOrThrow: Navigator
//    @Composable get() = current ?: error("LocalNavigator not found — wrap content in Navigator { }.")
//
//@Composable
//fun Navigator(
//    screen: Screen,
//    content: @Composable (Navigator) -> Unit = { CurrentScreen() }
//) {
//    val navigator = remember { Navigator(screen) }
//    CompositionLocalProvider(LocalNavigator provides navigator) {
//        content(navigator)
//    }
//}
//
//@Composable
//fun CurrentScreen() {
//    val navigator = LocalNavigator.current // LocalNavigator.currentOrThrow
//    navigator?.lastItem?.Content()
//}
