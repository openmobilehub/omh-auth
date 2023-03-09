package com.github.authnongms.data.login

import com.github.authnongms.data.login.datasource.AuthDataSource
import com.github.authnongms.data.login.models.AuthTokenResponse
import com.github.authnongms.domain.auth.AuthRepository
import com.github.authnongms.domain.models.DataResponse
import com.github.authnongms.domain.models.OAuthTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
    ): Flow<OAuthTokens> {
        return googleAuthDataSource.getToken(
            clientId = clientId,
            authCode = authCode,
            redirectUri = redirectUri,
            codeVerifier = codeVerifier
        ).map { response ->
            googleAuthDataSource.storeToken(
                tokenType = ACCESS_TOKEN,
                token = checkNotNull(response.accessToken)
            )
            googleAuthDataSource.storeToken(
                tokenType = REFRESH_TOKEN,
                token = checkNotNull(response.refreshToken)
            )
            OAuthTokens(
                response.accessToken,
                checkNotNull(response.refreshToken),
                response.idToken
            )
        }
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
