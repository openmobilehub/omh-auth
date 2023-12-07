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

package com.omh.android.auth.box.data.login.datasource

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import androidx.core.net.toUri
import com.omh.android.auth.box.R
import com.omh.android.auth.box.data.login.BoxApiRest
import com.omh.android.auth.box.data.login.models.AuthTokenResponse
import com.omh.android.auth.box.utils.Constants
import com.omh.android.auth.mobileweb.data.login.datasource.AuthDataSource
import com.omh.android.auth.mobileweb.domain.models.ApiResult

internal class BoxAuthDataSource(
    private val context: Context,
    private val clientSecret: String,
    private val authService: BoxApiRest,
    private val sharedPreferences: SharedPreferences,
) : AuthDataSource<AuthTokenResponse> {

    override suspend fun getToken(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String,
    ): ApiResult<AuthTokenResponse> {
        return authService.getToken(
            clientId = clientId,
            clientSecret = clientSecret,
            code = authCode,
            redirectUri = redirectUri,
        )
    }

    /**
     * Builds the login URL for the Custom Tabs screen. This only works when your app is setup in the
     * Google Console in the OAuth Credentials sections. If the login is successful, an auth code
     * will be returned with the redirectUri. If not, an error code will be attached as a query param.
     *
     * @param scopes -> requested scopes by the application
     * @param clientId -> clientId from google console of the Android Application type.
     * @param codeChallenge -> PKCE implementation against man in the middle attacks
     * @param redirectUri -> URI used to redirect back to the application.
     */
    override fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String,
    ): Uri {
        return AUTH_URI.toUri().buildUpon()
            .appendQueryParameter(Constants.PARAM_SCOPE, scopes)
            .appendQueryParameter(Constants.PARAM_RESPONSE_TYPE, CODE_VALUE)
            .appendQueryParameter(
                Constants.PARAM_REDIRECT_URI,
                formatRedirectUri(context),
            )
            .appendQueryParameter(Constants.PARAM_CLIENT_ID, clientId)
            .appendQueryParameter(Constants.PARAM_STATE, codeChallenge)
            .build()
    }

    override fun storeToken(tokenType: String, token: String) {
        sharedPreferences.edit {
            putString(tokenType, token)
        }
    }

    override fun getToken(tokenType: String): String? {
        return sharedPreferences.getString(tokenType, null)
    }

    private fun getRefreshToken(): String? {
        return sharedPreferences.getString(AuthDataSource.REFRESH_TOKEN, null)
    }

    override suspend fun refreshAccessToken(clientId: String): ApiResult<AuthTokenResponse> {
        val refreshToken = getRefreshToken() ?: return ApiResult.Error.RuntimeError(
            IllegalStateException("No refresh token"),
        )
        return authService.refreshToken(clientId, clientSecret, refreshToken)
    }

    override suspend fun revokeToken(clientId: String, token: String): ApiResult<Unit> {
        return (authService.revokeToken(clientId, clientSecret, token))
    }

    override fun clearData() {
        sharedPreferences.edit(action = SharedPreferences.Editor::clear)
    }

    companion object {
        private const val AUTH_URI = "https://account.box.com/api/oauth2/authorize"
        private const val CODE_VALUE = "code"

        @JvmStatic
        fun formatRedirectUri(context: Context): String {
            val scheme = context.getString(R.string.com_omh_android_auth_box_oauth2_redirect_scheme)
            val host = context.getString(R.string.com_omh_android_auth_box_oauth2_redirect_host)
            val pathPrefix = context.getString(R.string.com_omh_android_auth_box_oauth2_redirect_pathPrefix)
            return "$scheme://$host$pathPrefix"
        }
    }
}
