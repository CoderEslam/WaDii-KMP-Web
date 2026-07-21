package com.teacheronline.data.api

import kotlin.math.round


fun Double.to1dp(): String {
    val rounded = round(this * 10).toInt()
    return "${rounded / 10}.${rounded % 10}"
}
