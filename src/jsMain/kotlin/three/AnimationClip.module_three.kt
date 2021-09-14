@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*

open external class AnimationClip(name: String = definedExternally, duration: Number = definedExternally, tracks: Array<KeyframeTrack> = definedExternally, blendMode: AnimationBlendMode = definedExternally) {
    open var name: String
    open var tracks: Array<KeyframeTrack>
    open var blendMode: AnimationBlendMode
    open var duration: Number
    open var uuid: String
    open var results: Array<Any>
    open fun resetDuration(): AnimationClip
    open fun trim(): AnimationClip
    open fun validate(): Boolean
    open fun optimize(): AnimationClip
    open fun clone(): AnimationClip

    companion object {
        fun CreateFromMorphTargetSequence(name: String, morphTargetSequence: Array<MorphTarget>, fps: Number, noLoop: Boolean): AnimationClip
        fun findByName(clipArray: Array<AnimationClip>, name: String): AnimationClip
        fun CreateClipsFromMorphTargetSequences(morphTargets: Array<MorphTarget>, fps: Number, noLoop: Boolean): Array<AnimationClip>
        fun parse(json: Any): AnimationClip
        fun parseAnimation(animation: Any, bones: Array<Bone>): AnimationClip
        fun toJSON(): Any
    }
}