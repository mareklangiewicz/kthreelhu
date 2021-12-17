@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.deps.includeAndSubstituteBuild

pluginManagement {
    includeBuild("../deps.kt")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("pl.mareklangiewicz.deps.settings")
}

includeAndSubstituteBuild("../upue", Deps.upue, ":upue")

rootProject.name = "kthreelhu"

