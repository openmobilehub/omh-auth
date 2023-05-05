package com.omh.android.auth.nongms.domain.auth

import com.google.android.gms.tasks.SuccessContinuation
import com.google.android.gms.tasks.Task
import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.domain.models.OAuthTokens
import com.omh.android.auth.nongms.domain.utils.Pkce
import com.omh.android.auth.nongms.domain.utils.PkceImpl

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

    fun logout(): Task<Unit> = authRepository.clearData()

    fun revokeToken(): Task<Unit> {
        return authRepository.revokeToken()
//            .onSuccessTask { authRepository.clearData() }
    }

    companion object {
        const val REDIRECT_FORMAT = "%s:/oauth2redirect"

        fun createAuthUseCase(authRepository: AuthRepository): AuthUseCase {
            return AuthUseCase(authRepository, PkceImpl())
        }
    }
}
