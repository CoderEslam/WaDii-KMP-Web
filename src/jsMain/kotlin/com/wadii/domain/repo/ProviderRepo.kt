package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.follow.FollowProviderResponse
import com.wadii.domain.model.follow.Followers
import com.wadii.domain.model.provider.ProviderModel
import com.wadii.domain.model.provider.ProviderRequest
import com.wadii.domain.model.provider.ProviderRequestCallback
import com.wadii.domain.model.provider.UpdateProviderRequest
import com.wadii.domain.model.service.Service
import com.wadii.utils.RequestState


interface ProviderRepo {

    suspend fun providerMe(
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    )
    suspend fun providersList(response: (RequestState<BaseResponse<List<ProviderModel>>>) -> Unit)

    suspend fun providersListByServiceId(
        id: Int,
        response: (RequestState<BaseResponse<List<ProviderModel>>>) -> Unit
    )

    suspend fun updateProvider(
        request: UpdateProviderRequest,
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    )

    suspend fun getProviderById(
        id: Int,
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    )

    suspend fun followProvider(
        providerId: Int,
        response: (RequestState<BaseResponse<FollowProviderResponse>>) -> Unit
    )

    suspend fun unfollowProvider(
        providerId: Int,
        response: (RequestState<BaseResponse<String>>) -> Unit
    )

    suspend fun requestProvider(
        providerRequest: ProviderRequest,
        response: (RequestState<BaseResponse<ProviderRequestCallback>>) -> Unit
    )

    suspend fun putItUser(
        id: Int,
        response: (RequestState<BaseResponse<User>>) -> Unit
    )

    suspend fun putItProvider(
        id: Int,
        response: (RequestState<BaseResponse<User>>) -> Unit
    )
    suspend fun getFollowerList(
        id: Int,
        response: (RequestState<BaseResponse<List<Followers>>>) -> Unit
    )
}