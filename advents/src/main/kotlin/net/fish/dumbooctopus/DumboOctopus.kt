package net.fish.dumbooctopus

import net.fish.geometry.grid.GridItemData

interface DumboOctopus: GridItemData {
    var energyLevel: Int
}

data class DumboOctopusSimple(
    override var energyLevel: Int
): DumboOctopus