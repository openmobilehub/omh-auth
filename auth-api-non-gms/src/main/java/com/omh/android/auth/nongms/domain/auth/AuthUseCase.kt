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

package com.omh.android.auth.nongms.domain.auth

import com.omh.android.auth.api.domain.utils.Pkce
import com.omh.android.auth.api.domain.utils.PkceImpl
import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.domain.models.OAuthTokens

internal class AuthUseCase(
    private val authRepository: AuthRepository,
    private val pkce: Pkce
) {

    fun getLoginUrl(scopes: String, packageName: String, clientId: String): String {
        return authRepository.buildLoginUrl(
            scopes = scopes,
            clientId = clientId,
            codeChallenge = pkce.generateCodeChallenge(),
            redirectUri = REDIRECT_FORMAT.format(packageName)
        )
    }

    suspend fun requestTokens(
        authCode: String,
        packageName: String,
        clientId: String,
    ): ApiResult<OAuthTokens> {
        return authRepository.requestTokens(
            clientId = clientId,
            authCode = authCode,
            redirectUri = REDIRECT_FORMAT.format(packageName),
            codeVerifier = pkce.codeVerifier
        )
    }

    suspend fun blockingRefreshToken(clientId: String): ApiResult<String> {
        return authRepository.refreshAccessToken(clientId)
    }

    fun getAccessToken(): String? = authRepository.getAccessToken()

    fun logout() = authRepository.clearData()

    suspend fun revokeToken(): ApiResult<Unit> {
        val result = authRepository.revokeToken()
        if (result is ApiResult.Success<*>) {
            authRepository.clearData()
        }
        return result
    }

    companion object {
        const val REDIRECT_FORMAT = "%s:/oauth2redirect"

        fun createAuthUseCase(authRepository: AuthRepository): AuthUseCase {
            return AuthUseCase(authRepository, PkceImpl())
        }
    }
}
