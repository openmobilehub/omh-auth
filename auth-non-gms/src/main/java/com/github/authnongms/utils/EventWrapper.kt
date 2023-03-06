package com.github.authnongms.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class EventWrapper<T>(private val content: T) {
    var hasBeenHandled: Boolean = false

    fun getContentIfHandled(): T? {
        if (hasBeenHandled) {
            return null
        }
        hasBeenHandled = true
        return content
    }

    fun peekContent(): T {
        return content
    }
}

@OptIn(ExperimentalContracts::class)
fun EventWrapper<*>?.nullOrHandled(): Boolean {
    contract {
        returns(false) implies (this@nullOrHandled != null)
    }
    return this == null || hasBeenHandled
}
