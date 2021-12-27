package advents.seacucumber

import engine.item.GameItem
import net.fish.seacucumber.SeaCucumberFloor
import net.fish.seacucumber.SeaCucumberFloorValue

data class SeaCucumberFloorItemData(
    var gameItem: GameItem,
    override var value: SeaCucumberFloorValue
): SeaCucumberFloor
