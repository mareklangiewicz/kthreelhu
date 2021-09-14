@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*

external interface `T$27` {
    var vertices: Array<Number>
    var indices: Array<Number>
    var radius: Number
    var detail: Number
}

open external class PolyhedronBufferGeometry(vertices: Array<Number>, indices: Array<Number>, radius: Number = definedExternally, detail: Number = definedExternally) : BufferGeometry {
    override var type: String
    open var parameters: `T$27`
}

open external class PolyhedronGeometry(vertices: Array<Number>, indices: Array<Number>, radius: Number = definedExternally, detail: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$27`
}