plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.core.gsadmob"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
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

publishing {
    publications {
        register<MavenPublication>("release") {
            afterEvaluate{
                from(components["release"])
            }
        }
    }
}

dependencies {
    api(libs.androidx.multidex)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // admob
    api(libs.play.services.ads)
    api(libs.shimmer)

    api(libs.gscore)

    implementation(libs.androidx.lifecycle.process)

    implementation (libs.androidx.preference.ktx)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    // Add the dependency for the Firebase SDK for Google Analytics
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config.ktx)

    implementation(libs.gson)
}