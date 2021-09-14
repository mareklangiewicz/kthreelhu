@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*

external interface `T$37` {
    var points: Array<Vector2>
    var segments: Number
    var phiStart: Number
    var phiLength: Number
}

open external class LatheGeometry(points: Array<Vector2>, segments: Number = definedExternally, phiStart: Number = definedExternally, phiLength: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$37`
}