package com.mikhailkrivosheev.catalogbrowser.data

sealed class ResultData<T : Any> {
    data class Success<T : Any>(val data: T) : ResultData<T>()
    data class Exception<T : Any>(val e: Throwable) : ResultData<T>()
}