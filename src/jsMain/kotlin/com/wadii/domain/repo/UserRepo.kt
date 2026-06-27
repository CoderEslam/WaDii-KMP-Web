package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.user.UpdateUser
import com.wadii.utils.RequestState


interface UserRepo {
    suspend fun userMe(
        response: (RequestState<BaseResponse<User>>) -> Unit
    )
    suspend fun updateUser(
        updateUser: UpdateUser,
        response: (RequestState<BaseResponse<User>>) -> Unit
    )

    suspend fun updateImageUser(
        byteArray: ByteArray,
        response: (RequestState<BaseResponse<User>>) -> Unit
    )

    suspend fun updateImageBackgroundUser(
        byteArray: ByteArray,
        response: (RequestState<BaseResponse<User>>) -> Unit
    )
}