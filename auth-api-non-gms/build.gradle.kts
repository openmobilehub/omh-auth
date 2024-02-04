plugins {
    `android-base-lib`
}

android {
    namespace = "com.omh.android.auth.nongms"

    viewBinding {
        enable = true
    }

    defaultConfig {
        buildConfigField(
            type = "String",
            name = "G_AUTH_URL",
            value = getPropertyOrFail("googleAuthUrl")
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

    // ID token resolution
    implementation(Libs.googleApiClient) {
        exclude("org.apache.httpcomponents")
    }

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
