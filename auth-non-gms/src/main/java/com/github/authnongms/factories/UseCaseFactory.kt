package com.github.authnongms.factories

import android.content.Context
import com.github.authnongms.domain.auth.LoginUseCase
import com.github.authnongms.domain.user.ProfileUseCase

internal object UseCaseFactory {

    fun createLoginUseCase(): LoginUseCase {
        return LoginUseCase(RepositoryFactory.authRepository)
    }

    fun createUserProfileUseCase(applicationContext: Context): ProfileUseCase {
        return ProfileUseCase(RepositoryFactory.getUserRepository(applicationContext))
    }
}
