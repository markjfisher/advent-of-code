package noodle

import glm_.vec4.Vec4
import gln.glClearColor
import gln.glViewport
import imgui.ImGui
import imgui.classes.Context
import imgui.font.Font
import imgui.impl.gl.ImplGL3
import imgui.impl.glfw.ImplGlfw
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.system.Configuration
import org.lwjgl.system.MemoryStack
import uno.glfw.GlfwWindow
import uno.glfw.VSync
import uno.glfw.glfw

class ShowDemo {
    lateinit var window: GlfwWindow
    lateinit var ctx: Context
    lateinit var implGlfw: ImplGlfw
    lateinit var implGl3: ImplGL3

    private val clearColor = Vec4(0.45f, 0.55f, 0.6f, 1f)

    var showDemoWindow = true
    var f = 0.0f
    var counter = 0
    lateinit var sysDefault: Font
    lateinit var ubuntuFont: Font

    private fun init() {
        glfw {
            errorCallback = defaultErrorCallback
            init()
            windowHint {
                visible = true
                resizable = true
                // Other bits for OpenGL version?
            }
        }
        window = GlfwWindow(900, 600, "kotlin/imgui example")
        // key call backs, and position here...

        window.makeContextCurrent()
        glfw.swapInterval = VSync.ON

        GL.createCapabilities()
        ctx = Context()
        ImGui.styleColorsDark()
        implGlfw = ImplGlfw(window, true)
        implGl3 = ImplGL3()

        // Fonts
        with(ImGui) {
            sysDefault = io.fonts.addFontDefault()
            ubuntuFont = io.fonts.addFontFromFileTTF("fonts/UbuntuMono-R.ttf", 18.0f) ?: sysDefault
            println("got font: ${ubuntuFont.isLoaded}")
        }
    }

    private fun loop(stack: MemoryStack) {
        implGl3.newFrame()
        implGlfw.newFrame()

        ImGui.run {
            newFrame()
            pushFont(ubuntuFont)
            if (showDemoWindow) showDemoWindow(::showDemoWindow)
            run {
                begin("new window")
                text("some text")
                checkbox("Demo window", ::showDemoWindow)
                sliderFloat("clear colour", ::f, 0.0f, 1.0f)
                colorEdit3("clear color", clearColor)

                if(button("Button!")) {
                    counter++
                }
                sameLine()
                text("counter = $counter")

                text("Application average %.3f ms/frame (%.1f FPS)", 1_000f / io.framerate, io.framerate)
                end()
            }
            popFont()
        }

        ImGui.render()
        glViewport(window.framebufferSize)
        glClearColor(clearColor)
        glClear(GL_COLOR_BUFFER_BIT)

        implGl3.renderDrawData(ImGui.drawData!!)

    }

    private fun run() {
        imgui.DEBUG = false
        Configuration.DEBUG.set(false)

        init()
        window.loop(::loop)

        implGl3.shutdown()
        implGlfw.shutdown()
        ctx.destroy()
        window.destroy()
        glfw.terminate()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ShowDemo().run()
        }
    }

}