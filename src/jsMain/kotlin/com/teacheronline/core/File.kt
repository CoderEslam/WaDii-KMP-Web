package com.teacheronline.core

import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.files.File

suspend fun File.readBytes(): ByteArray = suspendCancellableCoroutine { cont ->
    val reader = org.w3c.files.FileReader()
    reader.onload = {
        val buffer = reader.result as org.khronos.webgl.ArrayBuffer
        val array = org.khronos.webgl.Int8Array(buffer).asDynamic()
        val length = array.length as Int
        cont.resumeWith(Result.success(ByteArray(length) { i -> array[i] as Byte }))
        Unit
    }
    reader.onerror = { cont.resumeWith(Result.success(ByteArray(0))); Unit }
    reader.readAsArrayBuffer(this)
}
