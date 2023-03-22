package com.openmobilehub.auth.api

import android.content.Context
import android.util.Log
import kotlin.reflect.KClass

object OmhAuthProvider {

    private const val GMS_ADDRESS = "com.openmobilehub.auth.nongms.presentation.OmhAuthFactoryImpl"
    private const val NGMS_ADDRESS = "com.openmobilehub.auth.gms.presentation.OmhAuthFactoryImpl"

    fun provideAuthClient(
        context: Context,
        scopes: Collection<String>,
        clientId: String
    ): OmhAuthClient {
        val omhAuthFactory = try {
            val clazz: KClass<out Any> = Class.forName(GMS_ADDRESS).kotlin
            clazz.objectInstance as OmhAuthFactory
        } catch (e: ClassNotFoundException) {
            Log.e("Auth provider", "Class not found", e) // Temporal for detekt SwallowedException rule
            val clazz: KClass<out Any> = Class.forName(NGMS_ADDRESS).kotlin
            clazz.objectInstance as OmhAuthFactory
        }
        return omhAuthFactory.getAuthClient(context, scopes, clientId)
    }
}
