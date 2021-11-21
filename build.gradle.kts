plugins {
    kotlin("multiplatform") version Vers.kotlin
    id("org.jetbrains.compose") version Vers.composeDesktop
}

group = "pl.mareklangiewicz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven(Repos.composeDesktopDev)
}

kotlin {
    jvm()
    js(IR) {
        browser {
//            webpackTask {
//                output.libraryTarget = "commonjs"
//            }
        }
//        useCommonJs()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("../kokpit667/widgets/src/commonMain/kotlin")
            dependencies {
                implementation(Deps.kotlinxDateTime)
                implementation(Deps.kotlinxCoroutinesCore)
                implementation(compose.runtime)
                implementation(compose.web.core)
                implementation(compose.web.widgets)
            }
        }

        val jvmMain by getting {
            kotlin.srcDir("../kokpit667/widgets/src/jvmMain/kotlin")
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
            }
        }

        val jsMain by getting {
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
        mainClass = "pl.mareklangiewicz.kthreelhu.AppKt"

        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
            packageName = "kthreelhu"
            packageVersion = "1.0.0"
        }
    }
}
