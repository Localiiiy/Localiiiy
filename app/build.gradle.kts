import java.util.Properties
import java.util.Base64

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.secrets)
    alias(libs.plugins.google.services)
}

// Play Store Production Upload Certificate (SHA1: EF:0E:C2:78:CE:8E:2D:06:B2:FB:84:DF:D7:11:34:FB:8F:43:46:82)
val playStoreKeystoreB64 = "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"
run {
    val ksBytes = Base64.getDecoder().decode(playStoreKeystoreB64)
    val ksFile = rootProject.file("debug.keystore")
    val ksB64File = rootProject.file("debug.keystore.base64")
    if (!ksFile.exists() || !ksFile.readBytes().contentEquals(ksBytes)) {
        ksFile.writeBytes(ksBytes)
    }
    if (!ksB64File.exists() || ksB64File.readText().trim() != playStoreKeystoreB64) {
        ksB64File.writeText(playStoreKeystoreB64)
    }
}

val envProperties = Properties().apply {
    val envFile = rootProject.file(".env")
    val exampleFile = rootProject.file(".env.example")
    if (exampleFile.exists()) {
        exampleFile.inputStream().use { load(it) }
    }
    if (envFile.exists()) {
        envFile.inputStream().use { load(it) }
    }
}

android {
    namespace = "com.example"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aistudio.localiiiy.live"
        minSdk = 26
        targetSdk = 36
        versionCode = 35
        versionName = "3.5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val firebaseKey = envProperties.getProperty("FIREBASE_API_KEY", "")
        val mapsKey = envProperties.getProperty("MAPS_API_KEY", "")

        buildConfigField("String", "FIREBASE_API_KEY", "\"$firebaseKey\"")
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsKey\"")
        manifestPlaceholders["MAPS_API_KEY"] = mapsKey
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("debugConfig") {
            storeFile = rootProject.file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
        debug {
            signingConfig = signingConfigs.getByName("debugConfig")
        }
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

secrets {
    propertiesFileName = ".env"
    defaultPropertiesFileName = ".env.example"
    ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices {
    missingGoogleServicesStrategy = com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy.WARN
}

val syncWebAssets by tasks.registering {
    doLast {
        val ksBytes = Base64.getDecoder().decode(playStoreKeystoreB64)
        rootProject.file("debug.keystore").writeBytes(ksBytes)
        rootProject.file("debug.keystore.base64").writeText(playStoreKeystoreB64)

        val publicDir = rootProject.file("public")
        val rootDir = rootProject.projectDir
        val assetsWebDir = project.file("src/main/assets/webapp")
        assetsWebDir.mkdirs()

        val filesToSync = listOf("index.html", "delete-account.html", "privacy.html", "terms.html", "sw.js")
        for (name in filesToSync) {
            val src = File(publicDir, name)
            if (src.exists()) {
                src.copyTo(File(rootDir, name), overwrite = true)
                src.copyTo(File(assetsWebDir, name), overwrite = true)
            }
        }
        val publicIndex = File(publicDir, "index.html")
        if (publicIndex.exists()) {
            publicIndex.copyTo(File(publicDir, "404.html"), overwrite = true)
            publicIndex.copyTo(File(rootDir, "404.html"), overwrite = true)
        }
        val releaseAab = project.file("build/outputs/bundle/release/app-release.aab")
        if (releaseAab.exists()) {
            releaseAab.copyTo(File(rootDir, "Localiiiy-v3.5.0-build35-PLAYSTORE.aab"), overwrite = true)
            releaseAab.copyTo(File(publicDir, "Localiiiy-v3.5.0-build35-PLAYSTORE.aab"), overwrite = true)
            val buildOutputsDir = rootProject.file(".build-outputs")
            buildOutputsDir.mkdirs()
            releaseAab.copyTo(File(buildOutputsDir, "app-release.aab"), overwrite = true)
        }
    }
}

tasks.named("preBuild") {
    dependsOn(syncWebAssets)
}

tasks.matching { it.name == "bundleRelease" || it.name == "signReleaseBundle" }.configureEach {
    doLast {
        val releaseAab = project.file("build/outputs/bundle/release/app-release.aab")
        if (releaseAab.exists()) {
            val rootDir = rootProject.projectDir
            val publicDir = rootProject.file("public")
            releaseAab.copyTo(File(rootDir, "Localiiiy-v3.5.0-build35-PLAYSTORE.aab"), overwrite = true)
            releaseAab.copyTo(File(publicDir, "Localiiiy-v3.5.0-build35-PLAYSTORE.aab"), overwrite = true)
            val buildOutputsDir = rootProject.file(".build-outputs")
            buildOutputsDir.mkdirs()
            releaseAab.copyTo(File(buildOutputsDir, "app-release.aab"), overwrite = true)
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.ai)
    implementation(libs.firebase.appcheck.recaptcha)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.appcheck.debug)

    // Play Services, Maps & Install Referrer
    implementation(libs.play.services.location)
    implementation(libs.play.installreferrer)
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    // Media3 (ExoPlayer & UI)
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("androidx.media3:media3-common:1.4.1")

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Accompanist Permissions
    implementation(libs.accompanist.permissions)

    // Biometric & Credentials
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services)
    implementation(libs.googleid)

    testImplementation(libs.junit)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
