package com.omh.android.auth.nongms.data.utils.retrofit.adapter.tasks

import com.google.android.gms.tasks.Task
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit

internal class TasksCallAdapterFactory : CallAdapter.Factory() {

    @SuppressWarnings("ReturnCount")
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Task::class.java) return null
        check(returnType is ParameterizedType) { "Response type must be a parameterized type." }

        val resultType = getParameterUpperBound(0, returnType)
        return TasksCallAdapter<Any>(resultType)
    }
}
