package advents.conwayhex.engine

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR
import org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE
import org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT
import org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import org.lwjgl.glfw.GLFW.GLFW_RESIZABLE
import org.lwjgl.glfw.GLFW.GLFW_VISIBLE
import org.lwjgl.glfw.GLFW.glfwCreateWindow
import org.lwjgl.glfw.GLFW.glfwDefaultWindowHints
import org.lwjgl.glfw.GLFW.glfwGetVideoMode
import org.lwjgl.glfw.GLFW.glfwInit
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback
import org.lwjgl.glfw.GLFW.glfwSetKeyCallback
import org.lwjgl.glfw.GLFW.glfwSetWindowPos
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.glfw.GLFW.glfwShowWindow
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.glfw.GLFW.glfwSwapInterval
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.glfw.GLFWErrorCallback.createPrint
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL11C.GL_DEPTH_TEST
import org.lwjgl.opengl.GL11C.GL_FALSE
import org.lwjgl.opengl.GL11C.GL_FRONT_AND_BACK
import org.lwjgl.opengl.GL11C.GL_LINE
import org.lwjgl.opengl.GL11C.GL_TRUE
import org.lwjgl.opengl.GL11C.glClearColor
import org.lwjgl.opengl.GL11C.glEnable
import org.lwjgl.opengl.GL11C.glPolygonMode
import org.lwjgl.system.MemoryUtil

data class Window(
    val title: String,
    var width: Int,
    var height: Int,
    private var vSync: Boolean
) {
    var windowHandle: Long = 0
    var isResized = false

    fun init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(glfwInit()) { "Unable to initialize GLFW" }
        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE) // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)

        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        if (windowHandle == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        // Setup resize callback
        glfwSetFramebufferSizeCallback(windowHandle) { window: Long, width: Int, height: Int ->
            this.width = width
            this.height = height
            isResized = true
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowHandle) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true) // We will detect this in the rendering loop
            }
        }

        // Get the resolution of the primary monitor
        val vidmode = glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()) ?: throw Exception("Cannot get primary monitor information")
        // Center our window
        glfwSetWindowPos(
            windowHandle,
            (vidmode.width() - width) / 2,
            (vidmode.height() - height) / 2
        )

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle)
        if (isvSync()) {
            // Enable v-sync
            glfwSwapInterval(1)
        }

        // Make the window visible
        glfwShowWindow(windowHandle)
        createCapabilities()

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
    }

    fun setClearColor(r: Float, g: Float, b: Float, alpha: Float) {
        glClearColor(r, g, b, alpha)
    }

    fun isKeyPressed(keyCode: Int): Boolean {
        return GLFW.glfwGetKey(windowHandle, keyCode) == GLFW_PRESS
    }

    fun windowShouldClose(): Boolean {
        return GLFW.glfwWindowShouldClose(windowHandle)
    }

    fun isvSync(): Boolean {
        return vSync
    }

    fun setvSync(vSync: Boolean) {
        this.vSync = vSync
    }

    fun update() {
        glfwSwapBuffers(windowHandle)
        glfwPollEvents()
    }
}