package com.github.authnongms.domain.user

import com.github.openmobilehub.auth.OmhUserProfile

class ProfileUseCase(private val userRepository: UserRepository) {

    suspend fun resolveIdToken(idToken: String, clientId: String) {
        userRepository.handleIdToken(idToken, clientId)
    }

    fun getProfileData(): OmhUserProfile? {
        return userRepository.getProfileData()
    }
}
