package advents.conwayhex

import advents.ui.Hud
import advents.ui.HudData
import engine.Window
import org.lwjgl.glfw.GLFW
import org.lwjgl.nanovg.NanoVG
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

class ConwayHud: Hud() {
    override fun render(window: Window, data: HudData) {
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
            GLFW.glfwGetCursorPos(window.windowHandle, posx, posy)
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
            NanoVG.nvgText(vg, 10f, window.height - 87f, "Speed")

            // Doesn't really do anything on hover, but worth knowing
            if (hover) {
                NanoVG.nvgFillColor(vg, rgba(0x00, 0x00, 0x00, 255, colour))
            } else {
                NanoVG.nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour))
            }
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_CENTER or NanoVG.NVG_ALIGN_TOP)
            NanoVG.nvgText(vg, 110f, window.height - 87f, String.format("%02d", data.speed))

            // Iteration
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT or NanoVG.NVG_ALIGN_TOP)
            NanoVG.nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour))
            NanoVG.nvgText(vg, 180f, window.height - 87f, String.format("Iteration: %d", data.iteration))

            // Live count
            NanoVG.nvgText(vg, 450f, window.height - 87f, String.format("Live: %d", data.customData["liveCount"]!!.toInt()))

            NanoVG.nvgText(vg, 620f, window.height - 87f, String.format("On: %d", data.customData["createdCount"]!!.toInt()))
            NanoVG.nvgText(vg, 750f, window.height - 87f, String.format("Off: %d", data.customData["destroyedCount"]!!.toInt()))

        }

        // Render flash text
        if (data.flashMessage != "") {
            NanoVG.nvgFontSize(vg, 200.0f)
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_CENTER or NanoVG.NVG_ALIGN_TOP)
            NanoVG.nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, (255 * cos((100 - data.flashPercentage) * PI / 200f)).toInt(), colour))
            NanoVG.nvgText(vg, window.width / 2f, window.height / 4f, data.flashMessage)
        }

        NanoVG.nvgEndFrame(vg)

        // Restore state
        window.restoreState()

    }
}