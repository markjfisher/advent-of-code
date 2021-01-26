package advents.conwayhex.game.ui

import commands.KeyCommand
import commands.LoadCamera
import commands.PrintState
import commands.ResetCamera
import commands.ResetGame
import commands.SetCamera
import commands.SingleStep
import glm_.vec2.Vec2
import imgui.Cond
import imgui.ImGui
import imgui.ImGui.begin
import imgui.ImGui.button
import imgui.ImGui.buttonEx
import imgui.ImGui.checkbox
import imgui.ImGui.columns
import imgui.ImGui.end
import imgui.ImGui.nextColumn
import imgui.ImGui.popItemFlag
import imgui.ImGui.popItemWidth
import imgui.ImGui.popStyleVar
import imgui.ImGui.pushItemFlag
import imgui.ImGui.pushItemWidth
import imgui.ImGui.pushStyleVar
import imgui.ImGui.selectable
import imgui.ImGui.sliderFloat
import imgui.ImGui.sliderInt
import imgui.StyleVar
import imgui.dsl.treeNode
import imgui.internal.sections.ButtonFlag
import imgui.internal.sections.ItemFlag
import kotlin.reflect.KFunction1

data class ConwayOptions(
    var gameSpeed: Int,
    var showHud: Boolean,
    var showMessage: Boolean,
    var pauseGame: Boolean,
    var showPolygons: Boolean,
    var cameraFrameNumber: Int,
    var maxCameraFrames: Int,
    var movingCamera: Boolean,
    var loopCamera: Boolean,
    val cameraPathNames: List<String>,
    var currentCameraPath: Int,
    var globalAlpha: Float,
    val stateChangeFunction: KFunction1<KeyCommand, Unit>
) {
    private val fullSize = Vec2(-Float.MIN_VALUE, 0f)

    fun render(width: Int) {
        ImGui.setNextWindowSize(Vec2(INITIAL_WIDTH, INITIAL_HEIGHT), Cond.FirstUseEver)
        ImGui.setNextWindowPos(Vec2(width - INITIAL_WIDTH - 2, 2), Cond.Appearing)
        if (!begin("Conway Options")) {
            end()
            return
        }

        pushItemWidth(-140f)
        treeNode("Game") {
            columns(2, "game1", false)
            checkbox("Pause", ::pauseGame); nextColumn()
            sliderInt("Speed", ::gameSpeed, 1, 50); nextColumn()

            // Single Step - only enabled if in pause mode
            if (!pauseGame) {
                pushItemFlag(ItemFlag.Disabled.i, true)
                pushStyleVar(StyleVar.Alpha, 0.5f)
            }
            val stepButtonFlag = if(pauseGame) ButtonFlag.None.i else ButtonFlag.Disabled.i
            if (buttonEx("Single Step", fullSize, stepButtonFlag)) stateChangeFunction(SingleStep)
            nextColumn()
            if (!pauseGame) {
                popItemFlag()
                popStyleVar()
            }

            if (button("Reset Game", fullSize)) stateChangeFunction(ResetGame)
            columns(1)
        }
        treeNode("Camera") {
            columns(3, "camera1", false)
            checkbox("Animate", ::movingCamera); nextColumn()
            checkbox("Loop", ::loopCamera); nextColumn()
            if (buttonEx("Reset", fullSize)) stateChangeFunction(ResetCamera)
            columns(1)

            val toShow = maxCameraFrames.coerceAtLeast(100)
            if (sliderInt("Camera Pos", ::cameraFrameNumber, 1, toShow) && maxCameraFrames != -1) {
                stateChangeFunction(SetCamera)
            }

            treeNode("Camera Path:") {
                (cameraPathNames.indices).forEach { i ->
                    if (selectable(String.format("%1d. %s", i + 1, cameraPathNames[i]), currentCameraPath == i)) {
                        currentCameraPath = i
                        stateChangeFunction(LoadCamera)
                    }
                }
            }
        }
        treeNode("Debug") {
            columns(2, "debug_options", false)
            checkbox("Show Hud", ::showHud); nextColumn()
            checkbox("Show Messages", ::showMessage); nextColumn()
            checkbox("Polygon Mode", ::showPolygons); nextColumn()
            if (buttonEx("Print State", fullSize)) stateChangeFunction(PrintState)
            columns(1)
        }
        treeNode("Surface") {
            sliderFloat("Global Alpha", ::globalAlpha, 0f, 1f)
        }

        popItemWidth()

        end()
    }

    companion object {
        const val INITIAL_WIDTH = 420
        const val INITIAL_HEIGHT = 350
    }
}