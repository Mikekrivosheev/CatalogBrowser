package com.mikhailkrivosheev.catalogbrowser.data

sealed class ResultData<T : Any> {
    class Success<T : Any>(val data: T) : ResultData<T>()
    class Exception<T : Any>(val e: Throwable) : ResultData<T>()
}