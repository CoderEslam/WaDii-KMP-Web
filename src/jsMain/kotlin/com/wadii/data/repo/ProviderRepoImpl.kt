package com.wadii.data.repo

import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.follow.FollowProviderResponse
import com.wadii.domain.model.follow.Followers
import com.wadii.domain.model.provider.ProviderModel
import com.wadii.domain.model.provider.ProviderRequest
import com.wadii.domain.model.provider.ProviderRequestCallback
import com.wadii.domain.model.provider.UpdateProviderRequest
import com.wadii.domain.model.service.Service
import com.wadii.domain.repo.ProviderRepo
import com.wadii.utils.RequestState


class ProviderRepoImpl(private val apiService: ApiService) : ProviderRepo {

    override suspend fun providerMe(response: (RequestState<BaseResponse<ProviderModel>>) -> Unit) =
        apiService.providerMe(response)

    override suspend fun providersList(response: (RequestState<BaseResponse<List<ProviderModel>>>) -> Unit) =
        apiService.providersList(response)

    override suspend fun providersListByServiceId(
        id: Long,
        response: (RequestState<BaseResponse<List<ProviderModel>>>) -> Unit
    ) = apiService.providersListByServiceId(id, response)

    override suspend fun updateProvider(
        request: UpdateProviderRequest,
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    ) = apiService.updateProvider(request, response)

    override suspend fun getProviderById(
        id: Int, response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    ) = apiService.getProviderById(id, response)

    override suspend fun followProvider(
        providerId: Int, response: (RequestState<BaseResponse<FollowProviderResponse>>) -> Unit
    ) = apiService.followProvider(providerId, response)

    override suspend fun unfollowProvider(
        providerId: Int,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) = apiService.unfollowProvider(providerId, response)

    override suspend fun requestProvider(
        providerRequest: ProviderRequest,
        response: (RequestState<BaseResponse<ProviderRequestCallback>>) -> Unit
    ) = apiService.requestProvider(providerRequest, response)

    override suspend fun putItUser(
        id: Long,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = apiService.putItUser(id, response)

    override suspend fun putItProvider(
        id: Long,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = apiService.putItProvider(id, response)

    override suspend fun getFollowerList(
        id: Int,
        response: (RequestState<BaseResponse<List<Followers>>>) -> Unit
    ) = apiService.getFollowerList(id, response)


}