package com.openmobilehub.auth.gms

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.openmobilehub.auth.api.OmhAuthClient
import com.openmobilehub.auth.api.models.OmhUserProfile

internal class OmhAuthClientImpl(
    private val googleSignInClient: GoogleSignInClient
) : OmhAuthClient {

    override fun getLoginIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    override fun getUser(context: Context): OmhUserProfile? {
        val googleUser = GoogleSignIn.getLastSignedInAccount(context)
        return googleUser?.toOmhProfile()
    }

    private fun GoogleSignInAccount?.toOmhProfile(): OmhUserProfile? {
        if (this == null) return null
        return OmhUserProfile(
            name = givenName,
            surname = familyName,
            email = email,
            profileImage = photoUrl.toString()
        )
    }

    override fun getCredentials(): Any {
        val context = googleSignInClient.applicationContext
        val lastSignedInAccount: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(context)
        val scopes = lastSignedInAccount?.grantedScopes?.map { scope -> scope.scopeUri }
        return GoogleAccountCredential.usingOAuth2(context, scopes).apply {
            selectedAccount = lastSignedInAccount?.account
        }
    }

    override fun signOut() {
        googleSignInClient.signOut()
    }
}
