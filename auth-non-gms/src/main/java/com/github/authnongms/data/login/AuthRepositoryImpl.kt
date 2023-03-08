package com.github.authnongms.data.login

import androidx.core.net.toUri
import com.github.authnongms.domain.models.DataResponse
import com.github.authnongms.domain.auth.AuthRepository
import com.github.authnongms.utils.Constants

class AuthRepositoryImpl(private val authService: GoogleAuthREST) : AuthRepository {

    companion object {
        private const val AUTH_URI = "https://accounts.google.com/o/oauth2/auth"
        private const val CODE_VALUE = "code"
    }

    override suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): DataResponse<AuthTokenResponse> {
        val response = authService.getToken(clientId, authCode, redirectUri, codeVerifier)
        // Todo improve error handling
        return DataResponse(response = response.body(), errorDetail = response.errorBody()?.string())
    }

    override fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String
    ): String {
        return AUTH_URI.toUri().buildUpon()
            .appendQueryParameter(Constants.PARAM_SCOPE, scopes)
            .appendQueryParameter(Constants.PARAM_RESPONSE_TYPE, CODE_VALUE)
            .appendQueryParameter(Constants.PARAM_REDIRECT_URI, redirectUri)
            .appendQueryParameter(Constants.PARAM_CLIENT_ID, clientId)
            .appendQueryParameter(Constants.PARAM_CHALLENGE_METHOD, Constants.SHA256)
            .appendQueryParameter(Constants.PARAM_CODE_CHALLENGE, codeChallenge)
            .build()
            .toString()
    }

}
