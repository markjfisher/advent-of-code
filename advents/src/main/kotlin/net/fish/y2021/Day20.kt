package net.fish.y2021

import net.fish.Day
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.resourceLines

object Day20 : Day {
    private val data by lazy { resourceLines(2021, 20) }

    override fun part1() = solve(data, 2)
    override fun part2() = solve(data, 50)

    fun solve(data: List<String>, iterations: Int): Int {
        val trench = TrenchMap.parseInput(data)
        val e2 = trench.evolve(iterations)
        return e2.imageMap.count()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

    data class TrenchMap(
        val algorithm: String,
        val imageMap: Set<Point>,
        val shouldConsiderInfinite: Boolean = false
    ) {
        fun evolve(iterations: Int): TrenchMap {
            var isInfinite = shouldConsiderInfinite
            val bit0 = if(algorithm[0] == '#') "1" else "0"
            val data = (0 until iterations).foldIndexed(this.imageMap) { i, map, _ ->
                val newImageMap = mutableSetOf<Point>()
                val bounds = map.bounds()
                for (y in bounds.first.y - 1..bounds.second.y + 1) {
                    for (x in bounds.first.x - 1..bounds.second.x + 1) {
                        val p = Point(x, y)
                        val pv = pointValue(p, bit0, map, bounds, isInfinite)
                        if (algorithm[pv] == '#') newImageMap.add(p)
                    }
                }
                isInfinite = !isInfinite
                newImageMap
            }
            return TrenchMap(algorithm, data, isInfinite)
        }


        fun stringGrid(): String {
            val bounds = imageMap.bounds()
            var outString = ""
            for (y in bounds.first.y - 1..bounds.second.y + 1) {
                for (x in bounds.first.x - 1..bounds.second.x + 1) {
                    outString += if (imageMap.contains(Point(x, y))) "#" else "."
                }
                outString += "\n"
            }
            return outString
        }

        companion object {
            private val surroundingPointsDeltas = listOf(
                Point(-1, -1), Point(0, -1), Point(1, -1),
                Point(-1, 0), Point(0, 0), Point(1, 0),
                Point(-1, 1), Point(0, 1), Point(1, 1)
            )

            fun parseInput(data: List<String>): TrenchMap {
                val algorithm = data[0]

                val imageMap = mutableSetOf<Point>()
                for (y in 2 until data.size) {
                    val line = data[y]
                    line.forEachIndexed { x, c -> if (c == '#') imageMap.add(Point(x, y - 2)) }
                }

                return TrenchMap(algorithm, imageMap)
            }

            fun pointValue(point: Point, bit0: String, imageMap: Set<Point>, bounds: Pair<Point, Point>, shouldConsiderInfinite: Boolean): Int {
                return Integer.parseInt(surroundingPointsDeltas.joinToString("") {
                    val neighbour = it + point
                    val bit = when {
                        !neighbour.within(bounds) -> {
                            if (shouldConsiderInfinite) bit0 else "0"
                        }
                        else -> {
                            if (imageMap.contains(neighbour)) "1" else "0"
                        }
                    }
                    bit
                }, 2)
            }

        }
    }

}