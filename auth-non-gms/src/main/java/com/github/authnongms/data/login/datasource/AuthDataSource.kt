package com.github.authnongms.data.login.datasource

import android.net.Uri
import com.github.authnongms.data.login.models.AuthTokenResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface AuthDataSource {

    suspend fun getToken(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ) : Flow<AuthTokenResponse>

    fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String
    ): Uri

    fun storeToken(tokenType: String, token: String)
}
