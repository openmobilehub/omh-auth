package com.openmobilehub.auth.gms

import com.openmobilehub.auth.api.OmhCredentials

internal class OmhCredentialsImpl : OmhCredentials{

    override fun blockingRefreshToken(): String? {
        TODO("Not yet implemented")
    }

    override val accessToken: String?
        get() = TODO("Not yet implemented")
}
