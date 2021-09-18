package pl.mareklangiewicz.kthreelhu

import kotlinx.browser.window
import kotlinx.coroutines.await

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
suspend fun processBraxSystemJsonFile(path: String) {
    val mysystem = window
        .fetch(path).await()
        .json().await() as BraxSystem
    console.log(mysystem)

    val myconfig = mysystem.config
    console.log(myconfig)

    val mydt = myconfig.dt
    console.log(mydt)

    val mycapsule0 = myconfig.bodies[0].colliders[0].capsule
    console.log(mycapsule0)
}


external interface BraxSystem {
    val config: BraxConfig
    val pos: Array<Array<Number>>
    val rot: Array<Array<Number>>
}

external interface BraxConfig {
    val bodies: Array<BraxBody>
    val joints: Any? // TODO_later
    val actuators: Any? // TODO_later
    val friction: Number
    val gravity: Vec3
    val angularDamping: Number
    val baumgarteErp: Number
    val collideInclude: Array<NamePair>
    val dt: Number
    val substeps: Number
    val frozen: BraxFrozen
    val elasticity: Number
    val velocityDamping: Number
}

external interface NamePair {
    val first: String
    val second: String
}

external interface BraxBody {
    val name: String
    val colliders: Array<BraxCollider>
    val inertia: Vec3
    val mass: Number
    val frozen: BraxFrozen
}

external interface BraxCollider {
    val position: Vec3?
    val rotation: Vec3?
    val box: BraxBox?
    val plane: BraxPlane?
    val sphere: BraxSphere?
    val capsule: BraxCapsule?
    val heightMap: BraxHeightMap?
}

external interface BraxBox {
    val halfsize: Vec3
}

external interface BraxPlane

external interface BraxSphere {
    val radius: Number
}

external interface BraxCapsule {
    val radius: Number
    val length: Number
    val end: Number
}

external interface BraxHeightMap {
    val size: Number
    val data: Array<Number>
}

external interface BraxFrozen {
    val all: Boolean?
    val position: Vec3?
    val rotation: Vec3?
}

external interface Vec3 {
    val x: Number
    val y: Number
    val z: Number
}
