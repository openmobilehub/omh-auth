package com.omh.android.auth.nongms.utils.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

internal object LifecycleUtil {

    fun runOnResume(lifecycle: Lifecycle, owner: LifecycleOwner, action: () -> Unit) {
        val observer: LifecycleEventObserver = getObserver(action, owner)
        lifecycle.addObserver(observer)
    }

    private fun getObserver(action: () -> Unit, owner: LifecycleOwner): LifecycleEventObserver {
        return OnLifecycleStateObserver { source, event, observer ->
            runOnState(event, source, owner, observer, action)
        }
    }

    private fun runOnState(
        event: Lifecycle.Event,
        source: LifecycleOwner,
        owner: LifecycleOwner,
        observer: LifecycleEventObserver,
        action: () -> Unit
    ) {
        val isOnResume = event == Lifecycle.Event.ON_RESUME
        val isRedirectActivity = source == owner
        if (isOnResume && isRedirectActivity) {
            action()
            source.lifecycle.removeObserver(observer)
        }
    }
}
