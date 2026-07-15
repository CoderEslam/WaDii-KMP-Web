package com.teacheronline.domain.repo

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.CreateSubjectsTeacher
import com.teacheronline.domain.model.Teacher
import com.teacheronline.utils.RequestState

interface TeacherRepo {
    suspend fun show(id: Long, response: (RequestState<BaseResponse<Teacher>>) -> Unit)
    suspend fun showAll(response: (RequestState<BaseResponse<List<Teacher>>>) -> Unit)
    suspend fun insert(request: CreateSubjectsTeacher, response: (RequestState<BaseResponse<Teacher>>) -> Unit)
    suspend fun update(request: CreateSubjectsTeacher, response: (RequestState<BaseResponse<Teacher>>) -> Unit)
    suspend fun delete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
}
