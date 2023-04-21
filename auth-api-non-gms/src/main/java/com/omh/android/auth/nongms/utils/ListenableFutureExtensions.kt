package com.omh.android.auth.nongms.utils

import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhAuthStatusCodes
import java.io.IOException
import java.lang.RuntimeException
import java.util.concurrent.Executor
import retrofit2.HttpException

fun <T> ListenableFuture<T>.addListeners(
    onSuccess: (T) -> Unit = {},
    onError: (OmhAuthException) -> Unit = {},
    executor: Executor
) {
    val futureCallback = object : FutureCallback<T> {
        override fun onSuccess(result: T) {
            onSuccess(result)
        }

        override fun onFailure(t: Throwable) {
            onError(t.toOmhException())
        }
    }
    Futures.addCallback(this, futureCallback, executor)
}

private fun Throwable.toOmhException(): OmhAuthException {
    val statusCode = when (this) {
        is HttpException -> OmhAuthStatusCodes.HTTPS_ERROR
        is IOException -> OmhAuthStatusCodes.NETWORK_ERROR
        is RuntimeException -> OmhAuthStatusCodes.INTERNAL_ERROR
        else -> OmhAuthStatusCodes.DEFAULT_ERROR
    }
    return OmhAuthException.ApiException(statusCode, cause)
}
