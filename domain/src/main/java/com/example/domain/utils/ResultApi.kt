package com.example.domain.utils
sealed interface ResultApi<out R> {
    data object Loading: ResultApi<Nothing>
    data class Success<out T>(val data: T) : ResultApi<T>
    data class Error<out T>(val message: String,val code:Int = -1,val cause: Throwable? = null) : ResultApi<T>
}
