package com.wadii.domain.repo

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

/** Endpoints added from API_REFERENCE.md (orders/responses edit+delete, rate, catalog, shop details, ads, presence, calls). */
interface MarketplaceRepo {
    suspend fun updateOrder(request: OrderRequest, response: (RequestState<BaseResponse<OrderModel>>) -> Unit)
    suspend fun deleteOrder(id: Int, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
    suspend fun updateResponse(request: ResponseRequest, response: (RequestState<BaseResponse<ResponseCallback>>) -> Unit)
    suspend fun deleteResponse(id: Int, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
    suspend fun markNotificationRead(id: Long, response: (RequestState<BaseResponse<UserNotification>>) -> Unit)
    suspend fun markAllNotificationsRead(response: (RequestState<BaseResponse<List<UserNotification>>>) -> Unit)
    suspend fun deleteNotification(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
    suspend fun insertRate(request: RateRequest, response: (RequestState<BaseResponse<RateModel>>) -> Unit)
    suspend fun updateRate(request: RateRequest, response: (RequestState<BaseResponse<RateModel>>) -> Unit)
    suspend fun getAllUsers(response: (RequestState<BaseResponse<List<User>>>) -> Unit)
    suspend fun startOrder(id: Int, response: (RequestState<BaseResponse<OrderModel>>) -> Unit)
    suspend fun completeOrder(id: Int, response: (RequestState<BaseResponse<OrderModel>>) -> Unit)
    suspend fun setAvailability(request: AvailabilityRequest, response: (RequestState<BaseResponse<ProviderModel>>) -> Unit)
    suspend fun setProviderActive(id: Long, active: Boolean, response: (RequestState<BaseResponse<ProviderModel>>) -> Unit)
    suspend fun deleteRate(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
    suspend fun insertCarType(request: NameRequest, response: (RequestState<BaseResponse<CarType>>) -> Unit)
    suspend fun updateCarType(request: NameRequest, response: (RequestState<BaseResponse<CarType>>) -> Unit)
    suspend fun deleteCarType(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
    suspend fun getCommonSpareParts(response: (RequestState<BaseResponse<List<CommonSparePart>>>) -> Unit)
    suspend fun insertCommonSparePart(request: NameRequest, response: (RequestState<BaseResponse<CommonSparePart>>) -> Unit)
    suspend fun updateCommonSparePart(request: NameRequest, response: (RequestState<BaseResponse<CommonSparePart>>) -> Unit)
    suspend fun deleteCommonSparePart(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
    suspend fun branchesByProvider(providerId: Long, response: (RequestState<BaseResponse<List<Branche>>>) -> Unit)
    suspend fun insertBranch(request: BranchRequest, response: (RequestState<BaseResponse<Branche>>) -> Unit)
    suspend fun updateBranch(request: BranchRequest, response: (RequestState<BaseResponse<Branche>>) -> Unit)
    suspend fun deleteBranch(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
    suspend fun workTimesByBranch(branchId: Long, response: (RequestState<BaseResponse<List<WorkTime>>>) -> Unit)
    suspend fun insertWorkTime(request: WorkTimeRequest, response: (RequestState<BaseResponse<WorkTime>>) -> Unit)
    suspend fun updateWorkTime(request: WorkTimeRequest, response: (RequestState<BaseResponse<WorkTime>>) -> Unit)
    suspend fun deleteWorkTime(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
    suspend fun insertLink(request: LinkRequest, response: (RequestState<BaseResponse<Link>>) -> Unit)
    suspend fun updateLink(request: LinkRequest, response: (RequestState<BaseResponse<Link>>) -> Unit)
    suspend fun deleteLink(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
    suspend fun updateProviderServices(request: ProviderServicesRequest, response: (RequestState<BaseResponse<ProviderModel>>) -> Unit)
    suspend fun providersByServices(serviceIds: List<Long>, response: (RequestState<BaseResponse<List<ProviderModel>>>) -> Unit)
    suspend fun offersByServices(serviceIds: List<Long>, response: (RequestState<BaseResponse<List<OfferResponse>>>) -> Unit)
    suspend fun setAdStatus(id: Long, status: String, response: (RequestState<BaseResponse<Ads>>) -> Unit)
    suspend fun trackAdImpression(id: Long, response: (RequestState<BaseResponse<Ads>>) -> Unit)
    suspend fun trackAdClick(id: Long, response: (RequestState<BaseResponse<Ads>>) -> Unit)
    suspend fun unreadMessageCount(response: (RequestState<BaseResponse<Int>>) -> Unit)
    suspend fun isUserOnline(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
    suspend fun presence(userIds: List<Long>, response: (RequestState<BaseResponse<Map<String, Boolean>>>) -> Unit)
    suspend fun onlineUsers(response: (RequestState<BaseResponse<List<Long>>>) -> Unit)
    suspend fun agoraToken(request: AgoraTokenRequest, response: (RequestState<BaseResponse<AgoraTokenResponse>>) -> Unit)
    suspend fun deleteAccount(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
}
