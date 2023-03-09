package com.github.authnongms.factories

import android.content.Context
import com.github.authnongms.domain.auth.LoginUseCase
import com.github.authnongms.domain.user.ProfileUseCase

/**
 * Factory for the use cases. Creates a new one every time you call the function.
 */
internal object UseCaseFactory {

    fun createLoginUseCase(applicationContext: Context): LoginUseCase {
        val authRepository = RepositoryFactory.getAuthRepository(applicationContext)
        return LoginUseCase(authRepository)
    }

    fun createUserProfileUseCase(applicationContext: Context): ProfileUseCase {
        val userRepository = RepositoryFactory.getUserRepository(applicationContext)
        return ProfileUseCase(userRepository)
    }
}
