package advents.dumbooctopus

import engine.item.GameItem
import net.fish.dumbooctopus.DumboOctopus

data class DumboOctopusItemData(
    var gameItem: GameItem,
    override var energyLevel: Int,
    // when to start the flashing cycle of this in the animation frame
    var flashingIteration: Int
): DumboOctopus
