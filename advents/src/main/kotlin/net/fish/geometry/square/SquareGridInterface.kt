package net.fish.geometry.square

import net.fish.geometry.grid.Grid

interface SquareGridInterface: Grid {
    fun square(x: Int, y: Int): Square?

}