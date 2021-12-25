package net.fish.dumbooctopus

import net.fish.geometry.Point
import net.fish.geometry.grid.HashMapBackedGridItemDataStorage
import net.fish.geometry.square.NonWrappingSquareGrid
import net.fish.y2021.GridDataUtils

class DumboOctopusSimulator(input: List<String>) {
    var grid: NonWrappingSquareGrid
    private var storage: HashMapBackedGridItemDataStorage<DumboOctopusSimple>
    var engine: DumboOctopusEngine<DumboOctopusSimple>

    init {
        val (g, s) = loadInput(input)
        grid = g
        storage = s
        engine = DumboOctopusEngine(g, s)
    }

    private fun loadInput(input: List<String>): Pair<NonWrappingSquareGrid, HashMapBackedGridItemDataStorage<DumboOctopusSimple>> {
        val width = input[0].length
        val height = input.size
        val nonWrappingSquareGrid = NonWrappingSquareGrid(width, height)
        val storage = HashMapBackedGridItemDataStorage<DumboOctopusSimple>()
        val mapOfPoints = GridDataUtils.mapIntPointsFromLines(input)
        nonWrappingSquareGrid.items().forEach { square ->
            val luminescence = mapOfPoints[Point(square.x, square.y)] ?: throw Exception("Couldn't find data for square: $square in $mapOfPoints")
            storage.addItem(square, DumboOctopusSimple(luminescence))
        }
        return Pair(nonWrappingSquareGrid, storage)
    }

    fun doSteps(count: Int): Int {
        return (0 until count).sumOf { engine.step().size }
    }

    fun findSync(): Int {
        var currentStep = 0
        var currentScore = 0
        while (currentScore != 100) {
            currentScore = engine.step().size
            currentStep++
        }
        return currentStep
    }

}
