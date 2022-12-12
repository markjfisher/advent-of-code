package net.fish.ropebridge

import net.fish.geometry.grid.SimpleDataStorage
import net.fish.geometry.square.WrappingSquareGrid

class RopeBridgeSimulator(input: List<String>) {
    var grid: WrappingSquareGrid
    private var storage: SimpleDataStorage<RopeBridgeSimple>

    init {
        val (g, s) = loadInput(input)
        grid = g
        storage = s
    }

    private fun loadInput(input: List<String>): Pair<WrappingSquareGrid, SimpleDataStorage<RopeBridgeSimple>> {
        // The original input data spans a space of (-25,-44) to (377, 222), so let's give it a surface that it'll wrap around for visual effect
        val width = 150
        val height = 150
        val wrappingSquareGrid = WrappingSquareGrid(width, height)
        val storage = SimpleDataStorage<RopeBridgeSimple>()
        // all points start at (0,0) for the number of knots
        TODO()
    }
}