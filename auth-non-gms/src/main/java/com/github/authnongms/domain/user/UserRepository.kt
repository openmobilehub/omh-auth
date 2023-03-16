package com.github.authnongms.domain.user

import com.github.openmobilehub.auth.api.models.OmhUserProfile

interface UserRepository {

    suspend fun handleIdToken(idToken: String, clientId: String)

    fun getProfileData(): OmhUserProfile?
}
