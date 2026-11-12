import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.secrets)
    alias(libs.plugins.google.services)
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
        versionCode = 30
        versionName = "3.0.0"

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
            releaseAab.copyTo(File(rootDir, "Localiiiy-v3.0.0-build30-DEX-OPTIMIZED.aab"), overwrite = true)
            releaseAab.copyTo(File(publicDir, "Localiiiy-v3.0.0-build30-DEX-OPTIMIZED.aab"), overwrite = true)
        }
    }
}

tasks.named("preBuild") {
    dependsOn(syncWebAssets)
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
    implementation(libs.firebase.appcheck.debug)

    // Play Services & Maps
    implementation(libs.play.services.location)
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
