@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.utils.*

pluginManagement {
//    includeBuild("../DepsKt")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("pl.mareklangiewicz.deps.settings") version "0.2.36"
}

//includeAndSubstituteBuild("../upue", deps.upue, ":upue")

rootProject.name = "kthreelhu"

