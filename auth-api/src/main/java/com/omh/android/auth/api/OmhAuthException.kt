package com.omh.android.auth.api

sealed class OmhAuthException : Exception() {

    class RecoverableException(
        override val message: String?,
        override val cause: Throwable? = null
    ) : OmhAuthException()

    class UnrecoverableException(
        override val message: String?,
        override val cause: Throwable? = null
    ) : OmhAuthException()
}
