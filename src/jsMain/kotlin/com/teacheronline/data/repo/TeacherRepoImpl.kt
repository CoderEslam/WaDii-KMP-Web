package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.CreateSubjectsTeacher
import com.teacheronline.domain.model.Teacher
import com.teacheronline.domain.repo.TeacherRepo
import com.teacheronline.utils.RequestState

class TeacherRepoImpl(private val apiService: ApiService) : TeacherRepo {
    override suspend fun show(id: Long, response: (RequestState<BaseResponse<Teacher>>) -> Unit) =
        apiService.teacherShow(id, response)

    override suspend fun showAll(response: (RequestState<BaseResponse<List<Teacher>>>) -> Unit) =
        apiService.teachersShowAll(response)

    override suspend fun insert(request: CreateSubjectsTeacher, response: (RequestState<BaseResponse<Teacher>>) -> Unit) =
        apiService.teacherInsert(request, response)

    override suspend fun update(request: CreateSubjectsTeacher, response: (RequestState<BaseResponse<Teacher>>) -> Unit) =
        apiService.teacherUpdate(request, response)

    override suspend fun delete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.teacherDelete(id, response)
}
