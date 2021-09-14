@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*

open external class PMREMGenerator(renderer: WebGLRenderer) {
    open fun fromScene(scene: Scene, sigma: Number = definedExternally, near: Number = definedExternally, far: Number = definedExternally): WebGLRenderTarget
    open fun fromEquirectangular(equirectangular: Texture): WebGLRenderTarget
    open fun fromCubemap(cubemap: CubeTexture): WebGLRenderTarget
    open fun compileCubemapShader()
    open fun compileEquirectangularShader()
    open fun dispose()
}