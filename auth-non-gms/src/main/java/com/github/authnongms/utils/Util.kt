package com.github.authnongms.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.security.MessageDigest
import java.security.SecureRandom


private const val SIXTYFOUR_BIT_SIZE = 64

internal fun getEncryptedSharedPrefs(context: Context): SharedPreferences {
    val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    return EncryptedSharedPreferences.create(
        Constants.SHARED_PREFS_TOKEN,
        masterKey,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}

private fun getEncoding() = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP

internal fun generateCodeVerifier(): String {
    val secureRandom = SecureRandom()
    val bytes = ByteArray(SIXTYFOUR_BIT_SIZE)
    secureRandom.nextBytes(bytes)
    return Base64.encodeToString(bytes, getEncoding())
}

internal fun generateCodeChallenge(codeVerifier: String): String {
    val bytes = codeVerifier.toByteArray()
    val messageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(bytes)
    val digest = messageDigest.digest()
    return Base64.encodeToString(digest, getEncoding())
}
