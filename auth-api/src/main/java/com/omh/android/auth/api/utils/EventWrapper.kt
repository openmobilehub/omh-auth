/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.omh.android.auth.api.utils

/**
 * Handy event wrapper for events with Observables (like LiveData) to avoid reacting once it has been
 * handled.
 */
class EventWrapper<T>(private val content: T) {

    private var hasBeenHandled: Boolean = false

    /**
     * Extracts the content if not handled and marks it as handled.
     *
     * @return null when content has been handled.
     */
    fun getContentIfHandled(): T? {
        if (hasBeenHandled) {
            return null
        }
        hasBeenHandled = true
        return content
    }

    /**
     * Allows you to pick the content without setting it as handled.
     */
    fun peekContent(): T {
        return content
    }

    /**
     * Returns true if the EventWrapper's content had been handled.
     */
    fun isHandled(): Boolean {
        return hasBeenHandled
    }
}
