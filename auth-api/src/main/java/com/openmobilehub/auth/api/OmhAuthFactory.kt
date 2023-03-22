package com.openmobilehub.auth.api

import android.content.Context
import kotlin.reflect.KClass

interface OmhAuthFactory {
    fun getAuthClient(context: Context, scopes: Collection<String>, clientId: String): OmhAuthClient
}
