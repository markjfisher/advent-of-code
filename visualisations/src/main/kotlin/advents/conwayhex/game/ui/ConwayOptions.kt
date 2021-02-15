package advents.conwayhex.game.ui

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
import imgui.dsl.collapsingHeader
import net.fish.geometry.projection.SurfaceMapper
import net.fish.geometry.projection.SurfaceOld
import org.joml.Vector4f
import kotlin.reflect.KFunction1

data class ConwayOptions(
    var gameOptions: GameOptions,
    var surfaceOptions: SurfaceOptions,
    var cameraOptions: CameraOptions,
    var debugOptions: DebugOptions,
    val stateChangeFunction: KFunction1<KeyCommand, Unit>
) {
    fun render(width: Int) {
        ImGui.setNextWindowSize(Vec2(INITIAL_WIDTH, INITIAL_HEIGHT), Cond.FirstUseEver)
        ImGui.setNextWindowPos(Vec2(width - INITIAL_WIDTH - 2, 2), Cond.Appearing)
        if (!begin("Conway Options")) {
            end()
            return
        }

        pushItemWidth(-140f)
        GameOptionsWidget(gameOptions, stateChangeFunction)
        CameraOptionsWidget(cameraOptions, stateChangeFunction)
        SurfaceOptionsWidget(surfaceOptions, stateChangeFunction)

        collapsingHeader("Debug") {
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
        const val MAX_M_BY_N = 15600
        val fullSize = Vec2(-Float.MIN_VALUE, 0f)
    }
}

data class GameOptions(
    var pauseGame: Boolean,
    var gameSpeed: Int,
    var useTexture: Boolean,
    var aliveColour: Vector4f
)

data class SurfaceOptions(
    var globalAlpha: Float,
    var animationPercentages: MutableMap<Int, Float>,
    var currentSurfaceName: String,
    var surfaces: MutableMap<String, SurfaceOld>,
    var currentSurfaceMapperName: String,
    var mappers: MutableMap<String, SurfaceMapper>
)

data class DebugOptions(
    var showHud: Boolean,
    var showMessage: Boolean,
    var showPolygons: Boolean
)

data class CameraOptions(
    var cameraFrameNumber: Int,
    var maxCameraFrames: Int,
    var movingCamera: Boolean,
    var loopCamera: Boolean,
    var currentCameraPath: Int,
    val cameraPathNames: List<String>,
    var lookAhead: Int,
    var fov: Float
)
