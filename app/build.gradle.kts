
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.gamezone"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gamezone"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.activity:activity:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation ("com.google.android.gms:play-services-auth:20.5.0")
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    implementation(libs.play.services.maps)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("com.google.android.exoplayer:exoplayer:2.18.1")
    implementation ("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")
}