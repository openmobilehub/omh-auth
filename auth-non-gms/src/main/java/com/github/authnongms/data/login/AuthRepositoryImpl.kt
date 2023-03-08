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

    /**
     * Requests the token from the Google REST services. This can return HTTP errors.
     *
     * @param authCode -> the auth code returned from the custom tab login screen.
     * @param clientId -> clientId from google console of the Android Application type.
     * @param redirectUri -> the same redirectUri used for the custom tabs
     * @param codeVerifier -> PKCE implementation against man in the middle attacks.
     */
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

    /**
     * Builds the login URL for the Custom Tabs screen. This only works when your app is setup in the
     * Google Console in the OAuth Credentials sections. If the login is successful, an auth code
     * will be returned with the redirectUri. If not, an error code will be attached as a query param.
     *
     * @param scopes -> requested scopes by the application
     * @param clientId -> clientId from google console of the Android Application type.
     * @param codeChallenge -> PKCE implementation against man in the middle attacks
     * @param redirectUri -> URI used to redirect back to the application.
     */
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
