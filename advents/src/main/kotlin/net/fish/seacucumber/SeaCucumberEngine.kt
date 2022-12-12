package net.fish.seacucumber

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.fish.geometry.grid.Grid
import net.fish.geometry.grid.GridItem
import net.fish.geometry.grid.SimpleDataStorage
import net.fish.geometry.square.Square
import net.fish.geometry.square.SquareGridInterface
import net.fish.seacucumber.SeaCucumberFloorValue.E
import net.fish.seacucumber.SeaCucumberFloorValue.EMPTY
import net.fish.seacucumber.SeaCucumberFloorValue.S
import java.lang.Integer.max

data class SeaCucumberEngine<T: SeaCucumberFloor>(
    val grid: Grid,
    val storage: SimpleDataStorage<T>
) {
    fun step(): Int {
        val moveableEast = move(E)
        val moveableSouth = move(S)

        return moveableEast + moveableSouth
    }

    private fun move(direction: SeaCucumberFloorValue): Int {
        // We have to find everything simultaneously, and then move them. can't find and move in ||el as that breaks the simultaneous requirement
        // we aren't using a sparse array, so we can use grid size directly
        val splitSize = grid.height * grid.width / 12
        val blocks = storage.items.chunked(splitSize)
        val moveable = mutableSetOf<Pair<GridItem, GridItem>>()
        runBlocking {
            val defs = blocks.map { gridItems ->
                async { findMoveableAsync(gridItems, direction) }
            }
            defs.awaitAll().map { moveable += it }
        }

        // And move them
        val splitSize2 = max(moveable.size / 12, 1)
        val blocks2 = moveable.chunked(splitSize2)
        runBlocking {
            val defs = blocks2.map { items ->
                async { moveItemsAsync(items, direction) }
            }
            defs.awaitAll()
        }
        return moveable.size
    }


    private suspend fun moveItemsAsync(items: List<Pair<GridItem, GridItem>>, direction: SeaCucumberFloorValue) = withContext(Dispatchers.Default) {
        items.forEach { (old, new) ->
            val floorOld = storage.getData(old) as SeaCucumberFloor
            floorOld.value = EMPTY

            val into = storage.getData(new) as SeaCucumberFloor
            into.value = direction
        }
    }

    private suspend fun findMoveableAsync(items: List<GridItem>, direction: SeaCucumberFloorValue): Set<Pair<GridItem, GridItem>> = withContext(Dispatchers.Default) {
        val moveable = mutableSetOf<Pair<GridItem, GridItem>>()
        items.forEach { item ->
            val sc = storage.getData(item) as SeaCucumberFloor
            if (sc.value == direction) {
                // TODO: Make this work off the grid, not just assume it's a Square
                val neighourIndex = if (direction == E) 0 else 2
                val neighbour = (item as Square).neighbour(neighourIndex)!!

                val neighbourSC = storage.getData(neighbour) as SeaCucumberFloor
                if (neighbourSC.value == EMPTY) {
                    moveable.add(Pair(item, neighbour))
                }
            }
        }
        moveable
    }

    fun gridValues(): List<String> {
        return (0 until grid.height).fold(emptyList()) { acc, y ->
            acc + (0 until grid.width).fold("") { acc2, x ->
                val item = when (grid) {
                    is SquareGridInterface -> grid.square(x, y) as Square
                    else -> throw Exception("can't handle items not in square yet")
                }
                val value = storage.getData(item)!!.value.vis
                "${acc2}${value}"
            }
        }
    }

}