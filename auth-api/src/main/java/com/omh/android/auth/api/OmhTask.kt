package com.omh.android.auth.api

abstract class OmhTask<T> {

    protected var onSuccess: ((T) -> Unit)? = null
    protected var onFailure: ((Exception) -> Unit)? = null

    fun addOnSuccess(onSuccess: (T) -> Unit): OmhTask<T> {
        this.onSuccess = onSuccess
        return this
    }

    fun addOnFailure(onFailure: (Exception) -> Unit): OmhTask<T> {
        this.onFailure = onFailure
        return this
    }

    abstract fun execute(): OmhCancellable
}