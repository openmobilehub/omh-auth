package com.github.authnongms.presentation

import com.github.authnongms.domain.auth.AuthUseCase
import com.github.authnongms.utils.ThreadUtils
import com.github.openmobilehub.auth.OmhCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

internal class OmhCredentialsImpl(
    private val authUseCase: AuthUseCase,
    clientId: String
) : OmhCredentials {

    init {
        authUseCase.clientId = clientId
    }

    override fun refreshAccessToken(onOperationFailure: OmhCredentials.OnOperationFailure): String? {
        ThreadUtils.checkForMainThread()
        return runBlocking(Dispatchers.IO) {
            authUseCase.refreshToken()
                .catch { e -> onOperationFailure.onFailure(Exception(e)) }
                .firstOrNull()
        }
    }

    override val accessToken: String?
        get() = authUseCase.getAccessToken()

    override fun revokeToken(onOperationFailure: OmhCredentials.OnOperationFailure) {
        ThreadUtils.checkForMainThread()
        runBlocking(Dispatchers.IO) {
            authUseCase.logout()
                .catch { e -> onOperationFailure.onFailure(Exception(e)) }
                .collect()
        }
    }
}
