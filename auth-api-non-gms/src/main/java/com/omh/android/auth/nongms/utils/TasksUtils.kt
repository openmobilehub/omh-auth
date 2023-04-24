package com.omh.android.auth.nongms.utils

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.omh.android.auth.api.models.OmhAuthException


internal fun <T, R> Task<T>.map(action: (T) -> R): Task<R> {
    return this.continueWithTask { completedTask ->
        val completionSource = TaskCompletionSource<R>()
        try {
            val result = completedTask.getResult(OmhAuthException::class.java)
            val newResult = action(result)
            completionSource.setResult(newResult)
        } catch (omhException: OmhAuthException) {
            completionSource.setException(omhException)
        }
        completionSource.task
    }
}
