package noodle

import glm_.has
import glm_.vec2.Vec2
import glm_.vec2.Vec2i
import glm_.vec4.Vec4
import gln.glClearColor
import gln.glViewport
import imgui.Cond
import imgui.ImGui
import imgui.WindowFlag
import imgui.classes.Context
import imgui.dsl
import imgui.font.Font
import imgui.impl.gl.ImplGL3
import imgui.impl.glfw.ImplGlfw
import imgui.or
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.system.Configuration
import org.lwjgl.system.MemoryStack
import uno.glfw.GlfwWindow
import uno.glfw.VSync
import uno.glfw.glfw

class BasicTest {
    lateinit var window: GlfwWindow
    lateinit var ctx: Context
    lateinit var implGlfw: ImplGlfw
    lateinit var implGl3: ImplGL3

    private val clearColor = Vec4(0.45f, 0.55f, 0.6f, 1f)
    var corner = 0
    val DISTANCE = 10f

    lateinit var sysDefault: Font
    lateinit var ubuntuFont: Font

    object show {
        var overlay = true
    }

    private fun init() {
        glfw {
            errorCallback = defaultErrorCallback
            init()
            windowHint {
                visible = true
                resizable = true
                decorated = true
                transparentFramebuffer = true
                // Other bits for OpenGL version?
            }
        }
        window = GlfwWindow(900, 600, "basic test")
        window.pos = Vec2i(300, 10)
        // window.opacity = 0.25f
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
        }
    }

    private fun loop(stack: MemoryStack) {
        implGl3.newFrame()
        implGlfw.newFrame()

        var windowFlags = WindowFlag.NoDecoration or WindowFlag.AlwaysAutoResize or WindowFlag.NoSavedSettings or WindowFlag.NoFocusOnAppearing or WindowFlag.NoNav

        ImGui.run {
            setNextWindowBgAlpha(1.0f)
            setNextWindowSize(Vec2(200, 200))

            newFrame()
            pushFont(ubuntuFont)
            run {
                begin("new window")
                text("some text")
                end()

                begin("another window")
                text("more text")
                end()
            }

            begin("outside run")
            text("hello")
            end()

            if (corner != -1) {
                val windowPos = Vec2{ if (corner has it + 1) io.displaySize[it] - DISTANCE else DISTANCE }
                val windowPosPivot = Vec2(if (corner has 1) 1f else 0f, if (corner has 2) 1f else 0f)
                setNextWindowPos(windowPos, Cond.Always, windowPosPivot)
                windowFlags = windowFlags or WindowFlag.NoMove
            }
            setNextWindowBgAlpha(0.35f)  // Transparent background
            dsl.window("Example: Simple overlay", show::overlay, windowFlags) {
                text("Simple overlay\nin the corner of the screen.\n(right-click to change position)")
                separator()
                text(
                    "Mouse Position: " + when {
                        isMousePosValid() -> "(%.1f,%.1f)".format(io.mousePos.x, io.mousePos.y)
                        else -> "<invalid>"
                    }
                )
                dsl.popupContextWindow {
                    dsl.menuItem("Custom", "", corner == -1) { corner = -1 }
                    dsl.menuItem("Top-left", "", corner == 0) { corner = 0 }
                    dsl.menuItem("Top-right", "", corner == 1) { corner = 1 }
                    dsl.menuItem("Bottom-left", "", corner == 2) { corner = 2 }
                    dsl.menuItem("Bottom-right", "", corner == 3) { corner = 3 }
                    if ((show::overlay)() && menuItem("Close")) (show::overlay).set(false)
                }
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
        Configuration.DEBUG.set(true)

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
            BasicTest().run()
        }
    }

}