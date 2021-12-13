package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines

object Day12 : Day {
    // private val startCave = parseCaves(resourceLines(2021, 12))

    override fun part1() = doPart1()
    override fun part2() = doPart2()

    fun doPart1(): Int = 0//traverse(start, ::singleVisitSmallCaves).size
    fun doPart2(): Int = 0//traverse(start, ::doubleVisitSmallCavesOnceOnly).size

    fun parseCaves(data: List<String>): Cave {
        val caves: MutableMap<String, Cave> = mutableMapOf()
        data.forEach { line ->
            val caveNames = line.split("-")
            val from = caveNames[0]
            val to = caveNames[1]
            if (caves[from] == null) {
                caves[from] = Cave(from)
            }
            if (caves[to] == null) {
                caves[to] = Cave(to)
            }
            caves[from]!!.connected += caves[to]!!
            caves[to]!!.connected += caves[from]!!
        }
        return caves["start"]!!
    }

    fun traverse(start: Cave, exitIsValid: (Cave, List<Cave>) -> Boolean): Set<List<Cave>> {
        return getPaths(start, mutableListOf(), mutableSetOf(), exitIsValid)
    }

    fun getPaths(from: Cave, currentPath: MutableList<Cave>, routes: MutableSet<List<Cave>>, exitIsValid: (Cave, List<Cave>) -> Boolean): MutableSet<List<Cave>> {
        println("cave: $from, path: $currentPath, routes: $routes")
        if (from.isEnd()) {
            routes.add(currentPath + from)
            return routes
        }
        currentPath.add(from)

        from.connected.forEach { connectedCave ->
            if (exitIsValid(connectedCave, currentPath)) {
                return getPaths(connectedCave, currentPath, routes, exitIsValid)
            }
        }

        return routes
    }

    fun singleVisitSmallCaves(to: Cave, currentPath: List<Cave>): Boolean {
        return to.isBig() || !currentPath.contains(to)
    }

    fun doubleVisitSmallCavesOnceOnly(to: Cave, currentPath: List<Cave>): Boolean {
        if (to.isStart()) return false
        if (to.isEnd()) return true
        if (to.isBig()) return true

        val smallCavesVisited = currentPath.filter { !it.isBig() }
        if (!smallCavesVisited.contains(to)) return true

        // check if any of them have been visited twice, this is only allowed to happen a single time
        return !smallCavesVisited.groupBy { it.name }.any { it.value.size == 2 }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

    class Cave(
        val name: String,
        val connected: MutableSet<Cave> = mutableSetOf()
    ) {
        fun isBig() = (name.first().isUpperCase())
        fun isStart() = (name == "start")
        fun isEnd() = (name == "end")

        override fun toString(): String {
            // recursive hell in debug if we don't define this
            return "Cave[$name]"
        }
    }


}