package com.openmobilehub.auth.gms

import com.openmobilehub.auth.api.OmhCredentials

internal class OmhCredentialsImpl : OmhCredentials {

    override fun blockingRefreshToken(): String? {
        return "new token" // TODO
    }

    override val accessToken: String
        get() = "no token yet" // TODO
}
