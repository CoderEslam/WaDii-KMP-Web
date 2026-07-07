plugins {
    kotlin("multiplatform") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("org.jetbrains.compose") version "1.7.3"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "wadii.js"
                cssSupport { enabled.set(true) }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.html.core)
                implementation(compose.runtime)
                implementation(compose.runtimeSaveable)
                implementation(compose.material)

                implementation(libs.ktor.client.js)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenModel)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)

                implementation(libs.koin.core)
                implementation(libs.koin.compose)
//                implementation(libs.koin.compose.viewmodel)
                implementation(libs.ktor.client.logging)
                implementation(libs.multiplatformSettings)

                //            implementation(libs.country.picker.kmp)
                implementation(libs.sketch.compose)
                // Provides the ability to load network images
                implementation(libs.sketch.http)

                // Agora Web SDK for video/voice calling
                implementation(npm("agora-rtc-sdk-ng", "4.24.3"))

                // Firebase Web SDK (Cloud Messaging) for push notification tokens
                implementation(npm("firebase", "11.6.0"))

            }
        }
    }
}
