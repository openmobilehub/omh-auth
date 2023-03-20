package com.openmobilehub.auth.gms

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.openmobilehub.auth.api.OmhCredentials

internal class OmhCredentialsImpl(private val gCredentials: GoogleAccountCredential) : OmhCredentials {

    override fun blockingRefreshToken(): String? {
        return gCredentials.token
    }

    override val accessToken: String
        get() = "no token yet"
}
