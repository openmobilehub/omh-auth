package com.github.openmobilehub.auth.api

import android.content.Context
import android.content.Intent
import com.github.openmobilehub.auth.api.models.OmhUserProfile

interface OmhAuthClient {

    interface Builder {

        fun build(): OmhAuthClient
    }

    fun getLoginIntent(context: Context): Intent

    fun getUser(context: Context): OmhUserProfile?

    fun getCredentials(context: Context): OmhCredentials

    fun signOut(context: Context)
}
