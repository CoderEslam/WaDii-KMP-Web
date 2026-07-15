package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.auth.AuthRequest
import com.teacheronline.domain.model.auth.login.User
import com.teacheronline.domain.repo.AuthRepo
import com.teacheronline.utils.RequestState

class AuthUseCase(private val authRepo: AuthRepo) {
    suspend fun login(request: AuthRequest, response: (RequestState<BaseResponse<User>>) -> Unit) =
        authRepo.login(request, response)

    suspend fun register(request: AuthRequest, response: (RequestState<BaseResponse<User>>) -> Unit) =
        authRepo.register(request, response)

    suspend fun show(id: Long, response: (RequestState<BaseResponse<User>>) -> Unit) =
        authRepo.show(id, response)
}
