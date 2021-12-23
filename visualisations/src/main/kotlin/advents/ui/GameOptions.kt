package advents.ui

import org.joml.Vector4f

data class GameOptions(
    var pauseGame: Boolean,
    var gameSpeed: Int,
    var maxGameSpeed: Int = 50,
    var useTexture: Boolean,
    var gameSpecificData: MutableMap<String, Any> = mutableMapOf()
) {
    fun setGSData(key: String, value: Any) {
        gameSpecificData[key] = value
    }

    // Create getter functions for type convenience. Hideous but works
    fun getVector4f(key: String): Vector4f? {
        val v = gameSpecificData[key]
        return if (v == null) null else (v as Vector4f)
    }
}

