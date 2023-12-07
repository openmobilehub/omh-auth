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

package com.omh.android.auth.dropbox.data.user

import android.content.Context
import android.content.SharedPreferences
import com.omh.android.auth.api.models.OmhUserProfile
import com.omh.android.auth.dropbox.data.user.datasource.DropboxUserDataSource
import com.omh.android.auth.dropbox.utils.Constants.PROVIDER_DROPBOX
import com.omh.android.auth.mobileweb.data.user.datasource.UserDataSource
import com.omh.android.auth.mobileweb.data.utils.getEncryptedSharedPrefs
import com.omh.android.auth.mobileweb.domain.user.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {

    override suspend fun handleIdToken(idToken: String, clientId: String) {
        withContext(ioDispatcher) {
            userDataSource.handleIdToken(idToken, clientId)
        }
    }

    override fun getProfileData(): OmhUserProfile? {
        return userDataSource.getProfileData()
    }

    companion object {

        private var userRepository: UserRepository? = null

        fun getUserRepository(
            context: Context,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        ): UserRepository {
            if (userRepository == null) {
                val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context, PROVIDER_DROPBOX)
                val userDataSource: UserDataSource = DropboxUserDataSource(sharedPreferences)
                userRepository = UserRepositoryImpl(userDataSource, ioDispatcher)
            }
            return userRepository!!
        }
    }
}
