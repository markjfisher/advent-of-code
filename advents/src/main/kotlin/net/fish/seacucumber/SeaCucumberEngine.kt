package net.fish.seacucumber

import net.fish.geometry.grid.Grid
import net.fish.geometry.grid.GridItem
import net.fish.geometry.grid.HashMapBackedGridItemDataStorage
import net.fish.geometry.square.Square
import net.fish.geometry.square.SquareGrid
import net.fish.seacucumber.SeaCucumberFloorValue.E
import net.fish.seacucumber.SeaCucumberFloorValue.EMPTY
import net.fish.seacucumber.SeaCucumberFloorValue.S

data class SeaCucumberEngine<T: SeaCucumberFloor>(
    val grid: Grid,
    val storage: HashMapBackedGridItemDataStorage<T>
) {
    fun step(): Int {
        // Find all E facing SeaCucumbers that can move, and move them
        val eastFacingThatCanMove = storage.items.filter { gridItem ->
            val sc = storage.getData(gridItem) as SeaCucumberFloor
            if (sc.value == E) {
                val eastNeighbourItem = (gridItem as Square).neighbour(0)!! // East direction, with wrapping
                val eastValue = storage.getData(eastNeighbourItem)!!.value
                eastValue == EMPTY
            } else false
        }
        moveEast(eastFacingThatCanMove)

        // Find all S facing SeaCucumbers that can move, and move them
        val southFacingThatCanMove = storage.items.filter { gridItem ->
            val sc = storage.getData(gridItem) as SeaCucumberFloor
            if (sc.value == S) {
                val southNeighbourItem = (gridItem as Square).neighbour(2)!! // South direction, with wrapping
                val southValue = storage.getData(southNeighbourItem)!!.value
                southValue == EMPTY
            } else false
        }
        moveSouth(southFacingThatCanMove)

        return (eastFacingThatCanMove + southFacingThatCanMove).size
    }

    private fun moveEast(items: List<GridItem>) {
        items.forEach { item ->
            val floorOld = storage.getData(item) as SeaCucumberFloor
            floorOld.value = EMPTY
            val eastNeighbourItem = (item as Square).neighbour(0)!!
            val into = storage.getData(eastNeighbourItem) as SeaCucumberFloor
            into.value = E
        }
    }

    private fun moveSouth(items: List<GridItem>) {
        items.forEach { item ->
            val floorOld = storage.getData(item) as SeaCucumberFloor
            floorOld.value = EMPTY
            val southNeighbourItem = (item as Square).neighbour(2)!!
            val into = storage.getData(southNeighbourItem) as SeaCucumberFloor
            into.value = S
        }
    }

    fun gridValues(): List<String> {
        return (0 until grid.height).fold(emptyList()) { acc, y ->
            acc + (0 until grid.width).fold("") { acc2, x ->
                val item = when (grid) {
                    is SquareGrid -> grid.square(x, y) as Square
                    else -> throw Exception("can't handle items not in square yet")
                }
                val value = storage.getData(item)!!.value.vis
                "${acc2}${value}"
            }
        }
    }

}