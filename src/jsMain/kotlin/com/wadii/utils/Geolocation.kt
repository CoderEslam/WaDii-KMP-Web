package com.wadii.utils

import kotlinx.coroutines.suspendCancellableCoroutine

external interface GeolocationCoordinates {
    val latitude: Double
    val longitude: Double
}

external interface GeolocationPosition {
    val coords: GeolocationCoordinates
}

external interface GeolocationPositionError {
    val code: Int
    val message: String
}

external interface PositionOptions {
    var enableHighAccuracy: Boolean
    var timeout: Int
    var maximumAge: Int
}

external interface Geolocation {
    fun getCurrentPosition(
        success: (GeolocationPosition) -> Unit,
        error: (GeolocationPositionError) -> Unit,
        options: PositionOptions
    )
}

private fun navigatorGeolocation(): Geolocation? =
    js("(navigator.geolocation || null)").unsafeCast<Geolocation?>()

private fun positionOptions(): PositionOptions =
    js("({})").unsafeCast<PositionOptions>().apply {
        enableHighAccuracy = true
        timeout = 10_000
        maximumAge = 60_000
    }

suspend fun getCurrentLocation(): Pair<Double, Double>? {
    val geolocation = navigatorGeolocation() ?: return null
    return suspendCancellableCoroutine { cont ->
        geolocation.getCurrentPosition(
            success = { position ->
                if (cont.isActive) cont.resume(
                    position.coords.latitude to position.coords.longitude
                ) { cause, _, _ -> }
            },
            error = {
                if (cont.isActive) cont.resume(null) { cause, _, _ -> {} }
            },
            options = positionOptions()
        )
    }
}
