package com.github.authnongms.domain.auth

import android.net.Uri
import androidx.core.net.toUri
import com.github.authnongms.data.login.AuthTokenResponse
import com.github.authnongms.utils.Constants
import com.github.authnongms.utils.generateCodeChallenge
import com.github.authnongms.utils.generateCodeVerifier
import retrofit2.Response

internal class LoginUseCase(private val authRepository: AuthRepository) {

    companion object {
        const val REDIRECT =
            "com.github.omhauthdemo:/oauth2redirect" // TODO update with dynamic URI
    }

    private var codeVerifier = generateCodeVerifier()
    var clientId: String? = null

    fun getLoginUrl(scopes: String): Uri {
        val authUri = "https://accounts.google.com/o/oauth2/auth"

        return authUri.toUri().buildUpon()
            .appendQueryParameter(Constants.PARAM_SCOPE, scopes)
            .appendQueryParameter(Constants.PARAM_RESPONSE_TYPE, "code")
            .appendQueryParameter(Constants.PARAM_REDIRECT_URI, REDIRECT)
            .appendQueryParameter(Constants.PARAM_CLIENT_ID, clientId)
            .appendQueryParameter(Constants.PARAM_CHALLENGE_METHOD, Constants.SHA256)
            .appendQueryParameter(Constants.PARAM_CODE_CHALLENGE, generateCodeChallenge(codeVerifier))
            .build()
    }

    suspend fun requestTokens(authCode: String): Response<AuthTokenResponse> {
        return authRepository.requestTokens(
            clientId = checkNotNull(clientId),
            authCode = authCode,
            redirectUri = REDIRECT,
            codeVerifier = codeVerifier
        )
    }
}
