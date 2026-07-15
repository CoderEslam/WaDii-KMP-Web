package com.teacheronline.domain.repo

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.auth.AuthRequest
import com.teacheronline.domain.model.auth.login.User
import com.teacheronline.utils.RequestState

interface AuthRepo {
    suspend fun login(request: AuthRequest, response: (RequestState<BaseResponse<User>>) -> Unit)
    suspend fun register(request: AuthRequest, response: (RequestState<BaseResponse<User>>) -> Unit)
    suspend fun show(id: Long, response: (RequestState<BaseResponse<User>>) -> Unit)
}
