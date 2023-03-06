package com.github.authnongms.factories

import android.content.Context
import android.content.SharedPreferences
import com.github.authnongms.data.RetrofitImpl
import com.github.authnongms.data.login.AuthRepositoryImpl
import com.github.authnongms.data.user.UserRepositoryImpl
import com.github.authnongms.domain.auth.AuthRepository
import com.github.authnongms.domain.user.UserRepository
import com.github.authnongms.utils.getEncryptedSharedPrefs

/**
 * Singleton factory for repositories
 */
internal object RepositoryFactory {

    var userRepository: UserRepository? = null

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(RetrofitImpl.instance.googleAuthREST)
    }

    fun getUserRepository(context: Context): UserRepository {
        if (userRepository == null) {
            val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context)
            userRepository = UserRepositoryImpl(sharedPreferences)
        }
        return userRepository!!
    }
}
