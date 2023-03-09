package com.github.authnongms.data.user

import com.github.authnongms.data.user.datasource.UserDataSource
import com.github.authnongms.domain.user.UserRepository
import com.github.openmobilehub.auth.models.OmhUserProfile

internal class UserRepositoryImpl(private val googleUserDataSource: UserDataSource) :
    UserRepository {

    override suspend fun handleIdToken(idToken: String, clientId: String) {
        googleUserDataSource.handleIdToken(idToken, clientId)
    }

    override fun getProfileData(): OmhUserProfile? {
        return googleUserDataSource.getProfileData()
    }
}
