package advents.ui

import advents.ui.GlobalOptions.Companion.fullSize
import commands.KeyCommand
import commands.LoadCamera
import commands.ResetCamera
import commands.SetCamera
import commands.SetLookahead
import glm_.vec2.Vec2
import imgui.Dir
import imgui.ImGui
import imgui.StyleVar
import imgui.dsl
import imgui.dsl.collapsingHeader
import imgui.internal.sections.ButtonFlag
import imgui.internal.sections.ItemFlag
import kotlin.math.PI

object CameraOptionsWidget {
    operator fun invoke(cameraOptions: CameraOptions, stateChangeFunction: (KeyCommand) -> Unit) {
        collapsingHeader("Camera") {
            ImGui.columns(3, "camera1", false)
            ImGui.checkbox("Animate", cameraOptions::movingCamera); ImGui.nextColumn()
            ImGui.checkbox("Loop", cameraOptions::loopCamera); ImGui.nextColumn()
            if (ImGui.buttonEx("Reset", fullSize)) stateChangeFunction(ResetCamera)
            ImGui.columns(1)

            val toShow = cameraOptions.maxCameraFrames.coerceAtLeast(100)
            if (ImGui.sliderInt("Position", cameraOptions::cameraFrameNumber, 1, toShow) && cameraOptions.maxCameraFrames != -1) {
                stateChangeFunction(SetCamera)
            }

            if (cameraOptions.maxCameraFrames == -1) {
                ImGui.pushItemFlag(ItemFlag.Disabled.i, true)
                ImGui.pushStyleVar(StyleVar.Alpha, 0.5f)
            }
            val spacing = ImGui.style.itemInnerSpacing.x
            ImGui.sameLine()
            ImGui.pushButtonRepeat(true)
            val arrowFlag = if (cameraOptions.maxCameraFrames == -1) ButtonFlag.Disabled.i else ButtonFlag.None.i
            if (ImGui.arrowButtonEx("##left", Dir.Left, Vec2(ImGui.frameHeight), arrowFlag)) {
                cameraOptions.cameraFrameNumber = (cameraOptions.cameraFrameNumber - 1)
                if (cameraOptions.cameraFrameNumber == 0) cameraOptions.cameraFrameNumber = cameraOptions.maxCameraFrames
                stateChangeFunction(SetCamera)
            }
            ImGui.sameLine(0f, spacing)
            if (ImGui.arrowButtonEx("##right", Dir.Right, Vec2(ImGui.frameHeight), arrowFlag)) {
                cameraOptions.cameraFrameNumber = cameraOptions.cameraFrameNumber + 1
                if (cameraOptions.cameraFrameNumber == cameraOptions.maxCameraFrames + 1) cameraOptions.cameraFrameNumber = 1
                stateChangeFunction(SetCamera)
            }
            ImGui.popButtonRepeat()
            if (cameraOptions.maxCameraFrames == -1) {
                ImGui.popItemFlag()
                ImGui.popStyleVar()
            }


            if (ImGui.sliderInt("Look ahead", cameraOptions::lookAhead, 1, 150)) {
                stateChangeFunction(SetLookahead)
            }

            ImGui.sliderFloat("fov", cameraOptions::fov, 0.01f, (PI - 0.01).toFloat())

            dsl.treeNode("Camera Path:") {
                (cameraOptions.cameraPathNames.indices).forEach { i ->
                    if (ImGui.selectable(String.format("%1d. %s", i + 1, cameraOptions.cameraPathNames[i]), cameraOptions.currentCameraPath == i)) {
                        cameraOptions.currentCameraPath = i
                        stateChangeFunction(LoadCamera)
                    }
                }
            }
        }
    }
}