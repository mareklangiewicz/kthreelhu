@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*

external interface `T$22` {
    var start: Number
    var materialIndex: Number
}

open external class DirectGeometry {
    open var id: Number
    open var uuid: String
    open var name: String
    open var type: String
    open var indices: Array<Number>
    open var vertices: Array<Vector3>
    open var normals: Array<Vector3>
    open var colors: Array<Color>
    open var uvs: Array<Vector2>
    open var uvs2: Array<Vector2>
    open var groups: Array<`T$22`>
    open var morphTargets: Array<MorphTarget>
    open var skinWeights: Array<Vector4>
    open var skinIndices: Array<Vector4>
    open var boundingBox: Box3?
    open var boundingSphere: Sphere?
    open var verticesNeedUpdate: Boolean
    open var normalsNeedUpdate: Boolean
    open var colorsNeedUpdate: Boolean
    open var uvsNeedUpdate: Boolean
    open var groupsNeedUpdate: Boolean
    open fun computeBoundingBox()
    open fun computeBoundingSphere()
    open fun computeGroups(geometry: Geometry)
    open fun fromGeometry(geometry: Geometry): DirectGeometry
    open fun dispose()
}