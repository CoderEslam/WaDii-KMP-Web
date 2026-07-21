package com.teacheronline.domain.model

import com.teacheronline.utils.RequestState

class Paginator<Key, Item>(
    private val initialKey: Key,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextKey: Key) -> RequestState<Item>,
    private val getNextKey: suspend (currentKey: Key, result: Item) -> Key,
    private val onError: suspend (Throwable?) -> Unit,
    private val onSuccess: suspend (result: Item, newKey: Key) -> Unit,
    private val endReached: (currentKey: Key, result: Item) -> Boolean
) {

    private var currentKey = initialKey
    private var isMakingRequest = false
    private var isEndReached = false

    suspend fun loadNextItems() {
        if (isMakingRequest || isEndReached) {
            return
        }

        isMakingRequest = true
        onLoadUpdated(true)

        val result = onRequest(currentKey)
        isMakingRequest = false

        result.getOrElse(onFailure = {
            onError(it)
            onLoadUpdated(false)
        }, onSuccess = { item ->
            currentKey = getNextKey(currentKey, item)
            onSuccess(item, currentKey)
            onLoadUpdated(false)
            isEndReached = endReached(currentKey, item)
        })
    }

    fun reset() {
        currentKey = initialKey
        isEndReached = false
    }
}