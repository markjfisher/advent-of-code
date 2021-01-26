package advents.conwayhex.game

import engine.Utils
import engine.Window
import org.lwjgl.glfw.GLFW.glfwGetCursorPos
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.nanovg.NanoVG.nvgText
import org.lwjgl.nanovg.NanoVGGL3
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.DoubleBuffer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

class Hud {
    private var vg: Long = 0
    private lateinit var colour: NVGColor
    private var fontBuffer: ByteBuffer = Utils.ioResourceToByteBuffer("/fonts/UbuntuMono-R.ttf", 150 * 1024)
    private val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
    private lateinit var posx: DoubleBuffer
    private lateinit var posy: DoubleBuffer

    fun init(window: Window) {
        vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS or NanoVGGL3.NVG_STENCIL_STROKES)
        if (vg == MemoryUtil.NULL) {
            throw Exception("Could not init nanovg")
        }
        val font = NanoVG.nvgCreateFontMem(vg, FONT_NAME, fontBuffer, 0)
        if (font == -1) {
            throw Exception("Could not add font")
        }
        colour = NVGColor.create()
        posx = MemoryUtil.memAllocDouble(1)
        posy = MemoryUtil.memAllocDouble(1)
    }

    fun render(window: Window, data: HudData) {
        NanoVG.nvgBeginFrame(vg, window.width.toFloat(), window.height.toFloat(), 1f)

        if (data.showBar) {
            // Upper ribbon
            when {
                data.isPaused -> rgba(0xf1, 0x61, 0x23, 200, colour)
                else -> rgba(0x23, 0xa1, 0xf1, 200, colour)
            }
            NanoVG.nvgBeginPath(vg)
            NanoVG.nvgRect(vg, 0f, window.height.toFloat() - 100f, window.width.toFloat(), 50f)
            NanoVG.nvgFillColor(vg, colour)
            NanoVG.nvgFill(vg)

            // Lower ribbon
            when {
                data.isPaused -> rgba(0xf9, 0xc1, 0xa3, 200, colour)
                else -> rgba(0xc1, 0xe3, 0xf9, 200, colour)
            }
            NanoVG.nvgBeginPath(vg)
            NanoVG.nvgRect(vg, 0f, window.height.toFloat() - 50f, window.width.toFloat(), 10f)
            NanoVG.nvgFillColor(vg, colour)
            NanoVG.nvgFill(vg)

            // calculate the mouse position and if it's in a small circle for hover detection. cute.
            glfwGetCursorPos(window.windowHandle, posx, posy)
            val xcenter = 110
            val ycenter: Int = window.height - 75
            val radius = 20
            val x = posx[0].toInt()
            val y = posy[0].toInt()
            val hover = (x - xcenter).toDouble().pow(2.0) + (y - ycenter).toDouble().pow(2.0) < radius.toDouble().pow(2.0)

            // Circle for speed
            NanoVG.nvgBeginPath(vg)
            NanoVG.nvgCircle(vg, xcenter.toFloat(), ycenter.toFloat(), radius.toFloat())
            NanoVG.nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour))
            NanoVG.nvgFill(vg)

            // Speed Text
            NanoVG.nvgFontSize(vg, 25.0f)
            NanoVG.nvgFontFace(vg, FONT_NAME)
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT or NanoVG.NVG_ALIGN_TOP)
            nvgText(vg, 10f, window.height - 87f, "Speed")

            // Doesn't really do anything on hover, but worth knowing
            if (hover) {
                NanoVG.nvgFillColor(vg, rgba(0x00, 0x00, 0x00, 255, colour))
            } else {
                NanoVG.nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour))
            }
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_CENTER or NanoVG.NVG_ALIGN_TOP)
            nvgText(vg, 110f, window.height - 87f, String.format("%02d", data.speed))

            // Iteration
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT or NanoVG.NVG_ALIGN_TOP)
            NanoVG.nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour))
            nvgText(vg, 180f, window.height - 87f, String.format("Iteration: %d", data.iteration))

            // Live count
            nvgText(vg, 450f, window.height - 87f, String.format("Live: %d", data.liveCount))

            nvgText(vg, 620f, window.height - 87f, String.format("On: %d", data.createdCount))
            nvgText(vg, 750f, window.height - 87f, String.format("Off: %d", data.destroyedCount))

        }

        // Render flash text
        if (data.flashMessage != "") {
            NanoVG.nvgFontSize(vg, 200.0f)
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_CENTER or NanoVG.NVG_ALIGN_TOP)
            NanoVG.nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, (255 * cos((100 - data.flashPercentage) * PI / 200f)).toInt(), colour))
            nvgText(vg, window.width / 2f, window.height / 4f, data.flashMessage)
        }

        NanoVG.nvgEndFrame(vg)

        // Restore state
        window.restoreState()
    }

    private fun rgba(r: Int, g: Int, b: Int, a: Int, colour: NVGColor): NVGColor {
        colour.r(r / 255.0f)
        colour.g(g / 255.0f)
        colour.b(b / 255.0f)
        colour.a(a / 255.0f)
        return colour
    }

    fun cleanup() {
        NanoVGGL3.nvgDelete(vg)
        MemoryUtil.memFree(posx)
        MemoryUtil.memFree(posy)
    }

    companion object {
        private const val FONT_NAME = "BOLD"
    }
}
