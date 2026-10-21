package com.wadii.utils

/**
 * Pre-flight checks for every request body in API_REFERENCE.md. Each function returns a map of
 * `field -> message`; an empty map means the request is safe to send. The rules mirror what the backend
 * enforces (so we never send a call that is guaranteed to fail) plus the format rules it leaves to the client.
 *
 * Screens show the messages next to the matching input via `InputField(error = errors["field"])`.
 */
object RequestValidators {

    val DAYS = listOf("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
    private val TIME = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")

    private fun Map<String, String?>.clean(): Map<String, String> =
        entries.mapNotNull { (k, v) -> v?.let { k to it } }.toMap()

    private fun required(value: String?, message: String): String? =
        if (value.isNullOrBlank()) message else null

    private fun positiveId(value: Long?, message: String): String? =
        if (value == null || value <= 0) message else null

    // ---------- auth ----------

    /** POST /auth/login */
    fun login(email: String, password: String) = mapOf(
        "email" to Validation.email(email),
        "password" to required(password, "Password is required")
    ).clean()

    /** POST /auth/register (see also the sign-up screen, which adds first/last name rules). */
    fun register(firstName: String, lastName: String, email: String, password: String, phone: String, cityId: Long, providerName: String?, isProvider: Boolean) = mapOf(
        "firstName" to Validation.name(firstName, "First name"),
        "lastName" to Validation.name(lastName, "Last name"),
        "email" to Validation.email(email),
        "password" to Validation.password(password),
        "phone" to Validation.phone(phone),
        "city" to positiveId(cityId, "Please choose your area"),
        "providerName" to if (isProvider) required(providerName, "Shop name is required") else null
    ).clean()

    /** POST /users/update */
    fun updateUser(id: Long, cityId: Long, firstName: String, lastName: String, phone: String) = mapOf(
        "id" to positiveId(id, "We couldn't identify your account. Please sign in again."),
        "city" to positiveId(cityId, "Please choose your area"),
        "firstName" to Validation.name(firstName, "First name"),
        "lastName" to Validation.name(lastName, "Last name"),
        "phone" to Validation.phone(phone)
    ).clean()

    // ---------- orders & proposals ----------

    /** POST /orders/insert and /orders/update */
    fun order(carModelYear: String, date: String?, serviceIds: Collection<Long>, servicesAvailable: Boolean, orderId: Int? = null) = mapOf(
        "id" to if (orderId != null && orderId <= 0) "Order id is required" else null,
        "car" to required(carModelYear, "Tell us the make, model and year, e.g. Toyota Camry 2022"),
        "date" to when {
            date == null -> null
            date.isBlank() -> "Pick the date you need this done"
            date < yesterdayIso() -> "The date can't be in the past"
            else -> null
        },
        "services" to if (servicesAvailable && serviceIds.isEmpty()) "Pick at least one service so the right sellers see your request" else null
    ).clean()

    /** POST /orders/cancel */
    fun orderCancel(orderId: Int?, reasonId: Long?) = mapOf(
        "order" to if (orderId == null || orderId <= 0) "Order id is required" else null,
        "reason" to positiveId(reasonId, "Please choose a reason")
    ).clean()

    /** POST /responses/insert and /responses/update. `prices` are the raw text boxes, one per requested part. */
    fun response(comment: String, orderId: Int, prices: List<String>, responseId: Int? = null) = buildMap<String, String?> {
        put("id", if (responseId != null && responseId <= 0) "Response id is required" else null)
        put("comment", required(comment, "Write a short note for the buyer (availability, timing)"))
        put("order", if (orderId <= 0) "Order id is required" else null)
        prices.forEachIndexed { i, raw ->
            if (raw.isBlank()) return@forEachIndexed // a price is optional per part
            val v = raw.toDoubleOrNull()
            put("price$i", when {
                v == null -> "Enter a number, e.g. 250"
                v < 0 -> "Price can't be negative"
                v > 10_000_000 -> "That price looks too high"
                else -> null
            })
        }
    }.clean()

    /** POST /responses/accept-response and /cancel-response */
    fun responseDecision(responseId: Int) = mapOf(
        "id" to if (responseId <= 0) "Response id is required" else null
    ).clean()

    // ---------- messaging, notifications, rate ----------

    /** POST /messages/insert */
    fun message(text: String, type: String, toUserId: Long) = mapOf(
        "text" to required(text, "Type a message first"),
        "type" to required(type, "Message type is required"),
        "toUserId" to positiveId(toUserId, "Choose who to send this to")
    ).clean()

    /** POST /rate/insert (needs the id of a completed order) and /rate/update (needs the rate id). */
    fun rate(rate: Double, comment: String, orderId: Long? = null, rateId: Long? = null) = mapOf(
        "order" to if (rateId == null && (orderId == null || orderId <= 0)) "Rate from one of your completed orders" else null,
        "id" to if (rateId != null && rateId <= 0) "Rate id is required" else null,
        "rate" to if (rate < 1.0 || rate > 5.0) "Pick 1 to 5 stars" else null,
        "comment" to if (comment.length > 2000) "Comment is too long (2000 characters max)" else null
    ).clean()

    // ---------- admin catalog: names ----------

    /** services, car-types, common-spare-parts, countries (insert/update) */
    fun name(name: String, label: String = "Name", id: Long? = null) = mapOf(
        "id" to if (id != null && id <= 0) "$label id is required" else null,
        "name" to required(name, "$label is required")
    ).clean()

    /** POST /reasons/insert and /update */
    fun reason(reason: String, id: Long? = null) = mapOf(
        "id" to if (id != null && id <= 0) "Reason id is required" else null,
        "reason" to required(reason, "Reason is required")
    ).clean()

    /** provinces: needs a country; cities: needs a province */
    fun child(name: String, parentId: Long, parentLabel: String, id: Long? = null) = mapOf(
        "id" to if (id != null && id <= 0) "Id is required" else null,
        "name" to required(name, "Name is required"),
        "parent" to positiveId(parentId, "Choose a $parentLabel")
    ).clean()

    // ---------- provider shop ----------

    /** POST /offers/insert and /update (endDate must be yyyy-MM-dd and not in the past) */
    fun offer(title: String, description: String, endDate: String, providerId: Long, id: Long? = null) = mapOf(
        "id" to if (id != null && id <= 0) "Offer id is required" else null,
        "title" to required(title, "Title is required"),
        "description" to required(description, "Description is required"),
        "endDate" to when {
            endDate.isBlank() -> "Pick when the deal ends"
            !Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(endDate) -> "Use the date picker (yyyy-mm-dd)"
            endDate < todayIso() -> "The end date can't be in the past"
            else -> null
        },
        "provider" to positiveId(providerId, "We couldn't find your seller profile")
    ).clean()

    /** POST /branches/insert and /update */
    fun branch(name: String, address: String, providerId: Long, id: Long? = null) = mapOf(
        "id" to if (id != null && id <= 0) "Branch id is required" else null,
        "name" to required(name, "Branch name is required"),
        "address" to required(address, "Address is required"),
        "provider" to positiveId(providerId, "We couldn't find your seller profile")
    ).clean()

    /** POST /work-time/insert and /update */
    fun workTime(branchId: Long, day: String, startTime: String, closeTime: String, id: Long? = null) = mapOf(
        "id" to if (id != null && id <= 0) "Work time id is required" else null,
        "branch" to positiveId(branchId, "Choose a branch"),
        "day" to when {
            day.isBlank() -> "Choose a day"
            day !in DAYS -> "Choose a day from the list"
            else -> null
        },
        "startTime" to when {
            startTime.isBlank() -> "Opening time is required"
            !TIME.matches(startTime) -> "Use 24-hour time, e.g. 09:00"
            else -> null
        },
        "closeTime" to when {
            closeTime.isBlank() -> "Closing time is required"
            !TIME.matches(closeTime) -> "Use 24-hour time, e.g. 17:00"
            TIME.matches(startTime) && closeTime <= startTime -> "Closing time must be after opening time"
            else -> null
        }
    ).clean()

    /** POST /links/insert and /update (links must also be unique server-side) */
    fun link(link: String, providerId: Long, id: Long? = null) = mapOf(
        "id" to if (id != null && id <= 0) "Link id is required" else null,
        "link" to Validation.url(link, required = true),
        "provider" to positiveId(providerId, "We couldn't find your seller profile")
    ).clean()

    /** POST /providers/update-services */
    fun providerServices(providerId: Long, serviceIds: Collection<Long>) = mapOf(
        "provider" to positiveId(providerId, "We couldn't find your seller profile"),
        "services" to if (serviceIds.isEmpty()) "Pick at least one service" else null
    ).clean()

    /** POST /provider-requests/request (multipart). Image rules live in [Validation.image]. */
    fun providerRequest(userId: Long?, name: String, address: String, phone: String, serviceIds: Collection<Long>, links: List<String>) = buildMap<String, String?> {
        put("user", if (userId == null || userId <= 0) "Please sign in again" else null)
        put("name", required(name, "Shop name is required"))
        put("address", required(address, "Address is required"))
        put("phone", Validation.phone(phone))
        put("services", if (serviceIds.isEmpty()) "Pick at least one service you sell" else null)
        put("links", links.firstNotNullOfOrNull { Validation.url(it) })
    }.clean()

    /** POST /providers/create-by-admin */
    fun createProviderByAdmin(
        email: String, password: String, providerName: String, phone: String, cityId: Long?,
        branchNames: List<String>, links: List<String>
    ) = mapOf(
        "email" to Validation.email(email),
        "password" to Validation.password(password),
        "providerName" to required(providerName, "Provider name is required"),
        "phone" to Validation.phone(phone, required = false),
        "city" to if (cityId != null && cityId <= 0) "Choose a valid area" else null,
        "branches" to if (branchNames.any { it.isBlank() }) "Every branch needs a name" else null,
        "links" to links.firstNotNullOfOrNull { Validation.url(it) }
    ).clean()

    // ---------- calls ----------

    /** POST /agora/token and /livekit/token: room is `<smallerId>_<largerId>` and the caller must be one of them. */
    fun callRoom(room: String, myId: Long): Map<String, String> {
        val parts = room.split("_")
        val ids = parts.mapNotNull { it.toLongOrNull() }
        return mapOf(
            "room" to when {
                room.isBlank() -> "Call room is required"
                parts.size != 2 || ids.size != 2 -> "Call room must look like 3_7"
                ids[0] > ids[1] -> "Call room ids must be in ascending order"
                myId !in ids -> "You are not part of this call"
                else -> null
            }
        ).clean()
    }

    // ---------- ads (admin) ----------

    /** POST /advertisements/insert and /update */
    fun ad(title: String, imageUrl: String, targetUrl: String, startDate: String, endDate: String, priority: String, id: Long? = null) = mapOf(
        "id" to if (id != null && id <= 0) "Advertisement id is required" else null,
        "title" to required(title, "Title is required"),
        "imageUrl" to Validation.url(imageUrl),
        "targetUrl" to Validation.url(targetUrl),
        "dates" to Validation.dateRange(startDate, endDate),
        "priority" to priority.toIntOrNull().let { if (it == null || it !in 0..100) "Priority is a whole number from 0 to 100" else null }
    ).clean()
}

/** First message to show in a toast when a form has several problems. */
fun Map<String, String>.firstMessage(): String = values.firstOrNull() ?: ""
