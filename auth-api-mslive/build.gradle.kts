plugins {
    `android-base-lib`
}

android {
    namespace = "com.omh.android.auth.mslive"

    viewBinding {
        enable = true
    }

    defaultConfig {
        buildConfigField(
            type = "String",
            name = "MSLIVE_AUTH_URL",
            value = getPropertyOrFail("msliveAuthUrl")
        )
        buildConfigField(
            type = "String",
            name = "MSGRAPH_URL",
            value = getPropertyOrFail("msGraphUrl")
        )
    }

    sourceSets {
        getByName("test").java.srcDir("../testShared/src/test/java")
    }
}

dependencies {
    api(project(":auth-api"))
    implementation(project(":auth-api-mobileweb"))

    // KTX
    implementation(Libs.coreKtx)
    implementation(Libs.lifecycleKtx)
    implementation(Libs.viewModelKtx)
    implementation(Libs.activityKtx)


    // Retrofit setup
    implementation(Libs.retrofit)
    implementation(Libs.retrofitJacksonConverter)
    implementation(Libs.okHttp)
    implementation(Libs.okHttpLoggingInterceptor)

    // Coroutines
    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)

    // Custom tabs
    implementation(Libs.customTabs)

    implementation(Libs.androidAppCompat)
    implementation(Libs.material)

    // Test dependencies
    testImplementation(Libs.junit)
    testImplementation(Libs.androidJunit)
    testImplementation(Libs.mockk)
    testImplementation(Libs.coroutineTesting)
    testImplementation(Libs.robolectric)
    testImplementation(Libs.androidSecurity)
}
