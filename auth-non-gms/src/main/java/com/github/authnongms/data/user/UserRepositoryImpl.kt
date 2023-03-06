package com.github.authnongms.data.user

import android.content.SharedPreferences
import androidx.core.content.edit
import com.github.authnongms.domain.user.UserRepository
import com.github.authnongms.utils.Constants
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import java.util.*

internal class UserRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    UserRepository {

    override suspend fun handleIdToken(idToken: String, clientId: String) {
        val verifier = GoogleIdTokenVerifier.Builder(
            NetHttpTransport.Builder().build(),
            GsonFactory.getDefaultInstance()
        )
            // Specify the CLIENT_ID of the app that accesses the backend:
            .setAudience(Collections.singletonList(clientId))
            .build()

        // (Receive idTokenString by HTTPS POST)
        val googleIdToken: GoogleIdToken = verifier.verify(idToken)
        val payload: GoogleIdToken.Payload = googleIdToken.payload

        // Get profile information from payload
        val email: String = payload.email
        val name = payload[Constants.NAME_KEY].toString()
        val surname = payload[Constants.SURNAME_KEY].toString()
        val picture = payload[Constants.PICTURE_KEY].toString()

        // Use or store profile information
        sharedPreferences.edit {
            putString(Constants.NAME_KEY, name)
            putString(Constants.SURNAME_KEY, surname)
            putString(Constants.EMAIL_KEY, email)
            putString(Constants.PICTURE_KEY, picture)
        }
    }

    override fun getProfileData(): UserProfile {
        TODO("Not yet implemented")
    }
}
