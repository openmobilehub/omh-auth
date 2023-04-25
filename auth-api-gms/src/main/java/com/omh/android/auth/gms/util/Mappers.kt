package com.omh.android.auth.gms.util

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhAuthStatusCodes


internal fun Task<Void>.mapToOmhExceptions(): Task<Unit> {
    val completionSource = TaskCompletionSource<Unit>()
    try {
        getResult(ApiException::class.java)
        completionSource.setResult(Unit)
    } catch (e: ApiException) {
        completionSource.setException(e.toOmhApiException())
    }
    return completionSource.task
}


internal fun Exception.toOmhApiException(): OmhAuthException.ApiException {
    val apiException: ApiException? = this as? ApiException
    val statusCode: Int = when (apiException?.statusCode) {
        CommonStatusCodes.API_NOT_CONNECTED -> OmhAuthStatusCodes.GMS_UNAVAILABLE
        else -> OmhAuthStatusCodes.INTERNAL_ERROR
    }
    return OmhAuthException.ApiException(
        statusCode = statusCode,
        cause = this
    )
}
