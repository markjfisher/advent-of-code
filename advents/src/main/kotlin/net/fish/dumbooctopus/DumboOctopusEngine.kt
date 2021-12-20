package net.fish.dumbooctopus

import net.fish.geometry.grid.Grid
import net.fish.geometry.grid.GridItem
import net.fish.geometry.grid.HashMapBackedGridItemDataStorage

data class DumboOctopusEngine<T: DumboOctopus>(
    val grid: Grid,
    val storage: HashMapBackedGridItemDataStorage<T>
) {
    fun step(): Int {
        val flashersThisStep = mutableSetOf<GridItem>()
        val allFlashersThisStep = mutableSetOf<GridItem>()
        grid.items().forEach { square ->
            val dumboOctopus = storage.getData(square)!!
            dumboOctopus.energyLevel++
            if (dumboOctopus.energyLevel >= 10) flashersThisStep.add(square)
        }
        allFlashersThisStep.addAll(flashersThisStep)
        // these flashers now increment all neighbours, which could then flash more, but only once per step, so recursively increment
        flashNeighbours(flashersThisStep, allFlashersThisStep)

        // finally, set any with values >= 10 to 0
        resetFlashers(allFlashersThisStep)

        return allFlashersThisStep.size
    }

    private fun flashNeighbours(flashersToProcess: MutableSet<GridItem>, allFlashersThisStep: MutableSet<GridItem>) {
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

    private fun incrementSquares(squares: List<GridItem>) {
        squares.forEach { square ->
            val dumboOctopus = storage.getData(square) ?: throw Exception("Could not find data for square: $square")
            dumboOctopus.energyLevel++
        }
    }

    private fun resetFlashers(flashersThisStep: MutableSet<GridItem>) {
        flashersThisStep.forEach { square ->
            val dumboOctopus = storage.getData(square)!!
            dumboOctopus.energyLevel = 0
        }
    }

}