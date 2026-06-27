package com.wadii.data.repo

import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.auth.login.LoginRequest
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.auth.register.RegisterRequest
import com.wadii.domain.repo.AuthRepo
import com.wadii.utils.RequestState


class AuthRepoImpl(private val apiService: ApiService) : AuthRepo {

    override suspend fun login(
        loginRequest: LoginRequest,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = apiService.login(loginRequest, response)

    override suspend fun register(
        registerRequest: RegisterRequest,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = apiService.register(registerRequest, response)

}