package com.github.openmobilehub.auth

import android.content.Context
import android.content.Intent

interface OmhAuthClient {

    interface Builder {

        fun build(): OmhAuthClient
    }

    fun getLoginIntent(context: Context): Intent
}
