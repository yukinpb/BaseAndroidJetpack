import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.flashlight.flashalert.oncall.sms"
    compileSdk = 35

    applicationVariants.configureEach {
        val formattedDate = SimpleDateFormat("MM.dd.yyyy").format(Date())
        // rename the output APK file
        outputs.configureEach {
            (this as? ApkVariantOutputImpl)?.outputFileName =
                "HV50_Flashlight_${versionName}(${versionCode})_${formattedDate}_${buildType.name}.apk"
        }

        // rename the output AAB file
        tasks.named(
            "sign${flavorName.uppercaseFirstChar()}${buildType.name.uppercaseFirstChar()}Bundle",
            com.android.build.gradle.internal.tasks.FinalizeBundleTask::class.java
        ) {
            val file = finalBundleFile.asFile.get()
            val finalFile =
                File(
                    file.parentFile,
                    "HV50_Flashlight_$versionName($versionCode)_${formattedDate}_${buildType.name}.aab"
                )
            finalBundleFile.set(finalFile)
        }
    }

    defaultConfig {
        applicationId = "com.flashlight.flashalert.oncall.sms"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

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
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.process)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Compose Destinations
    implementation(libs.compose.destinations.core)
    ksp(libs.compose.destinations.ksp)
    implementation(libs.compose.destinations.bottom.sheet)
    
    // System UI Controller
    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.compose.colorful.sliders)
    
    // Google Maps
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    implementation(libs.coil.compose)

    // FIREBASE
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.config.ktx)

    // AdMob
    implementation(libs.ads.advance.lib)
    implementation(libs.play.services.ads)
    implementation(libs.androidx.multidex)

    // Facebook ads
    implementation(libs.facebook.android.sdk)
    implementation(libs.facebook)

    implementation(libs.shimmer)
    implementation(libs.lottie)

    implementation(libs.ssp.android)
    implementation(libs.sdp.android)

    implementation(libs.androidx.constraintlayout)
}