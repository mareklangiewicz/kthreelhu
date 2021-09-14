@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*

external interface `T$28` {
    var radius: Number
    var detail: Number
}

open external class DodecahedronGeometry(radius: Number = definedExternally, detail: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$28`
}