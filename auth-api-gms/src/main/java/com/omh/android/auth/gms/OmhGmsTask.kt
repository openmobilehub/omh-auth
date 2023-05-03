package com.omh.android.auth.gms

import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.omh.android.auth.api.OmhCancellable
import com.omh.android.auth.api.OmhTask

class OmhGmsTask<T>(private val task: Task<T>) : OmhTask<T>() {

    override fun execute(): OmhCancellable {
        task
            .addOnSuccessListener { result -> onSuccess?.invoke(result) }
            .addOnFailureListener { e -> onFailure?.invoke(e) }

        val cancellationTokenSource = CancellationTokenSource()
        val completionSource = TaskCompletionSource<T>(cancellationTokenSource.token)
        return OmhGmsCancellable(cancellationTokenSource)
    }
}

class OmhGmsCancellable(
    private val cancellationTokenSource: CancellationTokenSource
) : OmhCancellable {

    override fun cancel() {
        cancellationTokenSource.cancel()
    }
}