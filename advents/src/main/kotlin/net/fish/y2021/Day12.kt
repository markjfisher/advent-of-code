package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines

object Day12 : Day {
    private val caves = toCaves(resourceLines(2021, 12))

    override fun part1() = doPart1(caves)
    override fun part2() = doPart2(caves)

    fun doPart1(caves: Map<String, Cave>): Int = traverse("start", "end", caves).size
    fun doPart2(caves: Map<String, Cave>): Int = traverseWithRevist("start", "end", caves).size

    fun toCaves(data: List<String>): Map<String, Cave> {
        val caves: MutableMap<String, Cave> = mutableMapOf()
        data.forEach { line ->
            val caveNames = line.split("-")
            val from = caveNames[0]
            val to = caveNames[1]
            if (caves[from] == null) {
                caves[from] = Cave(from, from.first().isUpperCase())
            }
            if (caves[to] == null) {
                caves[to] = Cave(to, to.first().isUpperCase())
            }
            caves[from]!!.connected += caves[to]!!.name
            caves[to]!!.connected += caves[from]!!.name
        }
        return caves.toMap()
    }

    fun traverse(from: String, to: String, caves: Map<String, Cave>): Set<List<String>> {
        return getPaths(from, to, caves, mutableMapOf(), mutableListOf())
    }

    fun traverseWithRevist(from: String, to: String, caves: Map<String, Cave>): Set<List<String>> {
        return caves.values.fold(setOf()) { acc, cave ->
            val paths = if (!cave.isBig && cave.name != from && cave.name != to) {
                // we have a small cave we can duplicate
                val dupeName = "${cave.name}2"
                val newCaves = mutableMapOf<String, Cave>().also { map ->
                    caves.forEach { c -> map[c.key] = c.value.copy() }
                }
                newCaves[dupeName] = cave.copy()

                // rework back references to include this duplicate cave
                newCaves.values.forEach { cc ->
                    if (cc.connected.contains(cave.name)) {
                        cc.connected += dupeName
                    }
                }
                // now get all paths. We're only interested in cases where we have duplicated a cave
                val pathsWithDuplicate = getPaths(from, to, newCaves, mutableMapOf(), mutableListOf())
                // we need to change the dupes back to normal caves, and reduce our output set, e.g. A-b-A-b2-A-end, and A-b2-A-b-A-end are actually the same routes
                val paths = pathsWithDuplicate.fold(mutableSetOf<List<String>>()) { acc2, cavePath ->
                    // Deduplicate by changing names back to original names, so clashes cancel each other
                    val newPath = cavePath.map { it.substringBefore("2") }
                    acc2.add(newPath)
                    acc2
                }
                paths
            } else emptySet()
            acc + paths
        }
    }

    fun getPaths(currentCaveName: String, to: String, caves: Map<String, Cave>, traversed: MutableMap<String, Int>, currentPath: MutableList<String>): Set<MutableList<String>> {
        // if it's a small cave and we've been here before, there's no more paths to be found
        if (!caves[currentCaveName]!!.isBig && traversed.getOrDefault(currentCaveName, 0) > 0) {
            return emptySet()
        }
        // increment visit count for this cave, and mark it on the path
        val currentCave = caves[currentCaveName]!!
        traversed[currentCaveName] = traversed.getOrDefault(currentCaveName, 0) + 1
        currentPath += currentCaveName

        // have we reached our end point?
        if (currentCaveName == to) {
            return setOf(currentPath)
        }

        // now look down all the connected paths from this point, recursing into hell
        return currentCave.connected.fold(setOf()) { acc, cave ->
            // We need copies of the current traversed and current path so the originals don't get blat in the recursion
            val newTraversed = mutableMapOf<String, Int>().also { it.putAll(traversed) }
            val newCurrentPath = mutableListOf<String>().also { it.addAll(currentPath) }
            acc + getPaths(cave, to, caves, newTraversed, newCurrentPath)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

    data class Cave(
        val name: String,
        val isBig: Boolean,
        val connected: MutableList<String> = mutableListOf()
    ) {
        fun copy(): Cave {
            return Cave(this.name, this.isBig, mutableListOf<String>().also { it.addAll(this.connected) })
        }
    }


}