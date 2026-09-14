package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.follow.FollowProviderResponse
import com.wadii.domain.model.follow.Followers
import com.wadii.domain.model.provider.CreateProviderByAdminRequest
import com.wadii.domain.model.provider.ProviderModel
import com.wadii.domain.model.provider.ProviderRequest
import com.wadii.domain.model.provider.ProviderRequestCallback
import com.wadii.domain.model.provider.UpdateProviderRequest
import com.wadii.domain.model.service.Service
import com.wadii.domain.repo.ProviderRepo
import com.wadii.utils.RequestState


class ProviderUseCase(private val providerRepo: ProviderRepo) {

    suspend fun providerMe(
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    ) = providerRepo.providerMe(response)

    suspend fun providersList(response: (RequestState<BaseResponse<List<ProviderModel>>>) -> Unit) =
        providerRepo.providersList(response)

    suspend fun providersListByServiceId(
        id: Long,
        response: (RequestState<BaseResponse<List<ProviderModel>>>) -> Unit
    ) = providerRepo.providersListByServiceId(id, response)

    suspend fun getProviderById(
        id: Int,
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    ) = providerRepo.getProviderById(id, response)

    suspend fun followProvider(
        providerId: Long, response: (RequestState<BaseResponse<FollowProviderResponse>>) -> Unit
    ) = providerRepo.followProvider(providerId, response)

    suspend fun unfollowProvider(
        providerId: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) = providerRepo.unfollowProvider(providerId, response)

    suspend fun updateProvider(
        request: UpdateProviderRequest,
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    ) = providerRepo.updateProvider(request, response)

    suspend fun createProviderByAdmin(
        request: CreateProviderByAdminRequest,
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    ) = providerRepo.createProviderByAdmin(request, response)

    suspend fun requestProvider(
        providerRequest: ProviderRequest,
        response: (RequestState<BaseResponse<ProviderRequestCallback>>) -> Unit
    ) = providerRepo.requestProvider(providerRequest, response)

    suspend fun putItUser(
        id: Long,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = providerRepo.putItUser(id, response)

    suspend fun putItProvider(
        id: Long,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) = providerRepo.putItProvider(id, response)


    suspend fun getFollowerList(
        id: Int,
        response: (RequestState<BaseResponse<List<Followers>>>) -> Unit
    ) = providerRepo.getFollowerList(id, response)
}