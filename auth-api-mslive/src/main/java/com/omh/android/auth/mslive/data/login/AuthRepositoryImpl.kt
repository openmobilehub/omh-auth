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

package com.omh.android.auth.mslive.data.login

import android.content.Context
import android.content.SharedPreferences
import com.omh.android.auth.mobileweb.data.login.datasource.AuthDataSource
import com.omh.android.auth.mobileweb.data.utils.getEncryptedSharedPrefs
import com.omh.android.auth.mobileweb.domain.auth.AuthRepository
import com.omh.android.auth.mobileweb.domain.models.ApiResult
import com.omh.android.auth.mobileweb.domain.models.OAuthTokens
import com.omh.android.auth.mslive.data.login.datasource.MsLiveAuthDataSource
import com.omh.android.auth.mslive.data.login.models.AuthTokenResponse
import com.omh.android.auth.mslive.data.utils.MsLiveRetrofitImpl
import com.omh.android.auth.mslive.utils.Constants.PROVIDER_MSLIVE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource<AuthTokenResponse>,
    private val ioDispatcher: CoroutineDispatcher,
    private val context: Context,
) : AuthRepository {

    override suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String,
    ): ApiResult<OAuthTokens> = withContext(ioDispatcher) {
        val authTokenResult: ApiResult<AuthTokenResponse> = authDataSource.getToken(
            clientId = clientId,
            authCode = authCode,
            redirectUri = redirectUri,
            codeVerifier = codeVerifier,
        )

        authTokenResult.map { authTokenResponse: AuthTokenResponse ->
            authDataSource.storeToken(
                tokenType = AuthDataSource.ACCESS_TOKEN,
                token = authTokenResponse.accessToken,
            )
            authDataSource.storeToken(
                tokenType = AuthDataSource.REFRESH_TOKEN,
                token = checkNotNull(authTokenResponse.refreshToken),
            )
            OAuthTokens(
                accessToken = authTokenResponse.accessToken,
                refreshToken = checkNotNull(authTokenResponse.refreshToken),
                idToken = authTokenResponse.idToken,
            )
        }
    }

    override fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String,
    ): String {
        return authDataSource.buildLoginUrl(
            scopes = scopes,
            clientId = clientId,
            codeChallenge = codeChallenge,
            redirectUri = redirectUri,
        ).toString()
    }

    override fun getAccessToken(): String? {
        return authDataSource.getToken(AuthDataSource.ACCESS_TOKEN)
    }

    override suspend fun refreshAccessToken(
        clientId: String,
    ): ApiResult<String> = withContext(ioDispatcher) {
        authDataSource.refreshAccessToken(clientId).map { data: AuthTokenResponse ->
            authDataSource.storeToken(AuthDataSource.ACCESS_TOKEN, data.accessToken)
            data.accessToken
        }
    }

    override suspend fun revokeToken(clientId: String): ApiResult<Unit> = withContext(ioDispatcher) {
        val accessToken = authDataSource.getToken(AuthDataSource.ACCESS_TOKEN)
        if (accessToken == null) {
            val noTokenException = IllegalStateException("No token stored")
            return@withContext ApiResult.Error.RuntimeError(noTokenException)
        }

        return@withContext authDataSource.revokeToken(clientId, accessToken)
    }

    override fun clearData() {
        authDataSource.clearData()
    }

    override fun formatRedirectUriFrom(packageName: String): String {
        return MsLiveAuthDataSource.formatRedirectUri(context)
    }

    companion object {

        private var authRepository: AuthRepository? = null

        fun getAuthRepository(
            context: Context,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        ): AuthRepository {
            if (authRepository == null) {
                val authService: MsLiveAuthRest = MsLiveRetrofitImpl.instance.authREST
                val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context, PROVIDER_MSLIVE)
                val authDataSource: AuthDataSource<AuthTokenResponse> = MsLiveAuthDataSource(
                    context = context,
                    authService = authService,
                    sharedPreferences = sharedPreferences,
                )
                authRepository = AuthRepositoryImpl(authDataSource, ioDispatcher, context)
            }

            return authRepository!!
        }
    }
}
