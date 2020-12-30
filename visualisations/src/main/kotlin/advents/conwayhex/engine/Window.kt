package advents.conwayhex.engine

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil


data class Window(
    val title: String,
    var width: Int,
    var height: Int,
    private var vSync: Boolean
) {
    private var windowHandle: Long = 0
    var isResized = false

    fun init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(GLFW.glfwInit()) { "Unable to initialize GLFW" }
        GLFW.glfwDefaultWindowHints() // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE) // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE) // the window will be resizable
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE)

        // Create the window
        windowHandle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        if (windowHandle == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        // Setup resize callback
        GLFW.glfwSetFramebufferSizeCallback(windowHandle) { window: Long, width: Int, height: Int ->
            this.width = width
            this.height = height
            isResized = true
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        GLFW.glfwSetKeyCallback(windowHandle) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(window, true) // We will detect this in the rendering loop
            }
        }

        // Get the resolution of the primary monitor
        val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
        // Center our window
        GLFW.glfwSetWindowPos(
            windowHandle,
            (vidmode!!.width() - width) / 2,
            (vidmode.height() - height) / 2
        )

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(windowHandle)
        if (isvSync()) {
            // Enable v-sync
            GLFW.glfwSwapInterval(1)
        }

        // Make the window visible
        GLFW.glfwShowWindow(windowHandle)
        GL.createCapabilities()

        // Set the clear color
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
    }

    fun setClearColor(r: Float, g: Float, b: Float, alpha: Float) {
        GL11.glClearColor(r, g, b, alpha)
    }

    fun isKeyPressed(keyCode: Int): Boolean {
        return GLFW.glfwGetKey(windowHandle, keyCode) == GLFW.GLFW_PRESS
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
        GLFW.glfwSwapBuffers(windowHandle)
        GLFW.glfwPollEvents()
    }
}