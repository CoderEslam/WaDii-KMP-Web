package com.wadii.data.repo

import com.wadii.data.api.ApiService
import com.wadii.domain.repo.MarketplaceRepo
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.WorkTime
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.ads.Ads
import com.wadii.domain.model.branch.Branche
import com.wadii.domain.model.carTypes.CarType
import com.wadii.domain.model.link.Link
import com.wadii.domain.model.notification.AgoraTokenRequest
import com.wadii.domain.model.notification.AgoraTokenResponse
import com.wadii.domain.model.notification.ProviderServicesRequest
import com.wadii.domain.model.notification.UserNotification
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.order.OrderModel
import com.wadii.domain.model.order.OrderRequest
import com.wadii.domain.model.provider.ProviderModel
import com.wadii.domain.model.rate.RateModel
import com.wadii.domain.model.rate.RateRequest
import com.wadii.domain.model.response.ResponseCallback
import com.wadii.domain.model.response.ResponseRequest
import com.wadii.domain.model.shop.AvailabilityRequest
import com.wadii.domain.model.shop.BranchRequest
import com.wadii.domain.model.shop.CommonSparePart
import com.wadii.domain.model.shop.LinkRequest
import com.wadii.domain.model.shop.NameRequest
import com.wadii.domain.model.shop.WorkTimeRequest
import com.wadii.utils.RequestState

class MarketplaceRepoImpl(private val apiService: ApiService) : MarketplaceRepo {
    override suspend fun updateOrder(request: OrderRequest, response: (RequestState<BaseResponse<OrderModel>>) -> Unit) =
        apiService.updateOrder(request, response)
    override suspend fun deleteOrder(id: Int, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.deleteOrder(id, response)
    override suspend fun updateResponse(request: ResponseRequest, response: (RequestState<BaseResponse<ResponseCallback>>) -> Unit) =
        apiService.updateResponse(request, response)
    override suspend fun deleteResponse(id: Int, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.deleteResponse(id, response)
    override suspend fun markNotificationRead(id: Long, response: (RequestState<BaseResponse<UserNotification>>) -> Unit) =
        apiService.markNotificationRead(id, response)
    override suspend fun markAllNotificationsRead(response: (RequestState<BaseResponse<List<UserNotification>>>) -> Unit) =
        apiService.markAllNotificationsRead(response)
    override suspend fun deleteNotification(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.deleteNotification(id, response)
    override suspend fun insertRate(request: RateRequest, response: (RequestState<BaseResponse<RateModel>>) -> Unit) =
        apiService.insertRate(request, response)
    override suspend fun updateRate(request: RateRequest, response: (RequestState<BaseResponse<RateModel>>) -> Unit) =
        apiService.updateRate(request, response)
    override suspend fun getAllUsers(response: (RequestState<BaseResponse<List<User>>>) -> Unit) =
        apiService.getAllUsers(response)
    override suspend fun startOrder(id: Int, response: (RequestState<BaseResponse<OrderModel>>) -> Unit) =
        apiService.startOrder(id, response)
    override suspend fun completeOrder(id: Int, response: (RequestState<BaseResponse<OrderModel>>) -> Unit) =
        apiService.completeOrder(id, response)
    override suspend fun setAvailability(request: AvailabilityRequest, response: (RequestState<BaseResponse<ProviderModel>>) -> Unit) =
        apiService.setAvailability(request, response)
    override suspend fun setProviderActive(id: Long, active: Boolean, response: (RequestState<BaseResponse<ProviderModel>>) -> Unit) =
        apiService.setProviderActive(id, active, response)
    override suspend fun deleteRate(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.deleteRate(id, response)
    override suspend fun insertCarType(request: NameRequest, response: (RequestState<BaseResponse<CarType>>) -> Unit) =
        apiService.insertCarType(request, response)
    override suspend fun updateCarType(request: NameRequest, response: (RequestState<BaseResponse<CarType>>) -> Unit) =
        apiService.updateCarType(request, response)
    override suspend fun deleteCarType(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.deleteCarType(id, response)
    override suspend fun getCommonSpareParts(response: (RequestState<BaseResponse<List<CommonSparePart>>>) -> Unit) =
        apiService.getCommonSpareParts(response)
    override suspend fun insertCommonSparePart(request: NameRequest, response: (RequestState<BaseResponse<CommonSparePart>>) -> Unit) =
        apiService.insertCommonSparePart(request, response)
    override suspend fun updateCommonSparePart(request: NameRequest, response: (RequestState<BaseResponse<CommonSparePart>>) -> Unit) =
        apiService.updateCommonSparePart(request, response)
    override suspend fun deleteCommonSparePart(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.deleteCommonSparePart(id, response)
    override suspend fun branchesByProvider(providerId: Long, response: (RequestState<BaseResponse<List<Branche>>>) -> Unit) =
        apiService.branchesByProvider(providerId, response)
    override suspend fun insertBranch(request: BranchRequest, response: (RequestState<BaseResponse<Branche>>) -> Unit) =
        apiService.insertBranch(request, response)
    override suspend fun updateBranch(request: BranchRequest, response: (RequestState<BaseResponse<Branche>>) -> Unit) =
        apiService.updateBranch(request, response)
    override suspend fun deleteBranch(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.deleteBranch(id, response)
    override suspend fun workTimesByBranch(branchId: Long, response: (RequestState<BaseResponse<List<WorkTime>>>) -> Unit) =
        apiService.workTimesByBranch(branchId, response)
    override suspend fun insertWorkTime(request: WorkTimeRequest, response: (RequestState<BaseResponse<WorkTime>>) -> Unit) =
        apiService.insertWorkTime(request, response)
    override suspend fun updateWorkTime(request: WorkTimeRequest, response: (RequestState<BaseResponse<WorkTime>>) -> Unit) =
        apiService.updateWorkTime(request, response)
    override suspend fun deleteWorkTime(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.deleteWorkTime(id, response)
    override suspend fun insertLink(request: LinkRequest, response: (RequestState<BaseResponse<Link>>) -> Unit) =
        apiService.insertLink(request, response)
    override suspend fun updateLink(request: LinkRequest, response: (RequestState<BaseResponse<Link>>) -> Unit) =
        apiService.updateLink(request, response)
    override suspend fun deleteLink(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.deleteLink(id, response)
    override suspend fun updateProviderServices(request: ProviderServicesRequest, response: (RequestState<BaseResponse<ProviderModel>>) -> Unit) =
        apiService.updateProviderServices(request, response)
    override suspend fun providersByServices(serviceIds: List<Long>, response: (RequestState<BaseResponse<List<ProviderModel>>>) -> Unit) =
        apiService.providersByServices(serviceIds, response)
    override suspend fun offersByServices(serviceIds: List<Long>, response: (RequestState<BaseResponse<List<OfferResponse>>>) -> Unit) =
        apiService.offersByServices(serviceIds, response)
    override suspend fun setAdStatus(id: Long, status: String, response: (RequestState<BaseResponse<Ads>>) -> Unit) =
        apiService.setAdStatus(id, status, response)
    override suspend fun trackAdImpression(id: Long, response: (RequestState<BaseResponse<Ads>>) -> Unit) =
        apiService.trackAdImpression(id, response)
    override suspend fun trackAdClick(id: Long, response: (RequestState<BaseResponse<Ads>>) -> Unit) =
        apiService.trackAdClick(id, response)
    override suspend fun unreadMessageCount(response: (RequestState<BaseResponse<Int>>) -> Unit) =
        apiService.unreadMessageCount(response)
    override suspend fun isUserOnline(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.isUserOnline(id, response)
    override suspend fun presence(userIds: List<Long>, response: (RequestState<BaseResponse<Map<String, Boolean>>>) -> Unit) =
        apiService.presence(userIds, response)
    override suspend fun onlineUsers(response: (RequestState<BaseResponse<List<Long>>>) -> Unit) =
        apiService.onlineUsers(response)
    override suspend fun agoraToken(request: AgoraTokenRequest, response: (RequestState<BaseResponse<AgoraTokenResponse>>) -> Unit) =
        apiService.agoraToken(request, response)
    override suspend fun deleteAccount(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.deleteAccount(id, response)
}
