package com.teacheronline.pages.admin.dashboard

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.teacheronline.pages.admin.centers.CentersScreen
import com.teacheronline.pages.admin.students.AdminStudentsScreen
import com.teacheronline.pages.admin.subjects.SubjectsScreen
import com.teacheronline.pages.admin.teachers.TeachersScreen
import com.teacheronline.ui.LoadingScreen
import com.teacheronline.ui.StatCard
import org.jetbrains.compose.web.dom.*

class AdminDashboardScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.koinNavigatorScreenModel<AdminDashboardScreenModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-8") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Owner Dashboard") }
            if (state.isLoading) LoadingScreen()
            Div(attrs = { classes("grid", "grid-cols-2", "md:grid-cols-4", "gap-4") }) {
                StatCard("Centers", state.centersCount.toString(), "🏫") { navigator.replaceAll(CentersScreen()) }
                StatCard("Teachers", state.teachersCount.toString(), "🧑‍🏫") { navigator.replaceAll(TeachersScreen()) }
                StatCard("Subjects", state.subjectsCount.toString(), "📚") { navigator.replaceAll(SubjectsScreen()) }
                StatCard("Students", state.studentsCount.toString(), "🎓") { navigator.replaceAll(AdminStudentsScreen()) }
            }
        }
    }
}
