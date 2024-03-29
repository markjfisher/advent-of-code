package net.fish.y2021

import net.fish.Day
import net.fish.geometry.Point
import net.fish.geometry.grid.GridItemData
import net.fish.geometry.grid.SimpleDataStorage
import net.fish.geometry.square.SquareGrid
import net.fish.geometry.square.Square
import net.fish.resourceLines
import java.util.PriorityQueue
import kotlin.math.abs

object Day15 : Day {
    private val data by lazy { resourceLines(2021, 15) }
    override val warmUps: Int = 1

    override fun part1() = solve(data, 1)
    override fun part2() = solve(data, 5)

    fun solve(data: List<String>, expansions: Int): Int {
        val chitonGrid = createChitonGraph(data, expansions)
        val start = chitonGrid.grid.square(0, 0)!!
        val end = chitonGrid.grid.square(chitonGrid.grid.width - 1, chitonGrid.grid.height - 1)!!
        val (_, cost) = chitonGrid.dijkstraSearch(start, end, false)
        return cost
    }

    fun createChitonGraph(data: List<String>, expansions: Int): ChitonGraph {
        val dataWidth = data[0].length
        val dataHeight = data.size
        val width = data[0].length * expansions
        val height = data.size * expansions
        val squareGrid = SquareGrid(width, height)
        val costMap = GridDataUtils.mapIntPointsFromLines(data)
        val storage = SimpleDataStorage<ChitonDataItem>()
        squareGrid.items().forEach { square ->
            val x = square.x
            val y = square.y
            val cost = if (x == 0 && y == 0) 0 else {
                val relativeX = x % dataWidth
                val relativeY = y % dataHeight

                val adjustedCost = costMap[Point(relativeX, relativeY)]!! + x / dataWidth + y / dataHeight
                if (adjustedCost > 9) ((adjustedCost % 10) + 1) else adjustedCost
            }
            storage.addItem(square, ChitonDataItem(cost))
        }
        return ChitonGraph(storage, squareGrid)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

data class ChitonDataItem(
    var cost: Int
): GridItemData

data class ChitonGraph(
    val storage: SimpleDataStorage<ChitonDataItem>,
    val grid: SquareGrid
) {
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
                val newCost = costSoFar[current]!! + storage.getData(next)!!.cost
                if (newCost < costSoFar.getOrDefault(next, Int.MAX_VALUE)) {
                    costSoFar[next] = newCost
                    frontier.add(RiskSquare(next, newCost))
                    cameFrom[next] = current
                }
            }
        }

        return Pair(if (returnPath) reconstructPath(cameFrom, from, to) else emptyList(), costSoFar.getOrDefault(to, -1))
    }

    fun aStarSearch(from: Square, to: Square, returnPath: Boolean = false): Pair<List<Square>, Int> {
        fun heuristic(a: Square, b: Square): Int {
            return abs(a.x - b.x) + abs(a.y - b.y)
        }

        val frontier = PriorityQueue(costBasedSquareComparator)

        frontier.add(RiskSquare(from, 0))
        val cameFrom = mutableMapOf<Square, Square>()
        val costSoFar = mutableMapOf<Square, Int>()

        costSoFar[from] = 0

        while (frontier.isNotEmpty()) {
            val current = frontier.poll().square
            if (current == to) break

            for (next in current.cardinals()) {
                val newCost = costSoFar[current]!! + storage.getData(next)!!.cost
                if (newCost < costSoFar.getOrDefault(next, Int.MAX_VALUE)) {
                    costSoFar[next] = newCost
                    frontier.add(RiskSquare(next, newCost + heuristic(next, to)))
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