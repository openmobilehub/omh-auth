package com.github.authnongms.data.login

import com.github.authnongms.data.login.datasource.AuthDataSource
import com.github.authnongms.domain.auth.AuthRepository
import com.github.authnongms.domain.models.DataResponse
import com.github.authnongms.utils.Constants

class AuthRepositoryImpl(private val googleAuthDataSource: AuthDataSource) : AuthRepository {

    companion object {
        private const val ACCESS_TOKEN = "accesstoken"
        private const val REFRESH_TOKEN = "refreshtoken"
    }

    override suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): DataResponse<AuthTokenResponse> {
        val response = googleAuthDataSource.getToken(clientId, authCode, redirectUri, codeVerifier)
        // Todo improve error handling
        if (response.isSuccessful) {
            googleAuthDataSource.storeToken(
                tokenType = ACCESS_TOKEN,
                token = checkNotNull(response.body()?.accessToken)
            )
            googleAuthDataSource.storeToken(
                tokenType = REFRESH_TOKEN,
                token = checkNotNull(response.body()?.refreshToken)
            )
        }
        return DataResponse(
            response = response.body(),
            errorDetail = response.errorBody()?.string()
        )
    }

    override fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String
    ): String {
        return googleAuthDataSource.buildLoginUrl(
            scopes = scopes,
            clientId = clientId,
            codeChallenge = codeChallenge,
            redirectUri = redirectUri
        ).toString()
    }
}
