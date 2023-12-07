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

package com.omh.android.auth.box.data.user.datasource

import android.content.SharedPreferences
import androidx.core.content.edit
import com.omh.android.auth.api.models.OmhUserProfile
import com.omh.android.auth.box.data.utils.BoxRetrofitImpl
import com.omh.android.auth.box.utils.Constants
import com.omh.android.auth.mobileweb.data.login.datasource.AuthDataSource.Companion.ACCESS_TOKEN
import com.omh.android.auth.mobileweb.data.user.datasource.UserDataSource

internal class BoxUserDataSource(private val sharedPreferences: SharedPreferences) :
    UserDataSource {

    /**
     * Handles the ID token returned from the Google Auth Provider. This uses the googleapis lib
     * to verify the token and validate it. The operation can't run on the main thread.
     *
     * Once it passes the checks, the data is stored in the Encrypted Shared Preferences.
     *
     * @param idToken -> token to validate and handle.
     * @param clientId -> clientId from google console of the Android Application type.
     */
    override suspend fun handleIdToken(idToken: String, clientId: String) {
        requireNotNull(sharedPreferences.getString(ACCESS_TOKEN, null)).let { accessToken ->
            val profileResponse = BoxRetrofitImpl.instance.boxApiRest.getCurrentUserProfile("Bearer $accessToken")

            profileResponse.map {
                sharedPreferences.edit {
                    putString(Constants.NAME_KEY, it.name)
                    putString(Constants.ID_KEY, it.id)
                    if (false == it.notificationEmail?.isEmpty()) {
                        putString(Constants.EMAIL_KEY, it.notificationEmail)
                    } else {
                        putString(Constants.EMAIL_KEY, it.email)
                    }
                    putString(Constants.PICTURE_KEY, it.picture)
                }
            }
        }
    }

    /**
     * Checks if there's any relevant data stored for the user. If any of the required values are
     * null, then it's assumed that no user is stored and a null object is returned.
     */
    override fun getProfileData(): OmhUserProfile? {
        val name = sharedPreferences.getString(Constants.NAME_KEY, null)
        val email = sharedPreferences.getString(Constants.EMAIL_KEY, null)
        // box.com only has name but no separate name and surname fields
        val picture = sharedPreferences.getString(Constants.PICTURE_KEY, null)

        if (name == null || email == null) return null

        return OmhUserProfile(
            name = name,
            surname = "",
            email = email,
            profileImage = picture,
        )
    }
}
