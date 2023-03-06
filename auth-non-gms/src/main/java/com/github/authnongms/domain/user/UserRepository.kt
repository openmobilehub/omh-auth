package com.github.authnongms.domain.user

import com.github.authnongms.data.user.UserProfile

interface UserRepository {

    suspend fun handleIdToken(idToken: String, clientId: String)

    fun getProfileData(): UserProfile
}
