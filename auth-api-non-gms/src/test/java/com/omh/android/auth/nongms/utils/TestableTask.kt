package com.omh.android.auth.nongms.utils

import android.app.Activity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.SuccessContinuation
import com.google.android.gms.tasks.Task
import io.mockk.mockk
import java.util.concurrent.Executor

class TestableTask<T>(
    private val expectedResult: T,
    private val isCanceled: Boolean = false,
    private val isSuccessful: Boolean = true,
    private val isComplete: Boolean = true,
) : Task<T>() {

    override fun addOnFailureListener(p0: OnFailureListener): Task<T> {
        TODO("Not yet implemented")
    }

    override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<T> {
        TODO("Not yet implemented")
    }

    override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<T> {
        TODO("Not yet implemented")
    }

    override fun getException(): Exception {
        return mockk()
    }

    override fun getResult(): T {
        return expectedResult
    }

    override fun <X : Throwable?> getResult(p0: Class<X>): T {
        TODO("Not yet implemented")
    }

    override fun isCanceled(): Boolean {
        return isCanceled
    }

    override fun isComplete(): Boolean {
        return isComplete
    }

    override fun isSuccessful(): Boolean {
        return isSuccessful
    }

    override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in T>): Task<T> {
        TODO("Not yet implemented")
    }

    override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in T>): Task<T> {
        TODO("Not yet implemented")
    }

    override fun addOnSuccessListener(p0: OnSuccessListener<in T>): Task<T> {
        TODO("Not yet implemented")
    }

    override fun <TContinuationResult : Any?> onSuccessTask(
        p0: SuccessContinuation<T, TContinuationResult>
    ): Task<TContinuationResult> {
        if (!isSuccessful) return mockk()
        return p0.then(expectedResult)
    }
}
