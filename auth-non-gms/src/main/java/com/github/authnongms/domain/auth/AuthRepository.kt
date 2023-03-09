package com.github.authnongms.domain.auth

import com.github.authnongms.domain.models.OAuthTokens
import kotlinx.coroutines.flow.Flow

internal interface AuthRepository {

    suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): Flow<OAuthTokens>

    fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String
    ): String
}
