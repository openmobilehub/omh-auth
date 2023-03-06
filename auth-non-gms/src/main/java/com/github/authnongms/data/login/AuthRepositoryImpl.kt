package com.github.authnongms.data.login

import com.github.authnongms.domain.auth.AuthRepository
import retrofit2.Response

class AuthRepositoryImpl(private val authService: GoogleAuthREST) : AuthRepository {

    override suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): Response<AuthTokenResponse> {
        return authService.getToken(clientId, authCode, redirectUri, codeVerifier)
    }

}
