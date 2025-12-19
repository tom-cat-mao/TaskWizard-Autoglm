plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.autoglm"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "com.example.autoglm"
        minSdk = 26 // Android 8.0+
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    // ==================== 签名配置 ====================
    signingConfigs {
        create("release") {
            // 从环境变量读取签名信息（GitHub Actions 会设置）
            val keystoreFile = System.getenv("KEYSTORE_FILE")
            val keystorePassword = System.getenv("KEYSTORE_PASSWORD")
            val keyAlias = System.getenv("KEY_ALIAS")
            val keyPassword = System.getenv("KEY_PASSWORD")

            if (keystoreFile != null && keystorePassword != null &&
                keyAlias != null && keyPassword != null) {
                storeFile = file(keystoreFile)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 只有在环境变量存在时才使用 release 签名配置
            // 否则使用 debug 签名（用于本地测试构建）
            val keystoreFile = System.getenv("KEYSTORE_FILE")
            if (keystoreFile != null) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                // 本地构建使用 debug 签名
                signingConfig = signingConfigs.getByName("debug")
            }
        }
        debug {
            // Debug 版本使用默认签名
        }
    }
    // ==================== 签名配置结束 ====================

    // ==================== Lint 配置（减少内存占用）====================
    lint {
        // 禁用 Release 构建时的 Lint 检查（减少内存占用）
        checkReleaseBuilds = false
        abortOnError = false
        // 只检查严重错误
        checkOnly += setOf("NewApi", "InlinedApi")
    }
    // ==================== Lint 配置结束 ====================

    buildFeatures {
        aidl = true
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Shizuku API
    implementation("dev.rikka.shizuku:api:13.1.5")
    // Shizuku Provider
    implementation("dev.rikka.shizuku:provider:13.1.5")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // JSON
    implementation("com.google.code.gson:gson:2.10.1")
}
