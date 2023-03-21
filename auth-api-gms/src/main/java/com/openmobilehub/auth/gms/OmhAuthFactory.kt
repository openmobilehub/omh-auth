package com.openmobilehub.auth.gms

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.openmobilehub.auth.api.OmhAuthClient
import com.openmobilehub.auth.api.OmhCredentials

object OmhAuthFactory {

    fun getAuthClient(context: Context, scopes: Collection<String>): OmhAuthClient {
        val scopeList: MutableList<Scope> = scopes.map(::Scope).toMutableList()
        val gsoBuilder = GoogleSignInOptions.Builder()
        scopeList.forEach(gsoBuilder::requestScopes)
        val client: GoogleSignInClient = GoogleSignIn.getClient(context, gsoBuilder.build())
        return OmhAuthClientImpl(client)
    }

    fun getCredentials(context: Context, signInAccount: GoogleSignInAccount): OmhCredentials {
        val scopeCollection = signInAccount.grantedScopes.map { scope -> scope.scopeUri }
        val gCredentials = GoogleAccountCredential.usingOAuth2(context, scopeCollection)
        gCredentials.selectedAccount = signInAccount.account
        return OmhCredentialsImpl(gCredentials)
    }
}
