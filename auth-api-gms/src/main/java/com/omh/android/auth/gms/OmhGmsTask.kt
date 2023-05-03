package com.omh.android.auth.gms

import com.google.android.gms.tasks.Task
import com.omh.android.auth.api.OmhCancellable
import com.omh.android.auth.api.OmhTask

class OmhGmsTask<T>(private val task: Task<T>) : OmhTask<T>() {

    override fun execute(): OmhCancellable? {
        task.addOnSuccessListener { result -> onSuccess?.invoke(result) }
            .addOnFailureListener { e -> onFailure?.invoke(e) }
        return null // No way to cancel the task
    }
}
