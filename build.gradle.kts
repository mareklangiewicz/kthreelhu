// Single-module build: the root project IS the app, so it applies the plugins itself, versioned
// (there is no parent build script to supply versions, hence TemplateFun, not TemplateFunNoVer).

import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.templatefun.*

plugins {
  plugAll(
    plugs.TemplateFun,
    plugs.KotlinMulti,
    plugs.KotlinMultiCompose,
    plugs.ComposeJb,
  )
}

defaultBuildTemplateForFullMppApp {
  implementation(KotlinX.coroutines_core)
  implementation(Langiewicz.uwidgets)
}

kotlin {
  sourceSets {
    jsMain {
      dependencies {
        implementation(npm("three", "0.132.2"))
      }
    }
  }
}
