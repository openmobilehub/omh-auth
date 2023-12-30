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

package com.omh.android.auth.mslive.data.utils

import com.omh.android.auth.mobileweb.data.utils.retrofit.ApiResultCallAdapterFactory
import com.omh.android.auth.mslive.BuildConfig
import com.omh.android.auth.mslive.data.login.MsLiveAuthRest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

internal class MsLiveRetrofitImpl {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) setLevel(HttpLoggingInterceptor.Level.BODY)
            },
        )
        .build()

    private val retrofitClientForAuth = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BuildConfig.MSLIVE_AUTH_URL)
        .addConverterFactory(JacksonConverterFactory.create())
        .addCallAdapterFactory(ApiResultCallAdapterFactory())
        .build()

    val authREST: MsLiveAuthRest = retrofitClientForAuth.create(MsLiveAuthRest::class.java)

    companion object {
        val instance by lazy {
            MsLiveRetrofitImpl()
        }
    }
}
