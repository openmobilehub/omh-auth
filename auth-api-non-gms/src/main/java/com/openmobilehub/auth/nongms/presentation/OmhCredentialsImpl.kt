package com.openmobilehub.auth.nongms.presentation

import com.openmobilehub.auth.nongms.domain.auth.AuthUseCase
import com.openmobilehub.auth.nongms.utils.ThreadUtils
import com.openmobilehub.auth.api.OmhCredentials
import com.openmobilehub.auth.api.OperationFailureListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

internal class OmhCredentialsImpl(
    private val authUseCase: AuthUseCase,
    clientId: String
) : OmhCredentials {

    init {
        authUseCase.clientId = clientId
    }

    override fun refreshAccessToken(): String? {
        ThreadUtils.checkForMainThread()
        return runBlocking {
            authUseCase.refreshToken().first()
        }
    }

    override val accessToken: String?
        get() = authUseCase.getAccessToken()
}
