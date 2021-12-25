package net.fish.seacucumber

import net.fish.geometry.grid.GridItemData

interface SeaCucumberFloor: GridItemData {
    var value: SeaCucumberFloorValue
}

enum class SeaCucumberFloorValue(val vis: Char) {
    E('>'), S('v'), EMPTY('.');
}

data class SeaCucumberFloorSimple(
    override var value: SeaCucumberFloorValue
): SeaCucumberFloor