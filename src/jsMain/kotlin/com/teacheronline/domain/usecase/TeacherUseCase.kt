package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.CreateSubjectsTeacher
import com.teacheronline.domain.model.Teacher
import com.teacheronline.domain.repo.TeacherRepo
import com.teacheronline.utils.RequestState

class TeacherUseCase(private val repo: TeacherRepo) {
    suspend fun show(id: Long, response: (RequestState<BaseResponse<Teacher>>) -> Unit) = repo.show(id, response)
    suspend fun showAll(response: (RequestState<BaseResponse<List<Teacher>>>) -> Unit) = repo.showAll(response)
    suspend fun insert(request: CreateSubjectsTeacher, response: (RequestState<BaseResponse<Teacher>>) -> Unit) = repo.insert(request, response)
    suspend fun update(request: CreateSubjectsTeacher, response: (RequestState<BaseResponse<Teacher>>) -> Unit) = repo.update(request, response)
    suspend fun delete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) = repo.delete(id, response)
}
