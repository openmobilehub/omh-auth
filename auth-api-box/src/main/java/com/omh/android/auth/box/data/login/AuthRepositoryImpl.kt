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

package com.omh.android.auth.box.data.login

import android.content.Context
import android.content.SharedPreferences
import com.omh.android.auth.box.data.login.datasource.BoxAuthDataSource
import com.omh.android.auth.box.data.login.models.AuthTokenResponse
import com.omh.android.auth.box.data.utils.BoxRetrofitImpl
import com.omh.android.auth.box.utils.Constants.PROVIDER_BOX
import com.omh.android.auth.mobileweb.data.login.datasource.AuthDataSource
import com.omh.android.auth.mobileweb.data.utils.getEncryptedSharedPrefs
import com.omh.android.auth.mobileweb.domain.auth.AuthRepository
import com.omh.android.auth.mobileweb.domain.models.ApiResult
import com.omh.android.auth.mobileweb.domain.models.OAuthTokens
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AuthRepositoryImpl(
    private val boxAuthDataSource: AuthDataSource<AuthTokenResponse>,
    private val ioDispatcher: CoroutineDispatcher,
    private val context: Context,
) : AuthRepository {

    override suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String,
    ): ApiResult<OAuthTokens> = withContext(ioDispatcher) {
        val result: ApiResult<AuthTokenResponse> = boxAuthDataSource.getToken(
            clientId = clientId,
            authCode = authCode,
            redirectUri = redirectUri,
            codeVerifier = codeVerifier,
        )

        result.map { data: AuthTokenResponse ->
            boxAuthDataSource.storeToken(
                tokenType = AuthDataSource.ACCESS_TOKEN,
                token = data.accessToken,
            )
            boxAuthDataSource.storeToken(
                tokenType = AuthDataSource.REFRESH_TOKEN,
                token = checkNotNull(data.refreshToken),
            )
            OAuthTokens(
                accessToken = data.accessToken,
                refreshToken = checkNotNull(data.refreshToken),
                idToken = "N/A",
            )
        }
    }

    override fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String,
    ): String {
        return boxAuthDataSource.buildLoginUrl(
            scopes = scopes,
            clientId = clientId,
            codeChallenge = codeChallenge,
            redirectUri = redirectUri,
        ).toString()
    }

    override fun getAccessToken(): String? {
        return boxAuthDataSource.getToken(AuthDataSource.ACCESS_TOKEN)
    }

    override suspend fun refreshAccessToken(
        clientId: String,
    ): ApiResult<String> = withContext(ioDispatcher) {
        boxAuthDataSource.refreshAccessToken(clientId).map { data: AuthTokenResponse ->
            boxAuthDataSource.storeToken(AuthDataSource.ACCESS_TOKEN, data.accessToken)
            data.accessToken
        }
    }

    override suspend fun revokeToken(clientId: String): ApiResult<Unit> = withContext(ioDispatcher) {
        val accessToken = boxAuthDataSource.getToken(AuthDataSource.ACCESS_TOKEN)
        if (accessToken == null) {
            val noTokenException = IllegalStateException("No token stored")
            return@withContext ApiResult.Error.RuntimeError(noTokenException)
        }

        return@withContext boxAuthDataSource.revokeToken(clientId, accessToken)
    }

    override fun clearData() {
        boxAuthDataSource.clearData()
    }

    override fun formatRedirectUriFrom(packageName: String): String {
        return BoxAuthDataSource.formatRedirectUri(context)
    }

    companion object {

        private var authRepository: AuthRepository? = null

        fun getAuthRepository(
            context: Context,
            clientSecret: String,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        ): AuthRepository {
            if (authRepository == null) {
                val authService: BoxApiRest = BoxRetrofitImpl.instance.boxApiRest
                val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context, PROVIDER_BOX)
                val authDataSource: AuthDataSource<AuthTokenResponse> = BoxAuthDataSource(
                    context = context,
                    authService = authService,
                    clientSecret = clientSecret,
                    sharedPreferences = sharedPreferences,
                )
                authRepository = AuthRepositoryImpl(authDataSource, ioDispatcher, context)
            }

            return authRepository!!
        }
    }
}
