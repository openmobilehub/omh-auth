package com.omh.auth.gms

import com.omh.auth.api.OmhCredentials

internal class OmhCredentialsImpl : OmhCredentials {

    override fun blockingRefreshToken(): String? {
        return "new token"
    }

    override val accessToken: String
        get() = "no token yet"
}
