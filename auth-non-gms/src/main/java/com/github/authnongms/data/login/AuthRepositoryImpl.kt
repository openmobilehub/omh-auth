package com.github.authnongms.data.login

import android.content.Context
import android.content.SharedPreferences
import com.github.authnongms.data.GoogleRetrofitImpl
import com.github.authnongms.data.login.datasource.AuthDataSource
import com.github.authnongms.data.login.datasource.GoogleAuthDataSource
import com.github.authnongms.data.utils.getEncryptedSharedPrefs
import com.github.authnongms.domain.auth.AuthRepository
import com.github.authnongms.domain.models.OAuthTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

internal class AuthRepositoryImpl(
    private val googleAuthDataSource: AuthDataSource
) : AuthRepository {

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
        ).onEach { response ->
            googleAuthDataSource.storeToken(
                tokenType = AuthDataSource.ACCESS_TOKEN,
                token = checkNotNull(response.accessToken)
            )
            googleAuthDataSource.storeToken(
                tokenType = AuthDataSource.REFRESH_TOKEN,
                token = checkNotNull(response.refreshToken)
            )
        }.map { response ->
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

    override fun getAccessToken(): String? {
        return googleAuthDataSource.getToken(AuthDataSource.ACCESS_TOKEN)
    }

    override suspend fun refreshAccessToken(clientId: String): Flow<String> {
        return googleAuthDataSource.refreshAccessToken(clientId).map { response ->
            val accessToken = checkNotNull(response.accessToken)
            googleAuthDataSource.storeToken(AuthDataSource.ACCESS_TOKEN, response.accessToken)
            accessToken
        }
    }

    override suspend fun revokeToken(): Flow<Unit> {
        val accessToken = googleAuthDataSource.getToken(AuthDataSource.ACCESS_TOKEN)
        if (accessToken == null) {
            clearData()
            return flow { emit(Unit) }
        }
        return googleAuthDataSource.revokeToken(accessToken).onCompletion {
            googleAuthDataSource.clearData()
        }
    }

    override fun clearData() {
        googleAuthDataSource.clearData()
    }

    companion object {

        private var authRepository: AuthRepository? = null

        fun getAuthRepository(context: Context): AuthRepository {
            if (authRepository == null) {
                val authService: GoogleAuthREST = GoogleRetrofitImpl.instance.googleAuthREST
                val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context)
                val googleAuthDataSource: AuthDataSource = GoogleAuthDataSource(
                    authService = authService,
                    sharedPreferences = sharedPreferences
                )
                authRepository = AuthRepositoryImpl(googleAuthDataSource)
            }

            return authRepository!!
        }
    }
}