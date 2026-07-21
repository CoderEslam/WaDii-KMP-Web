package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.auth.AuthRequest
import com.teacheronline.domain.model.auth.login.User
import com.teacheronline.domain.repo.AuthRepo
import com.teacheronline.utils.RequestState

class AuthRepoImpl(private val apiService: ApiService) : AuthRepo {
    override suspend fun login(request: AuthRequest, response: (RequestState<BaseResponse<User>>) -> Unit) =
        apiService.login(request, response)

    override suspend fun register(request: AuthRequest, response: (RequestState<BaseResponse<User>>) -> Unit) =
        apiService.register(request, response)

    override suspend fun show(id: Long, response: (RequestState<BaseResponse<User>>) -> Unit) =
        apiService.authShow(id, response)
}
