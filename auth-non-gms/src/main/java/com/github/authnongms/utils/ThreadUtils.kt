package com.github.authnongms.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object ThreadUtils {

    private val isOnMainThread: Boolean
        get() = Thread.currentThread() === runBlocking(Dispatchers.Main.immediate) {
            Thread.currentThread()
        }

    fun checkForMainThread() {
        if (isOnMainThread) {
            error("Running blocking function on main thread.")
        }
    }
}
