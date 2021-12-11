package advents.ui

import advents.conwayhex.game.ui.GameOptionsWidget
import commands.KeyCommand
import commands.PrintState
import glm_.vec2.Vec2
import imgui.Cond
import imgui.ImGui
import imgui.ImGui.begin
import imgui.ImGui.buttonEx
import imgui.ImGui.checkbox
import imgui.ImGui.columns
import imgui.ImGui.end
import imgui.ImGui.nextColumn
import imgui.ImGui.popItemWidth
import imgui.ImGui.pushItemWidth
import imgui.dsl
import kotlin.reflect.KFunction1

data class GlobalOptions(
    val optionsName: String,
    var gameOptions: GameOptions,
    var surfaceOptions: SurfaceOptions,
    var cameraOptions: CameraOptions,
    var debugOptions: DebugOptions,
    val stateChangeFunction: KFunction1<KeyCommand, Unit>
) {
    fun render(width: Int) {
        ImGui.setNextWindowSize(Vec2(INITIAL_WIDTH, INITIAL_HEIGHT), Cond.FirstUseEver)
        ImGui.setNextWindowPos(Vec2(width - INITIAL_WIDTH - 2, 2), Cond.Appearing)
        if (!begin(optionsName)) {
            end()
            return
        }

        pushItemWidth(-140f)
        GameOptionsWidget(gameOptions, stateChangeFunction)
        CameraOptionsWidget(cameraOptions, stateChangeFunction)
        SurfaceOptionsWidget(surfaceOptions, stateChangeFunction)

        dsl.collapsingHeader("Debug") {
            columns(2, "debug_options", false)
            checkbox("Show Hud", debugOptions::showHud); nextColumn()
            checkbox("Show Messages", debugOptions::showMessage); nextColumn()
            checkbox("Polygon Mode", debugOptions::showPolygons); nextColumn()
            if (buttonEx("Print State", fullSize)) stateChangeFunction(PrintState)
            columns(1)
        }
        popItemWidth()

        end()
    }

    companion object {
        const val INITIAL_WIDTH = 500
        const val INITIAL_HEIGHT = 800
        const val MAX_M_BY_N = 14000
        val fullSize = Vec2(-Float.MIN_VALUE, 0f)
    }
}