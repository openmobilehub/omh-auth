package com.omh.android.auth.nongms.presentation

import com.omh.android.auth.api.OmhCancellable
import com.omh.android.auth.api.OmhTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OmhNonGmsTask<T>(private val task: suspend () -> T) : OmhTask<T>() {

    private val coroutineContext = Dispatchers.Main + SupervisorJob()
    private val customScope: CoroutineScope = CoroutineScope(context = coroutineContext)

    @SuppressWarnings("TooGenericExceptionCaught")
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
        return OmhNonGmsCancellable { coroutineContext.cancelChildren() }
    }
}

class OmhNonGmsCancellable(private val cancellationAction: () -> Unit) : OmhCancellable {
    override fun cancel() = cancellationAction()
}
