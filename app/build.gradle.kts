plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.final_assignment_even_g28"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.final_assignment_even_g28"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.androidx.appcompat)
    implementation(libs.material3)
    implementation(libs.material)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.accompanist.permissions)
    implementation(libs.jetbrains.kotlinx.serialization.json)
    implementation(libs.guava)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.accompanist.permissions)
    implementation(libs.jetbrains.kotlinx.serialization.json)
    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.google.firebase.auth)

    // Also add the dependencies for the Credential Manager libraries and specify their versions
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.database)
    implementation(libs.firebase.firestore)

    // When using the BoM, you don't specify versions in Firebase library dependencies

    // Add the dependency for the Firebase SDK for Google Analytics
    implementation(libs.firebase.analytics)

// new library declaration do not work for the platform implementation
    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))
    implementation(libs.storage.kt)
    implementation(libs.realtime.kt)
    implementation(libs.supabase.postgrest.kt)
    implementation(libs.ktor.ktor.client.android)
    implementation(libs.ktor.utils)

    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
}


