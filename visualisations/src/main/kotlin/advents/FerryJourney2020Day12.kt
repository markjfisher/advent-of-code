package advents

import glm_.vec2.Vec2
import glm_.vec2.Vec2i
import glm_.vec4.Vec4
import gln.glClearColor
import gln.glViewport
import imgui.Cond
import imgui.ImGui
import imgui.classes.Context
import imgui.impl.gl.ImplGL3
import imgui.impl.glfw.ImplGlfw
import net.fish.resourceLines
import net.fish.y2020.Day12
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.system.Configuration
import org.lwjgl.system.MemoryStack
import uno.glfw.GlfwWindow
import uno.glfw.VSync
import uno.glfw.glfw
import java.lang.Integer.max

class FerryJourney2020Day12(private val ferryPositions: List<Pair<Int, Int>>) {
    var dataPosition = 0.5f
    var animate = false
    var colTrail = Vec4(1.0f, 1.0f, 0.4f, 1.0f)
    var colHead = Vec4(0.2f, 0.2f, 0.9f, 1.0f)
    var headRadius = 5.0f

    var progress = 0.0f
    var progressDir = 1.0f

    val percentagePlotX = 1.0f
    val percentagePlotY = 0.75f

    val minX = ferryPositions.minByOrNull { it.first }!!.first
    val minY = ferryPositions.minByOrNull { it.second }!!.second
    val maxX = ferryPositions.maxByOrNull { it.first }!!.first
    val maxY = ferryPositions.maxByOrNull { it.second }!!.second

    val rangeX = maxX - minX
    val rangeY = maxY - minY

    lateinit var window: GlfwWindow
    lateinit var ctx: Context
    lateinit var implGlfw: ImplGlfw
    lateinit var implGl3: ImplGL3

    private val clearColor = Vec4(0.45f, 0.55f, 0.6f, 1f)

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
                context.version = "3.1"

            }
        }
        window = GlfwWindow(820, 620, "vis12")
        window.pos = Vec2i(250, 250)
        // key call backs, and position here...

        window.makeContextCurrent()
        glfw.swapInterval = VSync.ON

        GL.createCapabilities()
        ctx = Context()
        ImGui.styleColorsDark()
        implGlfw = ImplGlfw(window, true)
        implGl3 = ImplGL3()

    }

    private fun loop(stack: MemoryStack) {
        implGl3.newFrame()
        implGlfw.newFrame()

        ImGui.run {
            setNextWindowBgAlpha(1.0f)
            newFrame()
            run {
                setNextWindowSize(Vec2(800, 600), Cond.FirstUseEver)
                setNextWindowPos(Vec2(10, 10), Cond.FirstUseEver)

                begin("plot output")
                checkbox("Animate", ::animate)
                sameLine()
                sliderFloat("Route Data", ::dataPosition, 0.0f, 1.0f)
                colorEdit4("Head Colour", colHead)
                colorEdit4("Trail Colour", colTrail)
                inputFloat("head radius", ::headRadius, 1.0f, 10.0f, "%.1f")

                val drawList = windowDrawList
                val p = cursorScreenPos
                if (animate) {
                    dataPosition += progressDir * 0.4f * io.deltaTime
                    if (dataPosition >= 1.1f) {
                        dataPosition = +1.0f
                        progressDir *= -1f
                    }
                    if (dataPosition <= -0.1f) {
                        dataPosition = 0.0f
                        progressDir *= -1f
                    }
                }

                val numPoints = max(1, (ferryPositions.count() * dataPosition).toInt())
                val points = ferryPositions.take(numPoints)
                points.forEachIndexed { index, point ->
                    val pX = p.x + currentWindow.rect().size.x * percentagePlotX * (point.first - minX) / rangeX
                    val pY = p.y + currentWindow.rect().size.y * percentagePlotY * (point.second - minY) / rangeY
                    if (index == points.size - 1) {
                        drawList.apply {
                            addCircleFilled(Vec2(pX, pY), headRadius, getColorU32(colHead))
                        }
                    } else {
                        drawList.apply {
                            addRectFilled(Vec2(pX, pY), Vec2(pX + 1, pY + 1), getColorU32(colTrail))
                        }
                    }
                }

                end()
            }

        }

        ImGui.render()
        glViewport(window.framebufferSize)
        glClearColor(clearColor)
        glClear(GL_COLOR_BUFFER_BIT)

        implGl3.renderDrawData(ImGui.drawData!!)

    }

    fun run() {
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
            val viz = FerryJourney2020Day12(Day12.toPathP1(resourceLines(2020, 12)))
            viz.run()
        }
    }

}