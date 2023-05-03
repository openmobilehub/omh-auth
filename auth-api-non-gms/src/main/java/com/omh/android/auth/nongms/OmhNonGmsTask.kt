package com.omh.android.auth.nongms

import com.omh.android.auth.api.OmhCancellable
import com.omh.android.auth.api.OmhTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OmhNonGmsTask<T>(private val task: suspend () -> T) : OmhTask<T>() {

    private val customScope: CoroutineScope = CoroutineScope(context = Dispatchers.Main)

    override fun execute(): OmhCancellable {
        customScope.launch {
            try {
                val result = task()
                withContext(Dispatchers.Main) {
                    onSuccess?.invoke(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure?.invoke(e)
                }
            }
        }
        return OmhNonGmsCancellable { customScope.cancel() }
    }
}

class OmhNonGmsCancellable(private val cancellationAction: () -> Unit) : OmhCancellable {
    override fun cancel() {
        cancellationAction()
    }
}