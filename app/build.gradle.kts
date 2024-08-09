plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.kotlin)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "com.certn.mobile"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.certn.mobile"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1"

        buildConfigField("Boolean", "BETA_VERSION", "false")
        buildConfigField("Boolean", "RELEASE_VERSION", "false")
        buildConfigField("Boolean", "PROD_VERSION", "false")
        buildConfigField("String", "BASE_URL", "\"https://demo.trustmatic.io/\"")
        buildConfigField("Long", "CONNECT_TIMEOUT", "120000L")
        buildConfigField("Long", "READ_TIMEOUT", "120000L")

        vectorDrawables {
            useSupportLibrary = true
        }

        resourceConfigurations += listOf("en")

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    signingConfigs {
        create("beta") {
            storeFile = file("${projectDir}/../certnBeta_keystore.jks")
            storePassword = extra["certn_debug"].toString()
            keyAlias = "certnalias"
            keyPassword = extra["certn_debug"].toString()
            enableV3Signing = true
            enableV4Signing = true
        }
        create("release") {
            storeFile = file("${projectDir}/../certnRelease_keystore.jks")
            storePassword = extra["certn_release"].toString()
            keyAlias = "certnalias"
            keyPassword = extra["certn_release"].toString()
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        val release by getting {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "proguard-rules-release.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("Boolean", "RELEASE_VERSION", "true")
        }
        create("beta") {
            initWith(release)
            matchingFallbacks.add("release")
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            applicationIdSuffix = ".beta"
            signingConfig = signingConfigs.getByName("beta")
            buildConfigField("Boolean", "BETA_VERSION", "true")
            buildConfigField("Boolean", "RELEASE_VERSION", "false")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-Xextended-compiler-checks"
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("DebugProbesKt.bin")
            excludes.add("license/*")
        }

        dex {
            useLegacyPackaging = true
        }
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.material)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.navigation.ktx)
    implementation(libs.com.github.tgo1014.jp2ForAndroid)
    implementation(libs.timber)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi.core)
    implementation(libs.moshi.adapters)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)
    implementation(libs.security.crypto)
    implementation(libs.mlkit.barcode)
    implementation(libs.camera.camera2)
    implementation(libs.camera.mlkit)
    implementation(libs.camera.lifecycle)
    implementation(libs.gson)
    implementation(libs.co.certn.id.sdk)

}