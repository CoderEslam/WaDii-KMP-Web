package com.wadii.domain.usecase

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
import com.wadii.domain.repo.MarketplaceRepo
import com.wadii.state.AppState
import com.wadii.utils.RequestState
import com.wadii.utils.RequestValidators
import com.wadii.utils.StatusCode
import com.wadii.utils.firstMessage

/**
 * Every call that carries a request body is validated here BEFORE it reaches the network. If the body is
 * invalid the callback receives `RequestState.Error(<first problem>)` and nothing is sent, so the UI shows the
 * problem exactly as it would show a server error. Screens that want per-field messages call
 * [RequestValidators] themselves first and use this as a safety net.
 */
class MarketplaceUseCase(private val repo: MarketplaceRepo) {

    private inline fun <T : Any> guarded(
        errors: Map<String, String>,
        response: (RequestState<BaseResponse<T>>) -> Unit,
        send: () -> Unit
    ) {
        if (errors.isEmpty()) send()
        else response(RequestState.Error(errors.firstMessage(), StatusCode.BadRequest))
    }

    private fun positive(id: Long, what: String) =
        if (id <= 0) mapOf("id" to "$what is required") else emptyMap()

    private fun positive(id: Int, what: String) = positive(id.toLong(), what)

    // ---- orders & proposals ----

    suspend fun updateOrder(request: OrderRequest, response: (RequestState<BaseResponse<OrderModel>>) -> Unit) =
        guarded(
            RequestValidators.order(
                carModelYear = request.carModelYear,
                date = request.date.takeIf { it.isNotBlank() },
                serviceIds = request.servicesIds,
                servicesAvailable = false, // the edit form can't tell if services exist; the server keeps the old set when empty
                orderId = request.id.toIntOrNull() ?: 0
            ),
            response
        ) { repo.updateOrder(request, response) }

    suspend fun deleteOrder(id: Int, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        guarded(positive(id, "Order id"), response) { repo.deleteOrder(id, response) }

    suspend fun updateResponse(request: ResponseRequest, response: (RequestState<BaseResponse<ResponseCallback>>) -> Unit) =
        guarded(
            RequestValidators.response(
                comment = request.comment,
                orderId = request.orderId.takeIf { it > 0 } ?: 1, // orderId is ignored on update
                prices = request.sparePartsPrice.map { it.price.toString() },
                responseId = request.id
            ),
            response
        ) { repo.updateResponse(request, response) }

    suspend fun deleteResponse(id: Int, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        guarded(positive(id, "Response id"), response) { repo.deleteResponse(id, response) }

    // ---- notifications ----

    suspend fun markNotificationRead(id: Long, response: (RequestState<BaseResponse<UserNotification>>) -> Unit) =
        guarded(positive(id, "Notification id"), response) { repo.markNotificationRead(id, response) }

    suspend fun markAllNotificationsRead(response: (RequestState<BaseResponse<List<UserNotification>>>) -> Unit) =
        repo.markAllNotificationsRead(response)

    suspend fun deleteNotification(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        guarded(positive(id, "Notification id"), response) { repo.deleteNotification(id, response) }

    // ---- admin: users ----

    suspend fun getAllUsers(response: (RequestState<BaseResponse<List<User>>>) -> Unit) = repo.getAllUsers(response)

    // ---- order progress & availability ----

    suspend fun startOrder(id: Int, response: (RequestState<BaseResponse<OrderModel>>) -> Unit) =
        guarded(positive(id, "Order id"), response) { repo.startOrder(id, response) }

    suspend fun completeOrder(id: Int, response: (RequestState<BaseResponse<OrderModel>>) -> Unit) =
        guarded(positive(id, "Order id"), response) { repo.completeOrder(id, response) }

    suspend fun setAvailability(acceptingOrders: Boolean, response: (RequestState<BaseResponse<ProviderModel>>) -> Unit) =
        repo.setAvailability(AvailabilityRequest(acceptingOrders), response)

    suspend fun setProviderActive(id: Long, active: Boolean, response: (RequestState<BaseResponse<ProviderModel>>) -> Unit) =
        guarded(positive(id, "Provider id"), response) { repo.setProviderActive(id, active, response) }

    // ---- rate ----

    suspend fun insertRate(request: RateRequest, response: (RequestState<BaseResponse<RateModel>>) -> Unit) =
        guarded(RequestValidators.rate(request.rate, request.comment, orderId = request.orderId), response) {
            repo.insertRate(request, response)
        }

    suspend fun updateRate(request: RateRequest, response: (RequestState<BaseResponse<RateModel>>) -> Unit) =
        guarded(RequestValidators.rate(request.rate, request.comment, rateId = request.id ?: 0), response) {
            repo.updateRate(request, response)
        }

    suspend fun deleteRate(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        guarded(positive(id, "Rate id"), response) { repo.deleteRate(id, response) }

    // ---- admin catalog ----

    suspend fun insertCarType(request: NameRequest, response: (RequestState<BaseResponse<CarType>>) -> Unit) =
        guarded(RequestValidators.name(request.name, "Car name"), response) { repo.insertCarType(request, response) }

    suspend fun updateCarType(request: NameRequest, response: (RequestState<BaseResponse<CarType>>) -> Unit) =
        guarded(RequestValidators.name(request.name, "Car name", request.id ?: 0), response) { repo.updateCarType(request, response) }

    suspend fun deleteCarType(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        guarded(positive(id, "Car id"), response) { repo.deleteCarType(id, response) }

    suspend fun getCommonSpareParts(response: (RequestState<BaseResponse<List<CommonSparePart>>>) -> Unit) =
        repo.getCommonSpareParts(response)

    suspend fun insertCommonSparePart(request: NameRequest, response: (RequestState<BaseResponse<CommonSparePart>>) -> Unit) =
        guarded(RequestValidators.name(request.name, "Part name"), response) { repo.insertCommonSparePart(request, response) }

    suspend fun updateCommonSparePart(request: NameRequest, response: (RequestState<BaseResponse<CommonSparePart>>) -> Unit) =
        guarded(RequestValidators.name(request.name, "Part name", request.id ?: 0), response) { repo.updateCommonSparePart(request, response) }

    suspend fun deleteCommonSparePart(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        guarded(positive(id, "Part id"), response) { repo.deleteCommonSparePart(id, response) }

    // ---- provider shop ----

    suspend fun branchesByProvider(providerId: Long, response: (RequestState<BaseResponse<List<Branche>>>) -> Unit) =
        guarded(positive(providerId, "Provider id"), response) { repo.branchesByProvider(providerId, response) }

    suspend fun insertBranch(request: BranchRequest, response: (RequestState<BaseResponse<Branche>>) -> Unit) =
        guarded(RequestValidators.branch(request.name, request.address, request.providerId), response) { repo.insertBranch(request, response) }

    suspend fun updateBranch(request: BranchRequest, response: (RequestState<BaseResponse<Branche>>) -> Unit) =
        guarded(RequestValidators.branch(request.name, request.address, request.providerId, request.id ?: 0), response) { repo.updateBranch(request, response) }

    suspend fun deleteBranch(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        guarded(positive(id, "Branch id"), response) { repo.deleteBranch(id, response) }

    suspend fun workTimesByBranch(branchId: Long, response: (RequestState<BaseResponse<List<WorkTime>>>) -> Unit) =
        guarded(positive(branchId, "Branch id"), response) { repo.workTimesByBranch(branchId, response) }

    suspend fun insertWorkTime(request: WorkTimeRequest, response: (RequestState<BaseResponse<WorkTime>>) -> Unit) =
        guarded(RequestValidators.workTime(request.branchId, request.day, request.startTime, request.closeTime), response) {
            repo.insertWorkTime(request, response)
        }

    suspend fun updateWorkTime(request: WorkTimeRequest, response: (RequestState<BaseResponse<WorkTime>>) -> Unit) =
        guarded(RequestValidators.workTime(request.branchId, request.day, request.startTime, request.closeTime, request.id ?: 0), response) {
            repo.updateWorkTime(request, response)
        }

    suspend fun deleteWorkTime(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        guarded(positive(id, "Work time id"), response) { repo.deleteWorkTime(id, response) }

    suspend fun insertLink(request: LinkRequest, response: (RequestState<BaseResponse<Link>>) -> Unit) =
        guarded(RequestValidators.link(request.link, request.providerId), response) { repo.insertLink(request, response) }

    suspend fun updateLink(request: LinkRequest, response: (RequestState<BaseResponse<Link>>) -> Unit) =
        guarded(RequestValidators.link(request.link, request.providerId, request.id ?: 0), response) { repo.updateLink(request, response) }

    suspend fun deleteLink(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        guarded(positive(id, "Link id"), response) { repo.deleteLink(id, response) }

    suspend fun updateProviderServices(request: ProviderServicesRequest, response: (RequestState<BaseResponse<ProviderModel>>) -> Unit) =
        guarded(RequestValidators.providerServices(request.providerId, request.serviceIds), response) {
            repo.updateProviderServices(request, response)
        }

    suspend fun providersByServices(serviceIds: List<Long>, response: (RequestState<BaseResponse<List<ProviderModel>>>) -> Unit) =
        guarded(if (serviceIds.isEmpty()) mapOf("services" to "Pick at least one service") else emptyMap(), response) {
            repo.providersByServices(serviceIds, response)
        }

    suspend fun offersByServices(serviceIds: List<Long>, response: (RequestState<BaseResponse<List<OfferResponse>>>) -> Unit) =
        guarded(if (serviceIds.isEmpty()) mapOf("services" to "Pick at least one service") else emptyMap(), response) {
            repo.offersByServices(serviceIds, response)
        }

    // ---- ads ----

    suspend fun setAdStatus(id: Long, status: String, response: (RequestState<BaseResponse<Ads>>) -> Unit) =
        guarded(
            positive(id, "Advertisement id") +
                if (status !in listOf("ACTIVE", "INACTIVE", "EXPIRED")) mapOf("status" to "Status must be ACTIVE, INACTIVE or EXPIRED") else emptyMap(),
            response
        ) { repo.setAdStatus(id, status, response) }

    suspend fun trackAdImpression(id: Long, response: (RequestState<BaseResponse<Ads>>) -> Unit) =
        guarded(positive(id, "Advertisement id"), response) { repo.trackAdImpression(id, response) }

    suspend fun trackAdClick(id: Long, response: (RequestState<BaseResponse<Ads>>) -> Unit) =
        guarded(positive(id, "Advertisement id"), response) { repo.trackAdClick(id, response) }

    // ---- users, presence, calls ----

    suspend fun unreadMessageCount(response: (RequestState<BaseResponse<Int>>) -> Unit) = repo.unreadMessageCount(response)

    suspend fun isUserOnline(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        guarded(positive(id, "User id"), response) { repo.isUserOnline(id, response) }

    suspend fun presence(userIds: List<Long>, response: (RequestState<BaseResponse<Map<String, Boolean>>>) -> Unit) =
        guarded(if (userIds.isEmpty()) mapOf("userIds" to "Provide at least one user id") else emptyMap(), response) {
            repo.presence(userIds, response)
        }

    suspend fun onlineUsers(response: (RequestState<BaseResponse<List<Long>>>) -> Unit) = repo.onlineUsers(response)

    suspend fun agoraToken(request: AgoraTokenRequest, response: (RequestState<BaseResponse<AgoraTokenResponse>>) -> Unit) =
        guarded(RequestValidators.callRoom(request.channelName, AppState.user?.id ?: 0), response) {
            if (request.uid.toLong() != (AppState.user?.id ?: -1L)) {
                response(RequestState.Error("uid must be your own user id", StatusCode.BadRequest))
            } else repo.agoraToken(request, response)
        }

    suspend fun deleteAccount(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        guarded(positive(id, "User id"), response) { repo.deleteAccount(id, response) }
}
