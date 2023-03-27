package com.omh.auth.nongms.presentation

import android.content.Context
import com.omh.auth.nongms.data.login.AuthRepositoryImpl
import com.omh.auth.nongms.domain.auth.AuthRepository
import com.omh.auth.nongms.domain.auth.AuthUseCase
import com.omh.auth.api.OmhAuthClient
import com.omh.auth.api.OmhAuthFactory
import com.omh.auth.api.OmhCredentials

object OmhAuthFactoryImpl : OmhAuthFactory {

    /**
     * Creates an auth client for the user of the non GMS type and returns it as the abstraction.
     * This should be used by the core plugin only.
     */
    override fun getAuthClient(
        context: Context,
        scopes: Collection<String>,
        clientId: String
    ): OmhAuthClient {
        val builder = OmhAuthClientImpl.Builder(clientId)
        scopes.forEach(builder::addScope)
        return builder.build(context)
    }

    internal fun getCredentials(clientId: String, context: Context): OmhCredentials {
        val authRepository: AuthRepository = AuthRepositoryImpl.getAuthRepository(context)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        return OmhCredentialsImpl(authUseCase, clientId)
    }
}
