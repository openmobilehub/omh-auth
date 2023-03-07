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
        const val REDIRECT_FORMAT = "%s:/oauth2redirect"
        private const val authUri = "https://accounts.google.com/o/oauth2/auth"
    }

    private var codeVerifier = generateCodeVerifier()
    var clientId: String? = null

    fun getLoginUrl(scopes: String, packageName: String): Uri {
        return authUri.toUri().buildUpon()
            .appendQueryParameter(Constants.PARAM_SCOPE, scopes)
            .appendQueryParameter(Constants.PARAM_RESPONSE_TYPE, "code")
            .appendQueryParameter(Constants.PARAM_REDIRECT_URI, REDIRECT_FORMAT.format(packageName))
            .appendQueryParameter(Constants.PARAM_CLIENT_ID, clientId)
            .appendQueryParameter(Constants.PARAM_CHALLENGE_METHOD, Constants.SHA256)
            .appendQueryParameter(
                Constants.PARAM_CODE_CHALLENGE,
                generateCodeChallenge(codeVerifier)
            )
            .build()
    }

    suspend fun requestTokens(authCode: String, packageName: String): Response<AuthTokenResponse> {
        return authRepository.requestTokens(
            clientId = checkNotNull(clientId),
            authCode = authCode,
            redirectUri = REDIRECT_FORMAT.format(packageName),
            codeVerifier = codeVerifier
        )
    }
}
