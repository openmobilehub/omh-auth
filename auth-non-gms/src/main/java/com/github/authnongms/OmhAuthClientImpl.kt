package com.github.authnongms

import android.content.Context
import android.content.Intent
import com.github.authnongms.factories.UseCaseFactory
import com.github.authnongms.presentation.RedirectActivity
import com.github.openmobilehub.auth.OmhAuthClient
import com.github.openmobilehub.auth.OmhUserProfile

internal class OmhAuthClientImpl(private val clientId: String, private val scopes: String) :
    OmhAuthClient {

    override fun getLoginIntent(context: Context): Intent {
        return Intent(context, RedirectActivity::class.java)
            .putExtra(RedirectActivity.CLIENT_ID, clientId)
            .putExtra(RedirectActivity.SCOPES, scopes)
    }

    override fun getUser(context: Context): OmhUserProfile? {
        val profileUseCase = UseCaseFactory.createUserProfileUseCase(context)
        return profileUseCase.getProfileData()
    }

    internal class Builder(
        private var clientId: String,
        private var authScope: String
    ) : OmhAuthClient.Builder {

        // TODO Add optional parameters

        override fun build(): OmhAuthClient {
            return OmhAuthClientImpl(clientId, authScope)
        }
    }
}
