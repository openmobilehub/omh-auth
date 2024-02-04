/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.omh.android.auth.mobileweb.data.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.omh.android.auth.mobileweb.utils.Constants
import com.omh.android.auth.mobileweb.utils.Constants.NONCE_BYTE_LENGTH
import java.security.SecureRandom

fun getEncryptedSharedPrefs(
    context: Context,
    name: String
): SharedPreferences {
    val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    return EncryptedSharedPreferences.create(
        Constants.SHARED_PREFS_TOKEN_FORMAT.format(name),
        masterKey,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}

fun createNonce(): String {
    val sr = SecureRandom()
    val nonce = ByteArray(NONCE_BYTE_LENGTH)
    sr.nextBytes(nonce)
    return Base64.encodeToString(nonce, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
}
