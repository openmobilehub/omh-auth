package com.omh.android.auth.nongms.data.utils.retrofit.adapter.tasks

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhAuthStatusCodes
import java.io.IOException
import java.lang.IllegalStateException
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

internal class TasksCallAdapter<R>(private val successType: Type) : CallAdapter<R, Task<R>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<R>): Task<R> {
        val completionSource = TaskCompletionSource<R>()
        call.enqueue(TaskCallback(completionSource))
        return completionSource.task
    }

    inner class TaskCallback(private val completionSource: TaskCompletionSource<R>) : Callback<R> {
        override fun onResponse(call: Call<R>, response: Response<R>) {
            when {
                // Http error response (4xx - 5xx)
                !response.isSuccessful -> {
                    val httpException = HttpException(response)
                    val omhAuthException = OmhAuthException.ApiException(
                        statusCode = OmhAuthStatusCodes.HTTPS_ERROR,
                        cause = httpException
                    )
                    completionSource.trySetException(omhAuthException)
                }
                // Http success response with body
                response.body() != null -> {
                    completionSource.trySetResult(response.body()!!)
                }
                // if we defined Unit as success type it means we expected no response body
                // e.g. in case of 204 No Content
                successType == Unit::class.java -> {
                    @Suppress("UNCHECKED_CAST")
                    completionSource.trySetResult(Unit as R)
                }

                else -> {
                    val exception = IllegalStateException("Response body was null")
                    val omhAuthException = OmhAuthException.ApiException(
                        statusCode = OmhAuthStatusCodes.INTERNAL_ERROR,
                        cause = exception
                    )
                    completionSource.trySetException(omhAuthException)
                }
            }
        }

        override fun onFailure(call: Call<R>, throwable: Throwable) {
            val statusCode = when (throwable) {
                is IOException -> OmhAuthStatusCodes.NETWORK_ERROR
                else -> OmhAuthStatusCodes.INTERNAL_ERROR
            }
            val omhAuthException = OmhAuthException.ApiException(
                statusCode = statusCode,
                cause = throwable
            )
            completionSource.trySetException(omhAuthException)
        }
    }
}
