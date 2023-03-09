package com.github.authnongms.factories

import android.content.Context
import android.content.SharedPreferences
import com.github.authnongms.data.RetrofitImpl
import com.github.authnongms.data.login.datasource.AuthDataSource
import com.github.authnongms.data.login.AuthRepositoryImpl
import com.github.authnongms.data.login.datasource.GoogleAuthDataSource
import com.github.authnongms.data.login.GoogleAuthREST
import com.github.authnongms.data.user.UserRepositoryImpl
import com.github.authnongms.domain.auth.AuthRepository
import com.github.authnongms.domain.user.UserRepository
import com.github.authnongms.data.utils.getEncryptedSharedPrefs

/**
 * Singleton factory for repositories
 */
internal object RepositoryFactory {

    private var userRepository: UserRepository? = null
    private var authRepository: AuthRepository? = null

    fun getAuthRepository(context: Context): AuthRepository {
        if (authRepository == null) {
            val authService: GoogleAuthREST = RetrofitImpl.instance.googleAuthREST
            val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context)
            val googleAuthDataSource: AuthDataSource = GoogleAuthDataSource(
                authService = authService,
                sharedPreferences = sharedPreferences
            )
            authRepository = AuthRepositoryImpl(googleAuthDataSource)
        }

        return authRepository!!
    }

    fun getUserRepository(context: Context): UserRepository {
        if (userRepository == null) {
            val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context)
            userRepository = UserRepositoryImpl(sharedPreferences)
        }
        return userRepository!!
    }
}
