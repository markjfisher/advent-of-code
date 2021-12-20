package advents.conwayhex

import engine.item.GameItem
import net.fish.geometry.grid.GridItemData

data class ConwayItemData(
    var gameItem: GameItem,
    var state: ConwayItemState
): GridItemData

enum class ConwayItemState {
    CREATING, DESTROYING, ALIVE, DEAD
}