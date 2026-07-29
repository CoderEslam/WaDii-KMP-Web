package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.auth.login.LoginRequest
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.auth.register.RegisterRequest
import com.wadii.utils.RequestState


interface AuthRepo {

    suspend fun login(loginRequest: LoginRequest, response: (RequestState<BaseResponse<User>>) -> Unit)

    suspend fun register(registerRequest: RegisterRequest, response: (RequestState<BaseResponse<User>>) -> Unit)

}