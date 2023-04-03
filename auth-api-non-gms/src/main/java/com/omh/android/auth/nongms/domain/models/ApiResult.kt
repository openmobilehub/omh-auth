package com.omh.android.auth.nongms.domain.models

sealed class ApiResult<out T> {

    data class Success<out R>(val data: R) : ApiResult<R>()

    data class Error(val code: Int, val body: String) : ApiResult<Nothing>()

    data class RuntimeError(val exception: Throwable): ApiResult<Nothing>()

    data class NetworkError(val exception: Throwable): ApiResult<Nothing>()
}
