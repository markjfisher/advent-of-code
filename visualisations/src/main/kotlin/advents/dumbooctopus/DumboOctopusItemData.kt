package advents.dumbooctopus

import engine.item.GameItem
import net.fish.geometry.grid.GridItemData

data class DumboOctopusItemData(
    var gameItem: GameItem,
    var energyLevel: Int
): GridItemData
