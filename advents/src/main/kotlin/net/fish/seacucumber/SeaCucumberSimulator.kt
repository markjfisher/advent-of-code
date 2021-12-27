package net.fish.seacucumber

import net.fish.geometry.Point
import net.fish.geometry.grid.HashMapBackedGridItemDataStorage
import net.fish.geometry.square.WrappingSquareGrid
import net.fish.seacucumber.SeaCucumberFloorValue.E
import net.fish.seacucumber.SeaCucumberFloorValue.EMPTY
import net.fish.seacucumber.SeaCucumberFloorValue.S
import net.fish.y2021.GridDataUtils

class SeaCucumberSimulator(input: List<String>) {
    private var grid: WrappingSquareGrid
    private var storage: HashMapBackedGridItemDataStorage<SeaCucumberFloorSimple>
    var engine: SeaCucumberEngine<SeaCucumberFloorSimple>

    init {
        val (g, s) = loadInput(input)
        grid = g
        storage = s
        engine = SeaCucumberEngine(grid, storage)
    }

    private fun loadInput(input: List<String>): Pair<WrappingSquareGrid, HashMapBackedGridItemDataStorage<SeaCucumberFloorSimple>> {
        val width = input[0].length
        val height = input.size
        val wrappingSquareGrid = WrappingSquareGrid(width, height)
        val storage = HashMapBackedGridItemDataStorage<SeaCucumberFloorSimple>()
        val mapOfPoints = GridDataUtils.mapCharPointsFromLines(input)
        wrappingSquareGrid.items().forEach { square ->
            val char = mapOfPoints[Point(square.x, square.y)] ?: throw Exception("Could not find data at point $square")
            when(char) {
                '>' -> storage.addItem(square, SeaCucumberFloorSimple(value = E))
                'v' -> storage.addItem(square, SeaCucumberFloorSimple(value = S))
                '.' -> storage.addItem(square, SeaCucumberFloorSimple(value = EMPTY))
                else -> throw Exception("Unknown char in data: $char")
            }
        }

        return Pair(wrappingSquareGrid, storage)
    }

    fun findBlockStep(): Int {
        var stepsToBlock = 1
        while (engine.step() != 0 && stepsToBlock < 100_000) {
            stepsToBlock++
        }
        return stepsToBlock
    }

}
