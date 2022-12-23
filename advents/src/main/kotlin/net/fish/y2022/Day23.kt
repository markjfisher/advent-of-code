package net.fish.y2022

import net.fish.Day
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.resourceLines

object Day23 : Day {
    private val neighbours = listOf(Point(1, 0), Point(1, 1), Point(0, 1), Point(-1, 1), Point(-1, 0), Point(-1, -1), Point(0, -1), Point(1, -1))

    fun toElfGrid(map: List<List<Char>>): ElfGrid {
        val allPoints = map.indices.flatMap { y ->
            map[y].indices.map { x ->
                Point(x, y)
            }
        }
        return ElfGrid(allPoints.filter { (x, y) -> map[y][x] == '#' }.toMutableSet())
    }

    override fun part1() = doPart1(toElfGrid(resourceLines(2022, 23).map { it.toCharArray().toList() }))
    override fun part2() = doPart2(toElfGrid(resourceLines(2022, 23).map { it.toCharArray().toList() }))

    fun doPart1(elfGrid: ElfGrid): Int {
        elfGrid.step(10)
        val bounds = elfGrid.elves.bounds()
        val area = (bounds.second.x - bounds.first.x + 1) * (bounds.second.y - bounds.first.y + 1)
        return area - elfGrid.elves.count()
    }

    fun doPart2(elfGrid: ElfGrid): Int {
        do {
            elfGrid.step(1)
        } while (!elfGrid.checkStatic())
        return elfGrid.currentIteration + 1
    }

    data class ElfGrid(val elves: MutableSet<Point>) {
        private var currentProposalDirIndex = 0
        private val proposalDirs = listOf(
            listOf(Point(0, -1), Point(1, -1), Point(-1, -1)),  // N NE NW
            listOf(Point(0, 1), Point(1, 1), Point(-1, 1)),     // S SE SW
            listOf(Point(-1, 0), Point(-1, -1), Point(-1, 1)),  // W NW SW
            listOf(Point(1, 0), Point(1, -1), Point(1, 1)),     // E NE SE
        )
        private var static = false
        var currentIteration = 0

        fun step(steps: Int = 1) {
            (0 until steps).forEach { _ ->
                if (checkStatic()) return
                currentIteration++
                val proposedMovesMap = mutableMapOf<Point, Point>()
                // select elves who have no neighbours at all
                val elvesToMove = elves.filter { p -> neighbours.any { elves.contains(p + it) } }.toMutableSet()

                for (i in 0 .. 3) {
                    val elvesMoved = mutableSetOf<Point>()
                    val dirIndex = (currentProposalDirIndex + i) % 4
                    elvesToMove.forEach { elf ->
                        if (proposalDirs[dirIndex].none { elves.contains(elf + it) }) {
                            proposedMovesMap[elf] = elf + proposalDirs[dirIndex].first()
                            elvesMoved += elf
                        }
                    }
                    elvesToMove.removeAll(elvesMoved)
                }
                // check for any elves who would move to same location
                // Where there are multiple elves going to same location, remove the entry from proposed moves
                val locationToElves = proposedMovesMap.entries.groupBy { it.value }
                val commonLocations = locationToElves.filter { it.value.size > 1 }.keys
                commonLocations.forEach { common ->
                    proposedMovesMap.filter { it.value == common }.keys.forEach { proposedMovesMap.remove(it) }
                }
                // merge the results back into the elves
                elves.removeAll(proposedMovesMap.keys)
                elves.addAll(proposedMovesMap.values)
                currentProposalDirIndex = (currentProposalDirIndex + 1) % 4
            }

        }

        fun checkStatic(): Boolean {
            if (elves.all { p -> neighbours.none { elves.contains(p + it) } }) {
                static = true
            }
            return static
        }

        fun toGrid(p1: Point? = null, p2: Point? = null): List<String> {
            var bounds = elves.bounds()
            if (p1 != null) bounds = Pair(p1, bounds.second)
            if (p2 != null) bounds = Pair(bounds.first, p2)

            val ret = mutableListOf<String>()
            for (j in bounds.first.y .. bounds.second.y) {
                var row = ""
                for (i in bounds.first.x .. bounds.second.x) {
                    row += if (elves.contains(Point(i, j))) "#" else "."
                }
                ret += row
            }
            return ret
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}