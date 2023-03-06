package com.github.authnongms.domain.user

class ProfileUseCase(private val userRepository: UserRepository) {

    suspend fun resolveIdToken(idToken: String, clientId: String) {
        userRepository.handleIdToken(idToken, clientId)
    }
}
