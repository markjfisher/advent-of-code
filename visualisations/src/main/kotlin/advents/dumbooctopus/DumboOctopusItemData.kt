package advents.dumbooctopus

import engine.item.GameItem
import net.fish.dumbooctopus.DumboOctopus

data class DumboOctopusItemData(
    var gameItem: GameItem,
    override var energyLevel: Int
): DumboOctopus
