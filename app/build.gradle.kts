plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id ("com.google.devtools.ksp")
}

android {
    namespace = "ru.yandex.buggyweatherapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.yandex.buggyweatherapp"
        minSdk = 24
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
        buildConfig = true
    }

    buildTypes {
        release {
            buildConfigField("String", "API_KEY", "\"8fd9a0f2216e2bc16a09102e2af8ab1d\"")
            buildConfigField("String", "BASE_URL", "\"http://api.openweathermap.org/data/2.5/\"")
        }
        debug {
            buildConfigField("String", "API_KEY", "\"8fd9a0f2216e2bc16a09102e2af8ab1d\"")
            buildConfigField("String", "BASE_URL", "\"http://api.openweathermap.org/data/2.5/\"")
        }
    }
}

dependencies {
    // Android core libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.runtime.livedata)
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Network
    implementation (libs.retrofit)
    implementation (libs.gson)
    implementation (libs.okhttp)
    implementation (libs.converter.gson)
    implementation (libs.logging.interceptor)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Location
    implementation(libs.play.services.location)
    
    // Coil image loading
    implementation(libs.coil.compose)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Dagger2
    implementation(libs.dagger)
    ksp (libs.dagger.compiler)
}