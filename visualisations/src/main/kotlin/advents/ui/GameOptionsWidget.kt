package advents.ui

import commands.KeyCommand
import commands.ResetGame
import commands.SingleStep
import commands.ToggleTexture
import glm_.vec4.Vec4
import imgui.ImGui
import imgui.StyleVar
import imgui.dsl.collapsingHeader
import imgui.internal.sections.ButtonFlag
import imgui.internal.sections.ItemFlag
import kotlin.reflect.KFunction

object GameOptionsWidget {
    operator fun invoke(gameOptions: GameOptions, stateChangeFunction: (KeyCommand) -> Unit, uiExtensionFunction: KFunction<Unit>?) {
        collapsingHeader("Game") {
            ImGui.columns(2, "game1", false)
            ImGui.checkbox("Pause", gameOptions::pauseGame); ImGui.nextColumn()
            ImGui.sliderInt("Speed", gameOptions::gameSpeed, 1, 50); ImGui.nextColumn()

            // Single Step - only enabled if in pause mode
            if (!gameOptions.pauseGame) {
                ImGui.pushItemFlag(ItemFlag.Disabled.i, true)
                ImGui.pushStyleVar(StyleVar.Alpha, 0.5f)
            }
            val stepButtonFlag = if (gameOptions.pauseGame) ButtonFlag.None.i else ButtonFlag.Disabled.i
            if (ImGui.buttonEx("Single Step", GlobalOptions.fullSize, stepButtonFlag)) stateChangeFunction(SingleStep)
            ImGui.nextColumn()
            if (!gameOptions.pauseGame) {
                ImGui.popItemFlag()
                ImGui.popStyleVar()
            }

            if (ImGui.button("Reset Game", GlobalOptions.fullSize)) stateChangeFunction(ResetGame)
            ImGui.columns(1)
            if (ImGui.checkbox("Use Texture", gameOptions::useTexture)) {
                stateChangeFunction(ToggleTexture)
            }
            uiExtensionFunction?.call()
        }
    }
}