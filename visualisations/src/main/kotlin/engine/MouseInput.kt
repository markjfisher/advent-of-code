package engine

import org.joml.Vector2d
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback
import org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback
import org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback

class MouseInput {
    private val previousPos: Vector2d = Vector2d(-1.0, -1.0)
    private val currentPos: Vector2d = Vector2d(0.0, 0.0)
    val displVec: Vector2f = Vector2f()
    private var inWindow = false

    var isLeftButtonPressed = false
        private set

    var isRightButtonPressed = false
        private set

    var isMiddleButtonPressed = false
        private set

    var scrollDirection: Int = 0

    fun init(window: Window) {
        glfwSetCursorPosCallback(window.windowHandle) { windowHandle: Long, xpos: Double, ypos: Double ->
            currentPos.x = xpos
            currentPos.y = ypos
        }
        glfwSetCursorEnterCallback(window.windowHandle) { windowHandle: Long, entered: Boolean -> inWindow = entered }
        glfwSetMouseButtonCallback(window.windowHandle) { windowHandle: Long, button: Int, action: Int, mode: Int ->
            isLeftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS
            isRightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS
            isMiddleButtonPressed = button == GLFW_MOUSE_BUTTON_MIDDLE && action == GLFW_PRESS
        }
        GLFW.glfwSetScrollCallback(window.windowHandle) { windowHandle: Long, xOffset: Double, yOffset: Double ->
            // we only get a yOffset = +/- 1 for forward/backward
            scrollDirection = yOffset.toInt()
        }
    }

    fun input(window: Window) {
        displVec.x = 0f
        displVec.y = 0f
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            val deltax = currentPos.x - previousPos.x
            val deltay = currentPos.y - previousPos.y
            val rotateX = deltax != 0.0
            val rotateY = deltay != 0.0
            if (rotateX) {
                displVec.y = deltax.toFloat()
            }
            if (rotateY) {
                displVec.x = deltay.toFloat()
            }
        }
        previousPos.x = currentPos.x
        previousPos.y = currentPos.y
    }

}
