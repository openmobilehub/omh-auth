import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

subprojects {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }
}

tasks.register("installPrePushHook", Copy::class) {
    from("tools/scripts/pre-push")
    into(".git/hooks")
    fileMode = 0b000_111_111_111
}

tasks.register("installPreCommitHook", Copy::class) {
    from("tools/scripts/pre-commit")
    into(".git/hooks")
    fileMode = 0b000_111_111_111
}

tasks {
    val installPrePushHook by existing
    val installPreCommitHook by existing
    getByName("prepareKotlinBuildScriptModel").dependsOn(installPrePushHook)
    getByName("prepareKotlinBuildScriptModel").dependsOn(installPreCommitHook)
}

val ossrhUsername by extra(lookForVariable("ossrhUsername"))
val ossrhPassword by extra(lookForVariable("ossrhPassword"))
val mStagingProfileId by extra(lookForVariable("stagingProfileId"))
val signingKeyId by extra(lookForVariable("signing.keyId"))
val signingPassword by extra(lookForVariable("signing.password"))
val signingKey by extra(lookForVariable("signing.key"))

// Set up Sonatype repository
nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(mStagingProfileId.toString())
            username.set(ossrhUsername.toString())
            password.set(ossrhPassword.toString())
            // Add these lines if using new Sonatype infra
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

fun lookForVariable(name: String): Any? {
    val localProperties = gradleLocalProperties(rootDir)
    return System.getenv(name) ?: localProperties[name]
}
