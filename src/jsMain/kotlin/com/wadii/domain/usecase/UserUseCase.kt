package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.user.UpdateUser
import com.wadii.domain.repo.UserRepo
import com.wadii.utils.RequestState


class UserUseCase(val userRepo: UserRepo) {

    suspend fun userMe(
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = userRepo.userMe(response)

    suspend fun updateUser(
        updateUser: UpdateUser,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = userRepo.updateUser(updateUser, response)

    suspend fun updateImageUser(
        byteArray: ByteArray,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = userRepo.updateImageUser(byteArray, response)

    suspend fun updateImageBackgroundUser(
        byteArray: ByteArray,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = userRepo.updateImageBackgroundUser(byteArray, response)

}