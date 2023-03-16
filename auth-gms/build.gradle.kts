plugins {
    `android-base-lib`
}

android {
    namespace = "com.github.openmobilehub.authgms"
}

dependencies {
    api(project(":auth"))

    // KTX
    implementation(Libs.coreKtx)

    // Coroutines
    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    testImplementation(Libs.mockk)
    testImplementation(Libs.coroutineTesting)
}