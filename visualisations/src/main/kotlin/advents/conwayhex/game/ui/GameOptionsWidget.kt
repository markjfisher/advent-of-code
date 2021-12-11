package advents.conwayhex.game.ui

import advents.ui.GameOptions
import advents.ui.GlobalOptions
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

object GameOptionsWidget {
    operator fun invoke(gameOptions: GameOptions, stateChangeFunction: (KeyCommand) -> Unit) {
        val aliveColour4v = Vec4(gameOptions.aliveColour.x, gameOptions.aliveColour.y, gameOptions.aliveColour.z, gameOptions.aliveColour.w)
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
            if (ImGui.colorEdit4("Alive colour", aliveColour4v)) {
                gameOptions.aliveColour.set(aliveColour4v.x, aliveColour4v.y, aliveColour4v.z, aliveColour4v.w)
            }
            if (ImGui.checkbox("Use Texture", gameOptions::useTexture)) {
                stateChangeFunction(ToggleTexture)
            }
        }
    }
}