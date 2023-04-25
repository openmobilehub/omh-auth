package com.omh.android.auth.nongms.utils

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhAuthStatusCodes


internal fun <T> createTaskFromCallable(callable: () -> T): Task<T> {
    val completionSource = TaskCompletionSource<T>()
    try {
        completionSource.setResult(callable())
    } catch (e: RuntimeException) {
        val omhException = OmhAuthException.ApiException(
            statusCode = OmhAuthStatusCodes.INTERNAL_ERROR,
            cause = e
        )
        completionSource.setException(omhException)
    }
    return completionSource.task
}