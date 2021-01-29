package advents.conwayhex.game.ui

import commands.CreateSurface
import commands.KeyCommand
import commands.LoadCamera
import commands.PrintState
import commands.ResetCamera
import commands.ResetGame
import commands.SetCamera
import commands.SetLookahead
import commands.SingleStep
import glm_.vec2.Vec2
import imgui.Cond
import imgui.Dir
import imgui.ImGui
import imgui.ImGui.begin
import imgui.ImGui.button
import imgui.ImGui.buttonEx
import imgui.ImGui.checkbox
import imgui.ImGui.columns
import imgui.ImGui.dragInt2
import imgui.ImGui.end
import imgui.ImGui.frameHeight
import imgui.ImGui.inputFloat
import imgui.ImGui.inputInt
import imgui.ImGui.nextColumn
import imgui.ImGui.popButtonRepeat
import imgui.ImGui.popItemFlag
import imgui.ImGui.popItemWidth
import imgui.ImGui.popStyleVar
import imgui.ImGui.pushButtonRepeat
import imgui.ImGui.pushItemFlag
import imgui.ImGui.pushItemWidth
import imgui.ImGui.pushStyleVar
import imgui.ImGui.sameLine
import imgui.ImGui.selectable
import imgui.ImGui.separator
import imgui.ImGui.sliderFloat
import imgui.ImGui.sliderInt
import imgui.StyleVar
import imgui.dsl.radioButton
import imgui.dsl.treeNode
import imgui.internal.sections.ButtonFlag
import imgui.internal.sections.ItemFlag
import net.fish.geometry.hex.Orientation.ORIENTATION.FLAT
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import net.fish.geometry.hex.projection.DecoratedKnotSurface
import net.fish.geometry.hex.projection.DecoratedKnotType.Type10b
import net.fish.geometry.hex.projection.DecoratedKnotType.Type11c
import net.fish.geometry.hex.projection.DecoratedKnotType.Type4b
import net.fish.geometry.hex.projection.DecoratedKnotType.Type7a
import net.fish.geometry.hex.projection.DecoratedKnotType.Type7b
import net.fish.geometry.hex.projection.EpitrochoidSurface
import net.fish.geometry.hex.projection.SimpleTorusSurface
import net.fish.geometry.hex.projection.Surface
import net.fish.geometry.hex.projection.ThreeFactorParametricSurface
import net.fish.geometry.hex.projection.TorusKnotSurface
import kotlin.reflect.KFunction1

data class ConwayOptions(
    var gameSpeed: Int,
    var showHud: Boolean,
    var showMessage: Boolean,
    var pauseGame: Boolean,
    var showPolygons: Boolean,
    var globalAlpha: Float,
    var currentSurfaceName: String,
    var surfaces: MutableMap<String, Surface>,
    var cameraOptions: CameraOptions,
    val stateChangeFunction: KFunction1<KeyCommand, Unit>
) {
    private val fullSize = Vec2(-Float.MIN_VALUE, 0f)

    fun render(width: Int) {
        val currentSurface = surfaces[currentSurfaceName]!!
        val gridSize4i = intArrayOf(currentSurface.gridWidth, currentSurface.gridHeight, 2000, MAX_M_BY_N / currentSurface.gridHeight.coerceAtLeast(1))

        ImGui.setNextWindowSize(Vec2(INITIAL_WIDTH, INITIAL_HEIGHT), Cond.FirstUseEver)
        ImGui.setNextWindowPos(Vec2(width - INITIAL_WIDTH - 2, 2), Cond.Appearing)
        if (!begin("Conway Options")) {
            end()
            return
        }

        pushItemWidth(-140f)
        separator()
        treeNode("Game") {
            columns(2, "game1", false)
            checkbox("Pause", ::pauseGame); nextColumn()
            sliderInt("Speed", ::gameSpeed, 1, 50); nextColumn()

            // Single Step - only enabled if in pause mode
            if (!pauseGame) {
                pushItemFlag(ItemFlag.Disabled.i, true)
                pushStyleVar(StyleVar.Alpha, 0.5f)
            }
            val stepButtonFlag = if (pauseGame) ButtonFlag.None.i else ButtonFlag.Disabled.i
            if (buttonEx("Single Step", fullSize, stepButtonFlag)) stateChangeFunction(SingleStep)
            nextColumn()
            if (!pauseGame) {
                popItemFlag()
                popStyleVar()
            }

            if (button("Reset Game", fullSize)) stateChangeFunction(ResetGame)
            columns(1)
        }
        separator()
        treeNode("Camera") {
            columns(3, "camera1", false)
            checkbox("Animate", cameraOptions::movingCamera); nextColumn()
            checkbox("Loop", cameraOptions::loopCamera); nextColumn()
            if (buttonEx("Reset", fullSize)) stateChangeFunction(ResetCamera)
            columns(1)

            val toShow = cameraOptions.maxCameraFrames.coerceAtLeast(100)
            if (sliderInt("Position", cameraOptions::cameraFrameNumber, 1, toShow) && cameraOptions.maxCameraFrames != -1) {
                stateChangeFunction(SetCamera)
            }

            if (cameraOptions.maxCameraFrames == -1) {
                pushItemFlag(ItemFlag.Disabled.i, true)
                pushStyleVar(StyleVar.Alpha, 0.5f)
            }
            val spacing = ImGui.style.itemInnerSpacing.x
            sameLine()
            pushButtonRepeat(true)
            val arrowFlag = if (cameraOptions.maxCameraFrames == -1) ButtonFlag.Disabled.i else ButtonFlag.None.i
            if (ImGui.arrowButtonEx("##left", Dir.Left, Vec2(frameHeight), arrowFlag)) {
                cameraOptions.cameraFrameNumber = (cameraOptions.cameraFrameNumber - 1)
                if (cameraOptions.cameraFrameNumber == 0) cameraOptions.cameraFrameNumber = cameraOptions.maxCameraFrames
                stateChangeFunction(SetCamera)
            }
            sameLine(0f, spacing)
            if (ImGui.arrowButtonEx("##right", Dir.Right, Vec2(frameHeight), arrowFlag)) {
                cameraOptions.cameraFrameNumber = cameraOptions.cameraFrameNumber + 1
                if (cameraOptions.cameraFrameNumber == cameraOptions.maxCameraFrames + 1) cameraOptions.cameraFrameNumber = 1
                stateChangeFunction(SetCamera)
            }
            popButtonRepeat()
            if (cameraOptions.maxCameraFrames == -1) {
                popItemFlag()
                popStyleVar()
            }


            if (sliderInt("Look ahead", cameraOptions::lookAhead, 1, 150)) {
                stateChangeFunction(SetLookahead)
            }

            treeNode("Camera Path:") {
                (cameraOptions.cameraPathNames.indices).forEach { i ->
                    if (selectable(String.format("%1d. %s", i + 1, cameraOptions.cameraPathNames[i]), cameraOptions.currentCameraPath == i)) {
                        cameraOptions.currentCameraPath = i
                        stateChangeFunction(LoadCamera)
                    }
                }
            }
        }
        separator()
        treeNode("Surface") {
            sliderFloat("Global Alpha", ::globalAlpha, 0f, 1f)

            if (dragInt2("Grid Size", gridSize4i, 2f, 2, 2000)) {
                var newWidth = gridSize4i[0].coerceAtLeast(2)
                if (newWidth % 2 == 1) newWidth--
                var maxV = MAX_M_BY_N / newWidth
                if (maxV % 2 == 1) maxV--
                currentSurface.gridWidth = newWidth
                currentSurface.gridHeight = gridSize4i[1].coerceAtMost(maxV).coerceAtLeast(2)
            }
            radioButton("Pointy", currentSurface.gridOrientation == POINTY) {
                currentSurface.gridOrientation = POINTY
            }
            sameLine()
            radioButton("Flat", currentSurface.gridOrientation == FLAT) {
                currentSurface.gridOrientation = FLAT
            }
            surfaces.keys.sorted().forEachIndexed { i, surfaceName ->
                if (selectable(String.format("%1d. %s", i + 1, surfaceName), currentSurfaceName == surfaceName)) {
                    currentSurfaceName = surfaceName
                }
            }
            separator()
            // for the given surface type, show its controls
            when (currentSurface) {
                is SimpleTorusSurface -> {
                    sliderFloat("Major Radius", currentSurface::majorRadius, 0.0001f, 20f)
                }
                is DecoratedKnotSurface -> {
                    radioButton("4b", currentSurface.type == Type4b) { currentSurface.type = Type4b }
                    sameLine()
                    radioButton("7a", currentSurface.type == Type7a) { currentSurface.type = Type7a }
                    sameLine()
                    radioButton("7b", currentSurface.type == Type7b) { currentSurface.type = Type7b }
                    sameLine()
                    radioButton("10b", currentSurface.type == Type10b) { currentSurface.type = Type10b }
                    sameLine()
                    radioButton("11c", currentSurface.type == Type11c) { currentSurface.type = Type11c }
                }
                is TorusKnotSurface -> {
                    sliderInt("p", currentSurface::p, 1, 20)
                    sliderInt("q", currentSurface::q, 1, 20)

                    sliderFloat("a", currentSurface::a, 0.1f, 2f)
                    sliderFloat("b", currentSurface::b, 0.1f, 2f)
                }
                is EpitrochoidSurface -> {
                    inputFloat("a", currentSurface::a, 0.1f, 10f, "%.2f")
                    inputFloat("b", currentSurface::b, 0.1f, 10f, "%.2f")
                    inputFloat("c", currentSurface::c, 0.1f, 10f, "%.2f")
                }
                is ThreeFactorParametricSurface -> {
                    inputInt("a", currentSurface::a, 0, 20)
                    inputInt("b", currentSurface::b, 0, 20)
                    inputInt("c", currentSurface::c, 0, 20)
                }
                else -> {
                    // todo
                }
            }
            sliderFloat("Sweep Radius", currentSurface::r, 0.1f, 5f)
            sliderFloat("Scale", currentSurface::scale, 0.2f, 10f)
            separator()
            if (button("Create Surface", fullSize)) stateChangeFunction(CreateSurface)
        }
        separator()
        treeNode("Debug") {
            columns(2, "debug_options", false)
            checkbox("Show Hud", ::showHud); nextColumn()
            checkbox("Show Messages", ::showMessage); nextColumn()
            checkbox("Polygon Mode", ::showPolygons); nextColumn()
            if (buttonEx("Print State", fullSize)) stateChangeFunction(PrintState)
            columns(1)
        }
        popItemWidth()

        end()
    }

    companion object {
        const val INITIAL_WIDTH = 420
        const val INITIAL_HEIGHT = 600
        const val MAX_M_BY_N = 15600
    }
}

data class CameraOptions(
    var cameraFrameNumber: Int,
    var maxCameraFrames: Int,
    var movingCamera: Boolean,
    var loopCamera: Boolean,
    var currentCameraPath: Int,
    val cameraPathNames: List<String>,
    var lookAhead: Int
)
