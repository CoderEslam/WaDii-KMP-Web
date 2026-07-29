package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.auth.login.LoginRequest
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.auth.register.RegisterRequest
import com.wadii.domain.repo.AuthRepo
import com.wadii.utils.RequestState


class AuthUseCase(private val authRepo: AuthRepo) {

    suspend fun login(
        loginRequest: LoginRequest,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = authRepo.login(loginRequest, response)

    suspend fun register(
        registerRequest: RegisterRequest,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = authRepo.register(registerRequest, response)


}