package com.github.authnongms.data.user.datasource

import com.github.openmobilehub.auth.models.OmhUserProfile

interface UserDataSource {

    suspend fun handleIdToken(idToken: String, clientId: String)

    fun getProfileData(): OmhUserProfile?
}
