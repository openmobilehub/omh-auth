package com.github.authnongms.presentation

import com.github.authnongms.domain.auth.AuthUseCase
import com.github.authnongms.utils.ThreadUtils
import com.github.openmobilehub.auth.OmhCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

internal class OmhCredentialsImpl(
    private val authUseCase: AuthUseCase,
    clientId: String
) : OmhCredentials {

    init {
        authUseCase.clientId = clientId
    }

    @Throws(IllegalStateException::class)
    override fun refreshAccessToken(onRefreshFailure: OmhCredentials.OnRefreshFailure): String? {
        if (ThreadUtils.isOnMainThread) {
            error("Running blocking function on main thread.")
        }
        return runBlocking(Dispatchers.IO) {
            authUseCase.refreshToken()
                .catch { e -> onRefreshFailure.onFailure(Exception(e)) }
                .firstOrNull()
        }
    }

    override val accessToken: String?
        get() = authUseCase.getAccessToken()
}
