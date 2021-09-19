package pl.mareklangiewicz.kthreelhu

import kotlinx.browser.window
import kotlinx.coroutines.await
import three.js.BoxGeometry
import three.js.Color
import three.js.CylinderGeometry
import three.js.Euler
import three.js.GridHelper
import three.js.Group
import three.js.Mesh
import three.js.MeshPhongMaterial
import three.js.MeshStandardMaterial
import three.js.Object3D
import three.js.ParametricGeometry
import three.js.PlaneGeometry
import three.js.Scene
import three.js.SphereGeometry
import three.js.Vector3
import kotlin.math.PI
import kotlin.math.sqrt

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
suspend fun processBraxSystemJsonFile(path: String): Scene {
    val mysystem = window
        .fetch(path).await()
        .json().await() as BraxSystem
    console.log(mysystem)
    return createScene(mysystem)
}


val BraxBasicMaterial = MeshPhongMaterial().apply { color = Color(0x665544) }
val BraxTargetMaterial = MeshPhongMaterial().apply { color = Color(0xff2222) }

fun createScene(system: BraxSystem): Scene {
    val scene = Scene()
    system.config.bodies.forEach { body ->
        val parent = Group()
        parent.name = body.name.replace('/', '_')  // sanitize node name
        body.colliders.forEach { collider ->

            val child = collider.toObject3D()

            collider.rotation?.run {
                // convert from z-up to y-up coordinate system
                val rot = toVector3()
                rot.multiplyScalar(PI / 180)
                val eul = Euler()
                eul.setFromVector3(rot)
                child.quaternion.setFromEuler(eul)
                child.quaternion.x = -child.quaternion.x
                val tmp = child.quaternion.y
                child.quaternion.y = -child.quaternion.z
                child.quaternion.z = -tmp
            }

            // TODO: check if I can use assignment here instead of "set" as in system.js
            collider.position?.run { child.position = toVector3() }
            parent.add(child)
        }
        scene.add(parent)
    }
    return scene
}

fun BraxCollider.toObject3D() =
    box?.toObject3D()
    ?: capsule?.toObject3D()
    ?: plane?.toObject3D()
    ?: sphere?.toObject3D()
    ?: heightMap?.toObject3D()
    ?: error("Unknown collider kind")

fun BraxBox.toObject3D(): Object3D {
    val geom = BoxGeometry(
        halfsize.x * 2, halfsize.z * 2, halfsize.y * 2)
    val mesh = Mesh(geom, BraxBasicMaterial)
    mesh.castShadow = true
    // mesh.baseMaterial = mesh.material; // can't do it in Kotlin; and it's a hack anyway.
    // Let's skip "hover"/"select" feature for now (js logic in: brax repo:viewer.js:255 & 271
    return mesh
}

fun BraxCapsule.toObject3D(): Object3D {
    val sphere_geom = SphereGeometry(radius, 16, 16)
    val cylinder_geom = CylinderGeometry(radius, radius, length - radius * 2)

    val sphere1 = Mesh(sphere_geom, BraxBasicMaterial)
    sphere1.position.set(0, length / 2 - radius, 0) // TODO: check assignment instead
    sphere1.castShadow = true

    val sphere2 = Mesh(sphere_geom, BraxBasicMaterial)
    sphere2.position.set(0, -length / radius + 2, 0)
    sphere2.castShadow = true

    val cylinder = Mesh(cylinder_geom, BraxBasicMaterial)
    cylinder.castShadow = true

    val group = Group()
    group.add(sphere1, sphere2, cylinder)
    return group
}
fun BraxPlane.toObject3D(): Object3D {
    val group = Group()
    val mesh = Mesh(PlaneGeometry(2000, 2000), MeshPhongMaterial().apply {
            color = Color(0x999999)
            depthWrite = false
    })
    mesh.rotation.x = -PI / 2
    mesh.receiveShadow = true
    group.add(mesh)

    val mesh2 = GridHelper(2000, 2000, 0x000000, 0x000000)
    mesh2.material.opacity = 0.4
    mesh2.material.transparent = true
    group.add(mesh2)
    return group
}
fun BraxSphere.toObject3D(): Object3D {
    val geom = SphereGeometry(radius, 16, 16)
    val mat = BraxBasicMaterial // For now we ignore if the sphere is a "target"
    val mesh = Mesh(geom, mat)
    mesh.castShadow = true
    return mesh

}
fun BraxHeightMap.toObject3D(): Object3D {
    val size = size
    val n_subdivD = sqrt(data.size.toDouble()) - 1

    check(n_subdivD % 1.0 == 0.0) { "The data length for an height map should be a perfect square." }
    val n_subdiv = n_subdivD.toInt()

    fun builder(v: Number, u: Number, target: Vector3) {
        val idx = (v * (n_subdiv) + u * n_subdiv * (n_subdiv + 1)).toInt()
        val x = u * size.toInt()
        val y = -v * size.toInt()
        val z = data[idx]
        target.set(x, y, z).multiplyScalar(1)
    }

    val geom = ParametricGeometry(::builder, n_subdiv, n_subdiv)
    geom.normalize() // FIXME: original was: normalizeNormals instead of normalize; TODO: check BufferGeometry etc

    val group = Group()
    val mesh = Mesh(geom, MeshStandardMaterial().apply {
        color = Color(0x796049)
        flatShading = true
    })
    mesh.rotation.x = -PI / 2
    mesh.receiveShadow = true
    group.add(mesh)
    return group
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
    val gravity: BraxVector3
    val angularDamping: Number
    val baumgarteErp: Number
    val collideInclude: Array<BraxNamePair>
    val dt: Number
    val substeps: Number
    val frozen: BraxFrozen
    val elasticity: Number
    val velocityDamping: Number
}

external interface BraxNamePair {
    val first: String
    val second: String
}

external interface BraxBody {
    val name: String
    val colliders: Array<BraxCollider>
    val inertia: BraxVector3
    val mass: Number
    val frozen: BraxFrozen
}

external interface BraxCollider {
    val position: BraxVector3?
    val rotation: BraxVector3?
    val box: BraxBox?
    val plane: BraxPlane?
    val sphere: BraxSphere?
    val capsule: BraxCapsule?
    val heightMap: BraxHeightMap?
}

external interface BraxBox {
    val halfsize: BraxVector3
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
    val position: BraxVector3?
    val rotation: BraxVector3?
}

external interface BraxVector3 {
    val x: Number
    val y: Number
    val z: Number
}

fun BraxVector3.toVector3() = Vector3(x, y, z)
