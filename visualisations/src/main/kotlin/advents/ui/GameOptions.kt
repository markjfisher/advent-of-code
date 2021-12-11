package advents.ui

import org.joml.Vector4f

data class GameOptions(
    var pauseGame: Boolean,
    var gameSpeed: Int,
    var useTexture: Boolean,
    var aliveColour: Vector4f
)

