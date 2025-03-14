plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.gsadmob"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gsadmob"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.core.splashscreen)

    implementation(project(":GsAdmob"))

    // admob
    implementation(libs.play.services.ads)
    implementation(libs.shimmer)

    implementation(libs.gscore)

    implementation (libs.androidx.preference.ktx)

    implementation (libs.baseandroid)

    implementation(libs.lottie)
    implementation(libs.blurview)
}