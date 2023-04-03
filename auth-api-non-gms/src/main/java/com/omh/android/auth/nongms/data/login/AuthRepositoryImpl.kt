package com.omh.android.auth.nongms.data.login

import android.content.Context
import android.content.SharedPreferences
import com.omh.android.auth.nongms.data.GoogleRetrofitImpl
import com.omh.android.auth.nongms.data.login.datasource.AuthDataSource
import com.omh.android.auth.nongms.data.login.datasource.GoogleAuthDataSource
import com.omh.android.auth.nongms.data.login.models.AuthTokenResponse
import com.omh.android.auth.nongms.data.utils.getEncryptedSharedPrefs
import com.omh.android.auth.nongms.domain.auth.AuthRepository
import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.domain.models.OAuthTokens
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

internal class AuthRepositoryImpl(
    private val googleAuthDataSource: AuthDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    override suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): ApiResult<OAuthTokens> = withContext(ioDispatcher) {
        val result: ApiResult<AuthTokenResponse> = googleAuthDataSource.getToken(
            clientId = clientId,
            authCode = authCode,
            redirectUri = redirectUri,
            codeVerifier = codeVerifier
        )

        return@withContext when (result) {
            is ApiResult.Success<AuthTokenResponse> -> {
                val authTokenResponse = result.data
                googleAuthDataSource.storeToken(
                    tokenType = AuthDataSource.ACCESS_TOKEN,
                    token = authTokenResponse.accessToken
                )
                googleAuthDataSource.storeToken(
                    tokenType = AuthDataSource.REFRESH_TOKEN,
                    token = checkNotNull(authTokenResponse.refreshToken)
                )
                val data = OAuthTokens(
                    accessToken = authTokenResponse.accessToken,
                    refreshToken = checkNotNull(authTokenResponse.refreshToken),
                    idToken = authTokenResponse.idToken
                )
                ApiResult.Success(data)
            }
            is ApiResult.Error -> result
            is ApiResult.NetworkError -> result
            is ApiResult.RuntimeError -> result
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

    override suspend fun refreshAccessToken(
        clientId: String
    ): ApiResult<String> = withContext(ioDispatcher) {
        val response: Response<AuthTokenResponse> =
            googleAuthDataSource.refreshAccessToken(clientId)
        val body: AuthTokenResponse = response.body()
            ?: return@withContext ApiResult.RuntimeError(UnknownError("Null body"))

        return@withContext if (response.isSuccessful) {
            googleAuthDataSource.storeToken(AuthDataSource.ACCESS_TOKEN, body.accessToken)
            ApiResult.Success(body.accessToken)
        } else {
            val exception = response.errorBody()?.string()
            response.errorBody()?.close()
            ApiResult.Error(response.code(), exception.orEmpty())
        }
    }

    override suspend fun revokeToken(): ApiResult<Unit> = withContext(ioDispatcher) {
        val accessToken: String = googleAuthDataSource.getToken(AuthDataSource.ACCESS_TOKEN)
            ?: return@withContext ApiResult.Success(Unit)

        val response: Response<Nothing> = googleAuthDataSource.revokeToken(accessToken)

        return@withContext if (response.isSuccessful) {
            ApiResult.Success(Unit)
        } else {
            val exception = response.errorBody()?.string()
            response.errorBody()?.close()
            ApiResult.Error(response.code(), exception.orEmpty())
        }
    }

    override fun clearData() {
        googleAuthDataSource.clearData()
    }

    companion object {

        private var authRepository: AuthRepository? = null

        fun getAuthRepository(
            context: Context,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        ): AuthRepository {
            if (authRepository == null) {
                val authService: GoogleAuthREST = GoogleRetrofitImpl.instance.googleAuthREST
                val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context)
                val googleAuthDataSource: AuthDataSource = GoogleAuthDataSource(
                    authService = authService,
                    sharedPreferences = sharedPreferences
                )
                authRepository = AuthRepositoryImpl(googleAuthDataSource, ioDispatcher)
            }

            return authRepository!!
        }
    }
}
