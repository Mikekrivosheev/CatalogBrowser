package com.mikhailkrivosheev.catalogbrowser.domain

import com.mikhailkrivosheev.catalogbrowser.data.ResultData

class Paginator<Key, Item : Any>(
    private val initialKey: Key?,
    private inline val onLoadUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: Key?) -> ResultData<List<Item>>,
    private inline val getNextKey: suspend (key: Key?, List<Item>) -> Key?,
    private inline val onError: suspend (String) -> Unit,
    private inline val onSuccess: suspend (items: List<Item>, newKey: Key?) -> Unit,
    private inline val onReset: () -> Unit,
) {

    private var currentKey = initialKey
    private var isMakingRequest = false

    suspend fun loadNextItems() {
        if (isMakingRequest) {
            return
        }
        isMakingRequest = true
        onLoadUpdated(true)
        val result = onRequest(currentKey)
        isMakingRequest = false
        when (result) {
            is ResultData.Success -> {
                currentKey = getNextKey(currentKey, result.data)
                onSuccess(result.data, currentKey)
            }

            is ResultData.Exception -> {
                onError(result.e.message.orEmpty())
            }
        }

        onLoadUpdated(false)
    }

    fun reset() {
        onReset()
        isMakingRequest = false
        currentKey = initialKey
    }
}