package com.teacheronline.pages.admin.students

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.EnrollRequest
import com.teacheronline.domain.model.StudentRequest
import com.teacheronline.domain.usecase.LevelUseCase
import com.teacheronline.domain.usecase.ParentUseCase
import com.teacheronline.domain.usecase.StudentUseCase
import com.teacheronline.domain.usecase.SubjectUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.teacheronline.data.firebase.FcmService

class AdminStudentsViewModel(
    private val studentUseCase: StudentUseCase,
    private val subjectUseCase: SubjectUseCase,
    private val levelUseCase: LevelUseCase,
    private val parentUseCase: ParentUseCase
) : BaseViewModel<AdminStudentsState, AdminStudentsEvent>() {

    override val initialState: AdminStudentsState get() = AdminStudentsState()

    override val state: StateFlow<AdminStudentsState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: AdminStudentsEvent) {
        when (event) {
            AdminStudentsEvent.Load -> load()
            is AdminStudentsEvent.SetEnrollMode -> updateState { it.copy(enrollMode = event.mode) }
            is AdminStudentsEvent.SetSelectedParent -> updateState { it.copy(selectedParentId = event.id) }
            is AdminStudentsEvent.SetParentFirstName -> updateState { it.copy(parentFirstName = event.value) }
            is AdminStudentsEvent.SetParentLastName -> updateState { it.copy(parentLastName = event.value) }
            is AdminStudentsEvent.SetParentEmail -> updateState { it.copy(parentEmail = event.value) }
            is AdminStudentsEvent.SetParentPassword -> updateState { it.copy(parentPassword = event.value) }
            is AdminStudentsEvent.SetParentPhone -> updateState { it.copy(parentPhone = event.value) }
            is AdminStudentsEvent.SetParentContact -> updateState { it.copy(parentContact = event.value) }
            is AdminStudentsEvent.SetFirstName -> updateState { it.copy(firstName = event.value) }
            is AdminStudentsEvent.SetLastName -> updateState { it.copy(lastName = event.value) }
            is AdminStudentsEvent.SetEmail -> updateState { it.copy(email = event.value) }
            is AdminStudentsEvent.SetPassword -> updateState { it.copy(password = event.value) }
            is AdminStudentsEvent.SetPhone -> updateState { it.copy(phone = event.value) }
            is AdminStudentsEvent.SetLevel -> updateState { it.copy(levelId = event.id) }
            is AdminStudentsEvent.ToggleSubject -> updateState {
                val ids = it.selectedSubjectIds.toMutableSet()
                if (!ids.add(event.id)) ids.remove(event.id)
                it.copy(selectedSubjectIds = ids)
            }

            AdminStudentsEvent.Enroll -> enroll()
            is AdminStudentsEvent.Delete -> delete(event.id)
        }
    }

    private fun load() = screenModelScope.launch {
        subjectUseCase.showAll { r ->
            r.handelState(onSuccess = { data ->
                updateState {
                    it.copy(
                        subjects = data.data ?: emptyList()
                    )
                }
            })
        }
        levelUseCase.all { r ->
            r.handelState(onSuccess = { data ->
                updateState {
                    it.copy(
                        levels = data.data ?: emptyList()
                    )
                }
            })
        }
        parentUseCase.all { r ->
            r.handelState(onSuccess = { data ->
                updateState {
                    it.copy(
                        parents = data.data ?: emptyList()
                    )
                }
            })
        }
        studentUseCase.all { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            students = data.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun enroll() = screenModelScope.launch {
        val s = _state.value
        val fcmToken = FcmService.fetchToken() ?: "no fcm token"
        val studentRequest = StudentRequest(
            phone = s.phone,
            subjectIds = s.selectedSubjectIds,
            scheduleIds = emptySet(),
            levelId = s.levelId,
            firstName = s.firstName,
            lastName = s.lastName,
            email = s.email,
            password = s.password,
            fcmToken = fcmToken
        )

        when (s.enrollMode) {
            EnrollMode.NEW_PARENT -> {
                val request = EnrollRequest(
                    parentContact = s.parentContact,
                    phone = s.parentPhone,
                    firstName = s.parentFirstName,
                    lastName = s.parentLastName,
                    email = s.parentEmail,
                    password = s.parentPassword,
                    fcmToken = fcmToken,
                    students = listOf(studentRequest)
                )
                parentUseCase.enroll(request) { r ->
                    r.handelState(
                        onLoading = { updateState { it.copy(saving = true) } },
                        onSuccess = { data ->
                            if (data.statusCode != 200) {
                                updateState { it.copy(saving = false, error = data.message) }
                                AppState.toast(data.message, true)
                            } else {
                                resetForm()
                                AppState.toast("Parent and student enrolled")
                                load()
                            }
                        },
                        onError = { e, _ ->
                            updateState {
                                it.copy(
                                    saving = false,
                                    error = e
                                )
                            }; AppState.toast("Enroll failed", true)
                        }
                    )
                }
            }

            EnrollMode.EXISTING_PARENT -> {
                if (s.selectedParentId == 0L) {
                    AppState.toast("Select a parent first", true)
                    return@launch
                }
                parentUseCase.addStudents(s.selectedParentId, listOf(studentRequest)) { r ->
                    r.handelState(
                        onLoading = { updateState { it.copy(saving = true) } },
                        onSuccess = { data ->
                            if (data.statusCode != 200) {
                                updateState { it.copy(saving = false, error = data.message) }
                                AppState.toast(data.message, true)
                            } else {
                                resetForm()
                                AppState.toast("Student added to parent")
                                load()
                            }
                        },
                        onError = { e, _ ->
                            updateState {
                                it.copy(
                                    saving = false,
                                    error = e
                                )
                            }; AppState.toast("Add student failed", true)
                        }
                    )
                }
            }
        }
    }

    private fun resetForm() = updateState {
        it.copy(
            saving = false,
            firstName = "",
            lastName = "",
            email = "",
            password = "",
            phone = "",
            parentFirstName = "",
            parentLastName = "",
            parentEmail = "",
            parentPassword = "",
            parentPhone = "",
            parentContact = "",
            levelId = 0,
            selectedSubjectIds = emptySet()
        )
    }

    private fun delete(id: Long) = screenModelScope.launch {
        studentUseCase.delete(id) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { updateState { it.copy(isLoading = false) }; AppState.toast("Deleted"); load() },
                onError = { e, _ ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = e
                        )
                    }; AppState.toast("Delete failed", true)
                }
            )
        }
    }
}
