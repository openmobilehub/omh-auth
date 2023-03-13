package com.github.authnongms.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object ThreadUtils {

    val isOnMainThread: Boolean
        get() = Thread.currentThread() === runBlocking(Dispatchers.Main.immediate) {
            Thread.currentThread()
        }

}
