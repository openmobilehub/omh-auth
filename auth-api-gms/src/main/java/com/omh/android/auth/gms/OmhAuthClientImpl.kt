package com.omh.android.auth.gms

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.models.OmhUserProfile

internal class OmhAuthClientImpl(
    private val googleSignInClient: GoogleSignInClient
) : OmhAuthClient {

    override fun getLoginIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    override fun getUser(): OmhUserProfile? {
        val googleUser = GoogleSignIn.getLastSignedInAccount(googleSignInClient.applicationContext)
        return googleUser?.toOmhProfile()
    }

    private fun GoogleSignInAccount.toOmhProfile(): OmhUserProfile {
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

    override fun getAccountFromIntent(data: Intent?): OmhUserProfile {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            return account.toOmhProfile()
        } catch (e: ApiException) {
            val message = GoogleSignInStatusCodes.getStatusCodeString(e.statusCode)
            error(message) // TODO Map to OMH Exception
        }
    }
}
