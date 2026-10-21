package com.wadii.utils

/** Today as `yyyy-MM-dd` (UTC), comparable with the date part of the API's ISO timestamps. */
fun todayIso(): String = js("new Date().toISOString()").toString().take(10)

/** True once the deal's end date is in the past. A blank/unparseable date is treated as not expired. */
fun isExpired(endDate: String): Boolean {
    val day = endDate.take(10)
    return day.length == 10 && day < todayIso()
}

/** Time-aware greeting so the home screen feels personal. */
fun greeting(): String {
    val hour = js("new Date().getHours()") as Int
    return when {
        hour < 5 -> "Good evening"
        hour < 12 -> "Good morning"
        hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }
}

/** The earliest date an order may carry: the API allows one day of tolerance. */
fun yesterdayIso(): String = js("new Date(Date.now() - 86400000).toISOString()").toString().take(10)
