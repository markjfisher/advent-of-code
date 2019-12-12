package net.fish

import kotlin.math.abs

class Day3A : Day3Base() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val wirePaths = Helpers.loadWireData("/day3-input.txt")
            val md: Int = Day3A().calculateManhattanDistance(wirePaths)
            println(md) // 232
        }
    }

    fun calculateManhattanDistance(paths: Pair<List<String>, List<String>>): Int {
        val wire1Points: List<Pair<Int, Int>> = convertWirePathsToCoordinates(paths.first)
        val wire2Points: List<Pair<Int, Int>> = convertWirePathsToCoordinates(paths.second)
        return findIntersections(wire1Points, wire2Points).map { abs(it.first) + abs(it.second) }.filter { it > 0 }.min() ?: 0
    }

}