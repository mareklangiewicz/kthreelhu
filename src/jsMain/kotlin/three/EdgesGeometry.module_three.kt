@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*

external interface `T$38` {
    var thresholdAngle: Number
}

open external class EdgesGeometry : BufferGeometry {
    constructor(geometry: BufferGeometry, thresholdAngle: Number = definedExternally)
    constructor(geometry: Geometry, thresholdAngle: Number = definedExternally)
    override var type: String
    open var parameters: `T$38`
}