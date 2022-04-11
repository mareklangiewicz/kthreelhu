plugins {
    kotlin("multiplatform") version vers.kotlinForCompose
    id("org.jetbrains.compose") version vers.composeJb
}

group = "pl.mareklangiewicz"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    google()
    maven(repos.composeDesktopDev)
}

kotlin {
    jvm()
    js(IR) {
        browser {
            webpackTask {
//                output.libraryTarget = "var"
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("../kokpit667/kommon/src/commonMain/kotlin")
            kotlin.srcDir("../kokpit667/widgets/src/commonMain/kotlin")
            dependencies {
                implementation(deps.kotlinxDateTime)
                implementation(deps.kotlinxCoroutinesCore)
                implementation(compose.runtime)
                implementation(compose.web.core)
                implementation(compose.web.widgets)
                implementation(deps.upue)
            }
        }

        val jvmMain by getting {
            kotlin.srcDir("../kokpit667/kommon/src/jvmMain/kotlin")
            kotlin.srcDir("../kokpit667/widgets/src/jvmMain/kotlin")
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.ui)
                implementation(compose.foundation)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.material3)
                implementation(compose.material)
            }
        }

        val jsMain by getting {
            kotlin.srcDir("../kokpit667/kommon/src/jsMain/kotlin")
            kotlin.srcDir("../kokpit667/widgets/src/jsMain/kotlin")
            dependencies {
//                implementation("ch.viseon.threejs:wrapper:126.0.0")
//                implementation(npm("three", "0.126.0"))
                implementation(npm("three", "0.132.2"))
            }
        }
    }
}
compose.desktop {
    application {
        mainClass = "pl.mareklangiewicz.kthreelhu.App_jvmKt"

        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
            packageName = "kthreelhu"
            packageVersion = "1.0.0"
        }
    }
}
