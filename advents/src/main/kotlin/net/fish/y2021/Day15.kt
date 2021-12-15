package net.fish.y2021

import net.fish.Day
import net.fish.geometry.Point
import net.fish.geometry.grid.GridItemData
import net.fish.geometry.grid.HashMapBackedGridItemDataStorage
import net.fish.geometry.square.NonWrappingSquareGrid
import net.fish.geometry.square.Square
import net.fish.graph.AlgorithmAStar
import net.fish.graph.Graph
import net.fish.resourceLines

object Day15 : Day {
    private val data = resourceLines(2021, 15)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val chitonGrid = createChitonAStar(data)
        val start = chitonGrid.grid.square(0, 0)!!
        val end = chitonGrid.grid.square(chitonGrid.grid.width - 1, chitonGrid.grid.height - 1)!!
        val pathResult = chitonGrid.findPath(start, end)
        println("cost calculated: ${pathResult.second}")
        val cost = pathResult.second.toInt() - chitonGrid.storage.getData(start)!!.cost
        pathResult.first.forEach {
            println("${it.x}, ${it.y}, cost: ${chitonGrid.storage.getData(it)!!.cost}")
        }
        return cost
    }

    fun doPart2(data: List<String>): Int = 0

    data class Route(
        override val a: Square,
        override val b: Square,
        val routeCost: Int
    ) : Graph.Edge<Square> {
        val cost = routeCost
    }

    class ChitonAStar(
        edges: List<Route>,
        val storage: HashMapBackedGridItemDataStorage<ChitonDataItem>,
        val grid: NonWrappingSquareGrid
    ): AlgorithmAStar<Square, Route>(edges) {
        override fun costToMoveThrough(edge: Route): Double {
            return edge.cost.toDouble()
        }

        override fun createEdge(from: Square, to: Square): Route {
            return Route(from, to, storage.getData(to)!!.cost)
        }

    }

    fun createChitonAStar(data: List<String>): ChitonAStar {
        val width = data[0].length
        val height = data.size
        val nonWrappingSquareGrid = NonWrappingSquareGrid(width, height)
        val costMap = GridDataUtils.mapPointsFromLines(data)
        val storage = HashMapBackedGridItemDataStorage<ChitonDataItem>()
        val edges = mutableListOf<Route>()
        nonWrappingSquareGrid.items().forEach { square ->
            val cost = costMap[Point(square.x, square.y)] ?: throw Exception("Couldn't find data for square: $square in $costMap")
            storage.addItem(square, ChitonDataItem(cost))

            val neighbours = square.cardinals()
            neighbours.forEach { neighbour ->
                edges.add(Route(square, neighbour, costMap[Point(neighbour.x, neighbour.y)]!!))
            }
        }
        return ChitonAStar(edges, storage, nonWrappingSquareGrid)
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