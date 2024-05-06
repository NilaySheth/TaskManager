package com.taskmanager.base.network

sealed class NetworkResponse<out T : Any> {
    data class Success<out T : Any>(val data: T) : NetworkResponse<T>()
    data class Error(
        val errorCode: Int,
        val errorMessage: String,
        val additionalData: Any? = null
    ) : NetworkResponse<Nothing>()

    object NetworkError : NetworkResponse<Nothing>()
    object UnknownError : NetworkResponse<Nothing>()
    object ServerUnavailable : NetworkResponse<Nothing>()
    object Unauthorized : NetworkResponse<Nothing>()
}
