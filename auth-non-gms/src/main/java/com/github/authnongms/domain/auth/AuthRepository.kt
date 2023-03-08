package com.github.authnongms.domain.auth

import com.github.authnongms.data.login.AuthTokenResponse
import com.github.authnongms.domain.models.DataResponse

internal interface AuthRepository {

    suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): DataResponse<AuthTokenResponse>

    fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String
    ): String
}
