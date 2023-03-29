package com.omh.android.auth.nongms.domain.user

import com.omh.android.auth.api.models.OmhUserProfile

class ProfileUseCase(private val userRepository: UserRepository) {

    suspend fun resolveIdToken(idToken: String, clientId: String) {
        if (idToken.trim().isEmpty() || clientId.trim().isEmpty()) {
            error("ID token or clientID are empty")
        }
        userRepository.handleIdToken(idToken, clientId)
    }

    fun getProfileData(): OmhUserProfile? {
        return userRepository.getProfileData()
    }

    companion object {

        fun createUserProfileUseCase(userRepository: UserRepository): ProfileUseCase {
            return ProfileUseCase(userRepository)
        }
    }
}
