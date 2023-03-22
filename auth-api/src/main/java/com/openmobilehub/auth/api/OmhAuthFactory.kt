package com.openmobilehub.auth.api

import android.content.Context

interface OmhAuthFactory {
    fun getAuthClient(context: Context, scopes: Collection<String>, clientId: String): OmhAuthClient
}
