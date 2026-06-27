package com.wadii.data.repo

import com.wadii.domain.repo.UserRepo
import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.user.UpdateUser
import com.wadii.utils.RequestState


class UserRepoImpl(private val apiService: ApiService) : UserRepo {

    override suspend fun userMe(response: (RequestState<BaseResponse<User>>) -> Unit) =
        apiService.userMe(response)

    override suspend fun updateUser(
        updateUser: UpdateUser,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = apiService.updateUser(updateUser, response)

    override suspend fun updateImageUser(
        byteArray: ByteArray,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = apiService.updateImageUser(byteArray, response)

    override suspend fun updateImageBackgroundUser(
        byteArray: ByteArray,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = apiService.updateImageBackgroundUser(byteArray, response)
}