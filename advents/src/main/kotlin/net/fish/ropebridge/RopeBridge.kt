package net.fish.ropebridge

import net.fish.geometry.Point
import net.fish.geometry.grid.GridItemData

interface RopeBridge: GridItemData {
    var knots: MutableList<Point>
}

data class RopeBridgeSimple(
    override var knots: MutableList<Point>
): RopeBridge