package com.github.authnongms.domain.auth

import android.util.Base64
import com.github.authnongms.data.login.models.AuthTokenResponse
import com.github.authnongms.domain.models.DataResponse
import java.security.MessageDigest
import java.security.SecureRandom

internal class LoginUseCase(private val authRepository: AuthRepository) {

    companion object {
        const val REDIRECT_FORMAT = "%s:/oauth2redirect"
        private const val SIXTYFOUR_BIT_SIZE = 64
    }

    private val codeVerifier by lazy { generateCodeVerifier() }
    var clientId: String? = null

    fun getLoginUrl(scopes: String, packageName: String): String {
        return authRepository.buildLoginUrl(
            scopes,
            checkNotNull(clientId),
            generateCodeChallenge(codeVerifier),
            redirectUri = REDIRECT_FORMAT.format(packageName)
        )
    }

    suspend fun requestTokens(authCode: String, packageName: String): DataResponse<AuthTokenResponse> {
        return authRepository.requestTokens(
            clientId = checkNotNull(clientId),
            authCode = authCode,
            redirectUri = REDIRECT_FORMAT.format(packageName),
            codeVerifier = codeVerifier
        )
    }

    private fun getEncoding() = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP

    private fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(SIXTYFOUR_BIT_SIZE)
        secureRandom.nextBytes(bytes)
        return Base64.encodeToString(bytes, getEncoding())
    }

    private fun generateCodeChallenge(codeVerifier: String): String {
        val bytes = codeVerifier.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes)
        val digest = messageDigest.digest()
        return Base64.encodeToString(digest, getEncoding())
    }
}
