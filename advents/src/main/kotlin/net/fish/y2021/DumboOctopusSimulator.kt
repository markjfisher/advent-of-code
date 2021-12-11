package net.fish.y2021

import net.fish.geometry.Point
import net.fish.geometry.grid.GridItemData
import net.fish.geometry.grid.HashMapBackedGridItemDataStorage
import net.fish.geometry.square.NonWrappingSquareGrid
import net.fish.geometry.square.Square

class DumboOctopusSimulator(input: List<String>) {
    var grid: NonWrappingSquareGrid
    var storage: HashMapBackedGridItemDataStorage<DumboOctopus>

    init {
        val (g, s) = loadInput(input)
        grid = g
        storage = s
    }

    private fun loadInput(input: List<String>): Pair<NonWrappingSquareGrid, HashMapBackedGridItemDataStorage<DumboOctopus>> {
        val width = input[0].length
        val height = input.size
        val nonWrappingSquareGrid = NonWrappingSquareGrid(width, height)
        val storage = HashMapBackedGridItemDataStorage<DumboOctopus>()
        val mapOfPoints = GridDataUtils.mapPointsFromLines(input)
        nonWrappingSquareGrid.items().forEach { square ->
            val luminescence = mapOfPoints[Point(square.x, square.y)] ?: throw Exception("Couldn't find data for square: $square in $mapOfPoints")
            storage.addItem(square, DumboOctopus(luminescence))
        }
        return Pair(nonWrappingSquareGrid, storage)
    }

    fun doSteps(count: Int): Long {
        return (0 until count).fold(0L) { score, _ -> score + step() }
    }

    fun findSync(): Long {
        var currentStep = 0L
        var currentScore = 0
        while (currentScore != 100) {
            currentScore = step()
            currentStep++
        }
        return currentStep
    }

    fun step(): Int {
        val flashersThisStep = mutableSetOf<Square>()
        val allFlashersThisStep = mutableSetOf<Square>()
        grid.items().forEach { square ->
            val dumboOctopus = storage.getData(square)!!
            dumboOctopus.energyLevel++
            if (dumboOctopus.energyLevel >= 10) flashersThisStep += square
        }
        allFlashersThisStep.addAll(flashersThisStep)
        // these flashers now increment all neighbours, which could then flash more, but only once per step, so recursively increment
        flashNeighbours(flashersThisStep, allFlashersThisStep)

        // finally, set any with values >= 10 to 0
        resetFlashers(allFlashersThisStep)

        return allFlashersThisStep.size
    }

    private fun flashNeighbours(flashersToProcess: MutableSet<Square>, allFlashersThisStep: MutableSet<Square>) {
        if (flashersToProcess.isEmpty()) return

        // get a flasher to process
        val flasher = flashersToProcess.first()
        flashersToProcess.remove(flasher)

        // find its neighbours, and increment them
        val neighbours = flasher.neighbours()
        incrementSquares(neighbours)

        // find any that are now flashing because of this. it will be any squares with energy level above 9 that are not already in the flashing this step list
        val newFlashers = grid.items().filter { storage.getData(it)!!.energyLevel >= 10 } - allFlashersThisStep
        flashersToProcess.addAll(newFlashers)
        allFlashersThisStep.addAll(newFlashers)

        // now recurse overy any left to process
        return flashNeighbours(flashersToProcess, allFlashersThisStep)
    }

    private fun incrementSquares(squares: List<Square>) {
        squares.forEach { square ->
            val dumboOctopus = storage.getData(square) ?: throw Exception("Could not find data for square: $square")
            dumboOctopus.energyLevel++
        }
    }

    private fun resetFlashers(flashersThisStep: MutableSet<Square>) {
        flashersThisStep.forEach { square ->
            val dumboOctopus = storage.getData(square)!!
            dumboOctopus.energyLevel = 0
        }
    }

    // convenience function for turning grid into list of strings of the values to make it easy to test iterations
    // e.g.
    // 5483143223
    // ...
    // 5283751526
    fun gridValues(): List<String> {
        return (0 until grid.height).fold(emptyList()) { acc, y ->
            acc + (0 until grid.width).fold("") { acc2, x ->
                val square = grid.square(x, y)!!
                val level = storage.getData(square)!!.energyLevel
                "${acc2}${level}"
            }
        }
    }
}

data class DumboOctopus(
    var energyLevel: Int
): GridItemData
