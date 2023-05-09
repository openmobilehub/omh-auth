package com.omh.android.auth.nongms.presentation

import android.content.Context
import android.content.Intent
import com.omh.android.auth.nongms.data.user.UserRepositoryImpl
import com.omh.android.auth.nongms.domain.user.ProfileUseCase
import com.omh.android.auth.nongms.presentation.redirect.RedirectActivity
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.async.OmhTask
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhAuthStatusCodes
import com.omh.android.auth.api.models.OmhUserProfile
import com.omh.android.auth.nongms.data.login.AuthRepositoryImpl
import com.omh.android.auth.nongms.domain.auth.AuthUseCase
import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.utils.Constants
import kotlin.jvm.Throws

/**
 * Non GMS implementation of the OmhAuthClient abstraction. Required a clientId and defined scopes as
 * no extra scopes can be accessed in the future.
 */
internal class OmhAuthClientImpl(
    private val clientId: String,
    private val scopes: String,
    context: Context
) : OmhAuthClient {

    private val applicationContext: Context

    init {
        applicationContext = context.applicationContext
    }

    override fun getLoginIntent(): Intent {
        return Intent(applicationContext, RedirectActivity::class.java)
            .putExtra(RedirectActivity.CLIENT_ID, clientId)
            .putExtra(RedirectActivity.SCOPES, scopes)
    }

    override fun getUser(): OmhUserProfile? {
        val userRepository = UserRepositoryImpl.getUserRepository(applicationContext)
        val profileUseCase = ProfileUseCase.createUserProfileUseCase(userRepository)
        return profileUseCase.getProfileData()
    }

    internal class Builder(
        private var clientId: String,
    ) : OmhAuthClient.Builder {

        private var authScope: String = ""

        fun addScope(scope: String): Builder {
            authScope += " $scope"
            authScope.trim()
            return this
        }

        override fun build(context: Context): OmhAuthClient {
            return OmhAuthClientImpl(clientId, authScope, context)
        }
    }

    override fun getCredentials(): Any {
        return OmhAuthFactoryImpl.getCredentials(clientId, applicationContext)
    }

    @SuppressWarnings("TooGenericExceptionCaught") // Until we find any specific errors for this.
    override fun signOut(): OmhTask<Unit> {
        val authRepository = AuthRepositoryImpl.getAuthRepository(applicationContext)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        return OmhNonGmsTask(authUseCase::logout)
    }

    override fun getAccountFromIntent(data: Intent?): OmhUserProfile {
        if (data?.hasExtra(Constants.CAUSE_KEY) == true) {
            val exception = data.getSerializableExtra(Constants.CAUSE_KEY) as OmhAuthException
            throw exception
        }
        return getUser() ?: throw OmhAuthException.UnrecoverableLoginException(
            cause = Throwable(message = "No user profile stored")
        )
    }

    override fun revokeToken(): OmhTask<Unit> {
        val authRepository = AuthRepositoryImpl.getAuthRepository(applicationContext)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        return OmhNonGmsTask {
            val apiResult = authUseCase.revokeToken()
            return@OmhNonGmsTask apiResult.extractResult()
        }
    }

    @Throws(OmhAuthException::class)
    private fun <T> ApiResult<T>.extractResult(): T {
        return when (this) {
            is ApiResult.Error -> throw getOmhException()
            is ApiResult.Success -> this.data
        }
    }

    @Throws(OmhAuthException::class)
    private fun ApiResult.Error.getOmhException(): OmhAuthException {
        val statusCode = when (this) {
            is ApiResult.Error.ApiError -> OmhAuthStatusCodes.HTTPS_ERROR
            is ApiResult.Error.NetworkError -> OmhAuthStatusCodes.NETWORK_ERROR
            is ApiResult.Error.RuntimeError -> OmhAuthStatusCodes.INTERNAL_ERROR
        }
        return OmhAuthException.ApiException(statusCode, cause)
    }
}
