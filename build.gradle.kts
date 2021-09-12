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
        browser()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.web.widgets)
                implementation(compose.runtime)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
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

// a temporary workaround for a bug in jsRun invocation - see https://youtrack.jetbrains.com/issue/KT-48273
afterEvaluate {
    extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        versions.webpackDevServer.version = "4.0.0"
    }
}
