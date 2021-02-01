package advents.conwayhex.game

import engine.item.GameItem
import net.fish.geometry.hex.HexData

data class ConwayHexData(
    var gameItem: GameItem,
    var state: ConwayHexState
): HexData

enum class ConwayHexState {
    CREATING, DESTROYING, ALIVE, DEAD
}