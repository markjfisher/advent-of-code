package advents.conwayhex.engine.graph

import advents.conwayhex.engine.item.GameItem
import org.joml.Math.toRadians
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

class Transformation {
    private val projectionMatrix: Matrix4f = Matrix4f()
    private val modelViewMatrix: Matrix4f = Matrix4f()
    private val viewMatrix: Matrix4f = Matrix4f()
    private val modelMatrix: Matrix4f = Matrix4f()

    fun getProjectionMatrix(fov: Float, width: Float, height: Float, zNear: Float, zFar: Float): Matrix4f {
        return projectionMatrix.setPerspective(fov, width / height, zNear, zFar)
    }

    fun getViewMatrix(camera: Camera): Matrix4f {
        val cameraPos = camera.position
        val rotation = camera.rotation
        viewMatrix.identity()

        return viewMatrix
            .rotate(rotation)
            .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)
    }

    fun buildModelViewMatrix(gameItem: GameItem, viewMatrix: Matrix4f): Matrix4f {
        return buildModelViewMatrix(buildModelMatrix(gameItem), viewMatrix)
    }

    fun buildModelViewMatrix(modelMatrix: Matrix4f, viewMatrix: Matrix4f): Matrix4f {
        return viewMatrix.mulAffine(modelMatrix, modelViewMatrix)
    }

    fun buildModelMatrix(gameItem: GameItem): Matrix4f {
        val rotation = gameItem.rotation
        return modelMatrix.translationRotateScale(
            gameItem.position.x,
            gameItem.position.y,
            gameItem.position.z,
            rotation.x,
            rotation.y,
            rotation.z,
            rotation.w,
            gameItem.scale,
            gameItem.scale,
            gameItem.scale
        )
    }
}