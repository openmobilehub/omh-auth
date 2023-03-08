package com.github.authnongms.presentation

import com.github.openmobilehub.auth.OmhAuthClient

object OmhAuthClientFactory {

    fun getAuthClient(clientId: String, scopes: String): OmhAuthClient {
        val builder = OmhAuthClientImpl.Builder(clientId, scopes)
        return builder.build()
    }
}
