package com.teacheronline.ui

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.teacheronline.domain.model.auth.Role
import com.teacheronline.pages.admin.centers.CentersScreen
import com.teacheronline.pages.admin.config.ConfigScreen
import com.teacheronline.pages.admin.dashboard.AdminDashboardScreen
import com.teacheronline.pages.admin.languages.LanguagesScreen
import com.teacheronline.pages.admin.levels.LevelsScreen
import com.teacheronline.pages.admin.payments.PaymentsScreen
import com.teacheronline.pages.admin.schedule.AdminScheduleScreen
import com.teacheronline.pages.admin.secretaries.SecretariesScreen
import com.teacheronline.pages.admin.students.AdminStudentsScreen
import com.teacheronline.pages.admin.subjects.SubjectsScreen
import com.teacheronline.pages.admin.teachers.TeachersScreen
import com.teacheronline.pages.parent.dashboard.ParentDashboardScreen
import com.teacheronline.pages.secretary.dashboard.SecretaryDashboardScreen
import com.teacheronline.pages.shared.chat.ChatScreen
import com.teacheronline.pages.shared.profile.ProfileScreen
import com.teacheronline.pages.student.dashboard.StudentDashboardScreen
import com.teacheronline.pages.teacher.attendance.TeacherAttendanceScreen
import com.teacheronline.pages.teacher.dashboard.TeacherDashboardScreen
import com.teacheronline.pages.teacher.payments.TeacherPaymentsScreen
import com.teacheronline.pages.teacher.schedule.TeacherScheduleScreen
import com.teacheronline.pages.teacher.secretaries.TeacherSecretariesScreen
import com.teacheronline.pages.teacher.students.TeacherStudentsScreen
import com.teacheronline.state.AppState
import org.jetbrains.compose.web.dom.*

private data class NavLink(val label: String, val icon: String, val screen: Screen)

@Composable
fun Layout(content: @Composable () -> Unit) {
    val user = AppState.user ?: return
    val navigator = LocalNavigator.currentOrThrow
    val darkMode = AppState.darkMode
    var menuOpen by remember { mutableStateOf(false) }

    val adminLinks = listOf(
        NavLink("Dashboard", "⬡", AdminDashboardScreen()),
        NavLink("Centers", "🏫", CentersScreen()),
        NavLink("Teachers", "🧑‍🏫", TeachersScreen()),
        NavLink("Subjects", "📚", SubjectsScreen()),
        NavLink("Languages", "🌐", LanguagesScreen()),
        NavLink("Students", "🎓", AdminStudentsScreen()),
        NavLink("Secretaries", "🗂️", SecretariesScreen()),
        NavLink("Levels", "🎯", LevelsScreen()),
        NavLink("Schedule", "🗓️", AdminScheduleScreen()),
        NavLink("Payments", "💳", PaymentsScreen()),
        NavLink("Config", "⚙️", ConfigScreen()),
    )
    val teacherLinks = listOf(
        NavLink("Dashboard", "⬡", TeacherDashboardScreen()),
        NavLink("My Students", "🎓", TeacherStudentsScreen()),
        NavLink("Attendance", "🗓️", TeacherAttendanceScreen()),
        NavLink("Schedule", "🕐", TeacherScheduleScreen()),
        NavLink("Payments", "💳", TeacherPaymentsScreen()),
        NavLink("Secretaries", "🗂️", TeacherSecretariesScreen()),
        NavLink("Messages", "◎", ChatScreen()),
    )
    val studentLinks = listOf(
        NavLink("Dashboard", "⬡", StudentDashboardScreen()),
        NavLink("Messages", "◎", ChatScreen()),
    )
    val parentLinks = listOf(
        NavLink("My Children", "⬡", ParentDashboardScreen()),
        NavLink("Messages", "◎", ChatScreen()),
    )
    val secretaryLinks = listOf(
        NavLink("Console", "⬡", SecretaryDashboardScreen()),
        NavLink("Messages", "◎", ChatScreen()),
    )

    val links = when (user.role) {
        Role.ADMIN -> adminLinks
        Role.TEACHER -> teacherLinks
        Role.STUDENT -> studentLinks
        Role.PARENTS -> parentLinks
        Role.SECRETARY -> secretaryLinks
    }

    Div(attrs = { classes("flex", "min-h-screen") }) {
        // Desktop Sidebar
        Aside(attrs = {
            classes("hidden", "md:flex", "flex-col", "w-64", "fixed", "inset-y-0", "left-0", "z-30", "space-sidebar")
        }) {
            Div(attrs = { classes("px-6", "py-5", "border-b", "border-default") }) {
                Span(attrs = {
                    classes("text-2xl", "font-extrabold", "cursor-pointer", "brand-text", "tracking-tight")
                    onClick { navigator.replaceAll(links.first().screen) }
                }) { Text("TeacherOnline") }
                P(attrs = { classes("text-xs", "text-body-subtle", "mt-0.5", "tracking-widest", "uppercase") }) {
                    Text(user.role.name.lowercase())
                }
            }

            Nav(attrs = { classes("flex-1", "px-3", "py-4", "space-y-0.5", "overflow-y-auto") }) {
                links.forEach { link -> SpaceNavLink(link, navigator) }
            }

            Div(attrs = { classes("px-3", "py-4", "border-t", "border-default", "space-y-0.5") }) {
                SpaceNavLink(NavLink("Profile", "◑", ProfileScreen()), navigator)

                Button(attrs = {
                    classes("dark-toggle")
                    onClick { AppState.toggleDarkMode() }
                }) {
                    Span(attrs = { classes("text-base", "w-5", "text-center") }) { Text(if (darkMode) "☀️" else "🌙") }
                    Span { Text(if (darkMode) "Light Mode" else "Dark Mode") }
                }

                Button(attrs = {
                    classes("dark-toggle", "text-fg-danger")
                    onClick { AppState.logout() }
                }) {
                    Span(attrs = { classes("text-base", "w-5", "text-center") }) { Text("→") }
                    Span { Text("Sign Out") }
                }
            }
        }

        // Mobile header
        Header(attrs = {
            classes("md:hidden", "fixed", "top-0", "left-0", "right-0", "z-30", "space-header",
                "flex", "items-center", "justify-between", "px-4", "h-14")
        }) {
            Span(attrs = {
                classes("text-xl", "font-extrabold", "cursor-pointer", "brand-text", "tracking-tight")
                onClick { navigator.replaceAll(links.first().screen) }
            }) { Text("TeacherOnline") }
            Div(attrs = { classes("flex", "items-center", "gap-2") }) {
                Button(attrs = {
                    classes("p-2", "text-body-subtle", "hover:text-fg-brand", "transition-colors", "text-lg")
                    onClick { AppState.toggleDarkMode() }
                }) { Text(if (darkMode) "☀️" else "🌙") }
                Button(attrs = {
                    classes("p-2", "text-body", "text-xl")
                    onClick { menuOpen = !menuOpen }
                }) { Text(if (menuOpen) "✕" else "☰") }
            }
        }

        // Mobile menu overlay
        if (menuOpen) {
            Div(attrs = {
                classes("md:hidden", "fixed", "inset-0", "z-20", "bg-black/50")
                onClick { menuOpen = false }
            }) {
                Div(attrs = {
                    classes("absolute", "left-0", "top-14", "bottom-0", "w-64", "space-sidebar", "px-3", "py-4", "space-y-0.5", "overflow-y-auto")
                    onClick { it.stopPropagation() }
                }) {
                    links.forEach { link ->
                        SpaceNavLink(link, navigator) { menuOpen = false }
                    }
                    SpaceNavLink(NavLink("Profile", "◑", ProfileScreen()), navigator) { menuOpen = false }
                    Button(attrs = {
                        classes("dark-toggle", "text-fg-danger")
                        onClick { AppState.logout(); menuOpen = false }
                    }) {
                        Span(attrs = { classes("text-base", "w-5", "text-center") }) { Text("→") }
                        Span { Text("Sign Out") }
                    }
                }
            }
        }

        // Main content
        Main(attrs = {
            classes("flex-1", "md:ml-64", "pt-14", "md:pt-0", "min-h-screen")
        }) {
            Div(attrs = { classes("max-w-5xl", "mx-auto", "p-6") }) {
                content()
            }
        }
    }
}

@Composable
private fun SpaceNavLink(link: NavLink, navigator: Navigator, afterClick: (() -> Unit)? = null) {
    val active = navigator.lastItem::class == link.screen::class
    Button(attrs = {
        if (active) {
            attr("class", "w-full flex items-center gap-3 px-3 py-2.5 rounded-neu-base text-sm font-semibold transition-all text-left bg-surface shadow-neu-inset text-fg-brand")
        } else {
            attr("class", "w-full flex items-center gap-3 px-3 py-2.5 rounded-neu-base text-sm font-medium transition-all text-left bg-surface text-body hover:shadow-neu-sm hover:text-heading")
        }
        onClick {
            navigator.replaceAll(link.screen)
            afterClick?.invoke()
        }
    }) {
        Span(attrs = {
            if (active) attr("class", "text-base w-5 text-center text-fg-brand")
            else attr("class", "text-base w-5 text-center text-body-subtle")
        }) { Text(link.icon) }
        Span { Text(link.label) }
        if (active) {
            Span(attrs = { classes("ml-auto", "w-1.5", "h-1.5", "rounded-full", "bg-brand") }) {}
        }
    }
}
