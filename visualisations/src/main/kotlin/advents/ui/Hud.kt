package advents.ui

import engine.Utils
import engine.Window
import org.lwjgl.glfw.GLFW
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.nanovg.NanoVGGL3
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.DoubleBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

abstract class Hud {
    var vg: Long = 0
    lateinit var colour: NVGColor
    var fontBuffer: ByteBuffer = Utils.ioResourceToByteBuffer("/fonts/UbuntuMono-R.ttf", 150 * 1024)
    lateinit var posx: DoubleBuffer
    lateinit var posy: DoubleBuffer

    fun init(window: Window) {
        vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS or NanoVGGL3.NVG_STENCIL_STROKES)
        if (vg == MemoryUtil.NULL) {
            throw Exception("Could not init nanovg")
        }
        val font = NanoVG.nvgCreateFontMem(vg, FONT_NAME, fontBuffer, false)
        if (font == -1) {
            throw Exception("Could not add font")
        }
        colour = NVGColor.create()
        posx = MemoryUtil.memAllocDouble(1)
        posy = MemoryUtil.memAllocDouble(1)
    }

    abstract fun render(window: Window, data: HudData)

    fun rgba(r: Int, g: Int, b: Int, a: Int, colour: NVGColor): NVGColor {
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
        const val FONT_NAME = "BOLD"
    }
}
