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

package com.omh.android.auth.sample.di

import android.content.Context
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.OmhAuthProvider
import com.omh.android.auth.sample.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
    @Provides
    fun providesOmhAuthClient(@ApplicationContext context: Context): OmhAuthClient {
        val omhAuthProvider = OmhAuthProvider.Builder()
            .addNonGmsPath(BuildConfig.AUTH_NON_GMS_PATH)
            .addGmsPath(BuildConfig.AUTH_GMS_PATH)
            .build()
        return omhAuthProvider.provideAuthClient(
            scopes = listOf("openid", "email", "profile"),
            clientId = BuildConfig.CLIENT_ID,
            context = context
        )
    }
}