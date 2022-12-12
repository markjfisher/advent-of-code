package net.fish.geometry

import java.util.*
import net.fish.geometry.grid.GridItemData
import net.fish.geometry.grid.SimpleDataStorage
import net.fish.geometry.square.SquareGrid
import net.fish.geometry.square.Square
import kotlin.Comparator

interface IntStoringDataItem: GridItemData {
    fun value(): Int
}

// WIP to try and bring searching into common class
open class SearchableGraph<T: IntStoringDataItem>(open val storage: SimpleDataStorage<T>, open val grid: SquareGrid) {
    private val costBasedSquareComparator = Comparator<RiskSquare> { riskSquare1, riskSquare2 ->
        when (riskSquare2.risk) {
            riskSquare1.risk -> riskSquare1.square.manhattenDistance().compareTo(riskSquare2.square.manhattenDistance())
            else -> riskSquare1.risk.compareTo(riskSquare2.risk)
        }
    }

    data class RiskSquare(
        val square: Square,
        val risk: Int
    )

    fun dijkstraSearch(from: Square, to: Square, returnPath: Boolean = false): Pair<List<Square>, Int> {
        // implement dijkstra search
        val frontier = PriorityQueue(costBasedSquareComparator)
        frontier.add(RiskSquare(from, 0))
        val cameFrom = mutableMapOf<Square, Square>()
        val costSoFar = mutableMapOf<Square, Int>()

        costSoFar[from] = 0

        while (frontier.isNotEmpty()) {
            val current = frontier.poll().square
            if (current == to) break

            for (next in current.cardinals()) {
                val newCost = costSoFar[current]!! + storage.getData(next)!!.value()
                if (newCost < costSoFar.getOrDefault(next, Int.MAX_VALUE)) {
                    costSoFar[next] = newCost
                    frontier.add(RiskSquare(next, newCost))
                    cameFrom[next] = current
                }
            }
        }

        return Pair(if (returnPath) reconstructPath(cameFrom, from, to) else emptyList(), costSoFar.getOrDefault(to, -1))
    }

    private fun reconstructPath(cameFrom: Map<Square, Square>, from: Square, to: Square): List<Square> {
        var current = to
        val path = mutableListOf<Square>()
        while (current != from) {
            path.add(current)
            current = cameFrom[current]!!
        }
        path.add(from)
        path.reverse()
        return path
    }
}