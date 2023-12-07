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

package com.omh.android.auth.mslive.data.user.datasource

import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import com.omh.android.auth.api.models.OmhUserProfile
import com.omh.android.auth.mobileweb.data.user.datasource.UserDataSource
import com.omh.android.auth.mslive.utils.Constants
import org.json.JSONObject
import java.security.GeneralSecurityException

internal class MsLiveUserDataSource(private val sharedPreferences: SharedPreferences) :
    UserDataSource {

    /**
     * Some simple validation of the ID token as sanity check.
     *
     *
     * @param idToken -> token to validate
     * @param clientId -> clientId from Azure portal
     */
    override suspend fun handleIdToken(idToken: String, clientId: String) {
        val parts = idToken.split('.')
        // val header = JSONObject(String(Base64.decode(parts[0], Base64.DEFAULT)))
        val payload = JSONObject(String(Base64.decode(parts[1], Base64.DEFAULT)))
        if (payload.getString("ver") != "2.0") {
            throw GeneralSecurityException("Only ID token version 2.0 is accepted")
        }
        if (payload.getString("aud") != clientId) {
            throw GeneralSecurityException("Invalid audience in JWT payload, which is different from given clientId")
        }
        val email = payload.getString(Constants.EMAIL_KEY)
        val surname = payload.getString(Constants.SURNAME_KEY)
        val name = payload.getString(Constants.NAME_KEY)
        val id = payload.getString("oid")

        sharedPreferences.edit {
            putString(Constants.NAME_KEY, name)
            putString(Constants.SURNAME_KEY, surname)
            putString(Constants.EMAIL_KEY, email)
            putString(Constants.PICTURE_KEY, MS_PROFILE_PICTURE)
            putString(Constants.ID_KEY, id)
        }
    }

    /**
     * Checks if there's any relevant data stored for the user. If any of the required values are
     * null, then it's assumed that no user is stored and a null object is returned.
     */
    override fun getProfileData(): OmhUserProfile? {
        val name = sharedPreferences.getString(Constants.NAME_KEY, null)
        val email = sharedPreferences.getString(Constants.EMAIL_KEY, null)
        val surname = sharedPreferences.getString(Constants.SURNAME_KEY, null)
        val picture = sharedPreferences.getString(Constants.PICTURE_KEY, null)

        if (name == null || email == null || surname == null) return null

        return OmhUserProfile(
            name = name,
            surname = surname,
            email = email,
            profileImage = picture,
        )
    }

    companion object {
        const val MS_PROFILE_PICTURE = "https://graph.microsoft.com/v1.0/me/photo/\$value"
    }
}
