import org.jetbrains.compose.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.dsl.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.utils.*


plugins {
    kotlin("multiplatform") version vers.kotlin
    id("org.jetbrains.compose") version vers.composeJb
}


private val kthreelhuBuildFile = rootProjectPath / "build.gradle.kts" // it's just this file

tasks.registerAllThatGroupFun("inject",
    ::checkTemplates,
    ::injectTemplates,
)

fun checkTemplates() {
    checkKotlinModuleBuildTemplates(kthreelhuBuildFile)
    checkMppModuleBuildTemplates(kthreelhuBuildFile)
    checkComposeMppModuleBuildTemplates(kthreelhuBuildFile)
    checkComposeMppAppBuildTemplates(kthreelhuBuildFile)
}

fun injectTemplates() {
    injectKotlinModuleBuildTemplate(kthreelhuBuildFile)
    injectMppModuleBuildTemplate(kthreelhuBuildFile)
    injectComposeMppModuleBuildTemplate(kthreelhuBuildFile)
    injectComposeMppAppBuildTemplate(kthreelhuBuildFile)
}

defaultBuildTemplateForComposeMppApp(
    appMainPackage = "pl.mareklangiewicz.kthreelhu",
    details = libs.Kthreelhu,
    withComposeWebWidgets = true,
) {
    // TODO: I have to repeat some compose dependencies here in common, because strange issues with web.widgets.
    //   (web.widgets tried to be "common" and have common modifiers abstractions etc.)
    //   TD: Remove these three dependencies from here when I get rid of (deprecated) web.widgets.
    implementation(compose.web.core)
    @Suppress("DEPRECATION")
    implementation(compose.web.widgets)

    implementation(deps.kotlinxDateTime)
    implementation(deps.kotlinxCoroutinesCore)
    implementation(deps.upue)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("../UWidgets/uwidgets/src/commonMain/kotlin")
            kotlin.srcDir("../kokpit667/kommon/src/commonMain/kotlin")
            kotlin.srcDir("../kokpit667/widgets/src/commonMain/kotlin")
        }
        val jvmMain by getting {
            kotlin.srcDir("../UWidgets/uwidgets/src/jvmMain/kotlin")
            kotlin.srcDir("../kokpit667/kommon/src/jvmMain/kotlin")
            kotlin.srcDir("../kokpit667/widgets/src/jvmMain/kotlin")
        }
        val jsMain by getting {
            kotlin.srcDir("../UWidgets/uwidgets/src/jsMain/kotlin")
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

// region [Kotlin Module Build Template]

fun TaskCollection<Task>.defaultKotlinCompileOptions(
    jvmTargetVer: String = vers.defaultJvm,
    requiresOptIn: Boolean = true
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = jvmTargetVer
        if (requiresOptIn) freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}

// endregion [Kotlin Module Build Template]

// region [MPP Module Build Template]

/** Only for very standard small libs. In most cases it's better to not use this function. */
fun Project.defaultBuildTemplateForMppLib(
    details: LibDetails = libs.Unknown,
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withComposeJbDevRepo: Boolean = false,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    repositories { defaultRepos(withKotlinxHtml = withKotlinxHtml, withComposeJbDev = withComposeJbDevRepo) }
    defaultGroupAndVerAndDescription(details)
    kotlin { allDefault(
        withJvm,
        withJs,
        withNativeLinux64,
        withKotlinxHtml,
        withTestJUnit5,
        withTestUSpekX,
        addCommonMainDependencies
    ) }
    tasks.defaultKotlinCompileOptions()
    tasks.defaultTestsOptions(onJvmUseJUnitPlatform = withTestJUnit5)
    if (plugins.hasPlugin("maven-publish")) {
        defaultPublishing(details)
        if (plugins.hasPlugin("signing")) defaultSigning()
        else println("MPP Module ${name}: signing disabled")
    }
    else println("MPP Module ${name}: publishing (and signing) disabled")
}

/** Only for very standard small libs. In most cases it's better to not use this function. */
@Suppress("UNUSED_VARIABLE")
fun KotlinMultiplatformExtension.allDefault(
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    if (withJvm) jvm()
    if (withJs) jsDefault()
    if (withNativeLinux64) linuxX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                if (withKotlinxHtml) implementation(deps.kotlinxHtml)
                addCommonMainDependencies()
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                if (withTestUSpekX) implementation(deps.uspekx)
            }
        }
        if (withJvm) {
            val jvmTest by getting {
                dependencies {
                    if (withTestJUnit5) implementation(deps.junit5engine)
                }
            }
        }
        if (withNativeLinux64) {
            val linuxX64Main by getting
            val linuxX64Test by getting
        }
    }
}


fun KotlinMultiplatformExtension.jsDefault(
    withBrowser: Boolean = true,
    withNode: Boolean = false,
    testWithChrome: Boolean = true,
    testHeadless: Boolean = true,
) {
    js(IR) {
        if (withBrowser) browser {
            testTask {
                useKarma {
                    when (testWithChrome to testHeadless) {
                        true to true -> useChromeHeadless()
                        true to false -> useChrome()
                    }
                }
            }
        }
        if (withNode) nodejs()
    }
}

// endregion [MPP Module Build Template]

// region [Compose MPP Module Build Template]

/** Only for very standard compose mpp libs. In most cases it's better to not use this function. */
@Suppress("UNUSED_VARIABLE")
@OptIn(ExperimentalComposeLibrary::class)
fun Project.defaultBuildTemplateForComposeMppLib(
    details: LibDetails = libs.Unknown,
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withComposeUi: Boolean = true,
    withComposeFoundation: Boolean = true,
    withComposeMaterial2: Boolean = withJvm,
    withComposeMaterial3: Boolean = withJvm,
    withComposeMaterialIconsExtended: Boolean = withJvm,
    withComposeFullAnimation: Boolean = withJvm,
    withComposeDesktop: Boolean = withJvm,
    withComposeDesktopComponents: Boolean = withJvm,
    withComposeWebCore: Boolean = withJs,
    withComposeWebWidgets: Boolean = false,
    withComposeWebSvg: Boolean = withJs,
    withComposeTestUiJUnit4: Boolean = withJvm,
    withComposeTestWebUtils: Boolean = withJs,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    defaultBuildTemplateForMppLib(
        details = details,
        withJvm = withJvm,
        withJs = withJs,
        withNativeLinux64 = withNativeLinux64,
        withKotlinxHtml = withKotlinxHtml,
        withComposeJbDevRepo = true,
        withTestJUnit5 = false, // Unfortunately Compose UI steel uses JUnit4 instead of 5
        withTestUSpekX = true,
        addCommonMainDependencies = addCommonMainDependencies
    )
    kotlin {
        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation(compose.runtime)
                    if (withComposeUi) {
                        implementation(compose.ui)
                    }
                    if (withComposeFoundation) implementation(compose.foundation)
                    if (withComposeFullAnimation) implementation(compose.animation)
                    if (withComposeMaterial2) implementation(compose.material)
                }
            }
            val jvmMain by getting {
                dependencies {
                    if (withComposeUi) {
                        implementation(compose.uiTooling)
                        implementation(compose.preview)
                    }
                    if (withComposeFullAnimation) implementation(compose.animationGraphics)
                    if (withComposeMaterial3) implementation(compose.material3)
                    if (withComposeMaterialIconsExtended) implementation(compose.materialIconsExtended)
                    if (withComposeDesktop) {
                        implementation(compose.desktop.common)
                        implementation(compose.desktop.currentOs)
                    }
                    if (withComposeDesktopComponents) {
                        implementation(compose.desktop.components.splitPane)
                    }
                }
            }
            val jsMain by getting {
                dependencies {
                    implementation(compose.runtime)
                    if (withComposeWebCore) implementation(compose.web.core)
                    @Suppress("DEPRECATION")
                    if (withComposeWebWidgets) implementation(compose.web.widgets)
                    if (withComposeWebSvg) implementation(compose.web.svg)
                }
            }
            val jvmTest by getting {
                dependencies {
                    if (withComposeTestUiJUnit4) implementation(compose.uiTestJUnit4)
                }
            }
            val jsTest by getting {
                dependencies {
                    if (withComposeTestWebUtils) implementation(compose.web.testUtils)
                }
            }
        }
    }
}

// endregion [Compose MPP Module Build Template]

// region [Compose MPP App Build Template]

/** Only for very standard compose mpp apps. In most cases it's better to not use this function. */
@Suppress("UNUSED_VARIABLE")
fun Project.defaultBuildTemplateForComposeMppApp(
    appMainPackage: String,
    appMainClass: String = "App_jvmKt", // for compose jvm
    appMainFun: String = "main", // for native
    details: LibDetails = libs.Unknown,
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withComposeUi: Boolean = true,
    withComposeFoundation: Boolean = true,
    withComposeMaterial2: Boolean = withJvm,
    withComposeMaterial3: Boolean = withJvm,
    withComposeMaterialIconsExtended: Boolean = withJvm,
    withComposeFullAnimation: Boolean = withJvm,
    withComposeDesktop: Boolean = withJvm,
    withComposeDesktopComponents: Boolean = withJvm,
    withComposeWebCore: Boolean = withJs,
    withComposeWebWidgets: Boolean = false,
    withComposeWebSvg: Boolean = withJs,
    withComposeTestUiJUnit4: Boolean = withJvm,
    withComposeTestWebUtils: Boolean = withJs,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    defaultBuildTemplateForComposeMppLib(
        details = details,
        withJvm = withJvm,
        withJs = withJs,
        withNativeLinux64 = withNativeLinux64,
        withKotlinxHtml = withKotlinxHtml,
        withComposeUi = withComposeUi,
        withComposeFoundation = withComposeFoundation,
        withComposeMaterial2 = withComposeMaterial2,
        withComposeMaterial3 = withComposeMaterial3,
        withComposeMaterialIconsExtended = withComposeMaterialIconsExtended,
        withComposeFullAnimation = withComposeFullAnimation,
        withComposeDesktop = withComposeDesktop,
        withComposeDesktopComponents = withComposeDesktopComponents,
        withComposeWebCore = withComposeWebCore,
        withComposeWebWidgets = withComposeWebWidgets,
        withComposeWebSvg = withComposeWebSvg,
        withComposeTestUiJUnit4 = withComposeTestUiJUnit4,
        withComposeTestWebUtils = withComposeTestWebUtils,
        addCommonMainDependencies = addCommonMainDependencies
    )
    kotlin {
        if (withJs) js(IR) {
            binaries.executable()
        }
        if (withNativeLinux64) linuxX64 {
            binaries {
                executable {
                    entryPoint = "$appMainPackage.$appMainFun"
                }
            }
        }
    }
    if (withJvm) {
        compose.desktop {
            application {
                mainClass = "$appMainPackage.$appMainClass"
                nativeDistributions {
                    targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
                    packageName = details.name
                    packageVersion = details.version
                    description = details.description
                }
            }
        }
    }
}

// endregion [Compose MPP App Build Template]