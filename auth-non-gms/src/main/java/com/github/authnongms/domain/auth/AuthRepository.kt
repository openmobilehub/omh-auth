package com.github.authnongms.domain.auth

import com.github.authnongms.data.login.AuthTokenResponse
import retrofit2.Response

internal interface AuthRepository {

    suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): Response<AuthTokenResponse>
}
