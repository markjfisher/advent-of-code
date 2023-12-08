package net.fish.y2023

import net.fish.Day
import net.fish.collections.cycle
import net.fish.maths.lcm
import net.fish.resourceStrings

object Day08 : Day {
    private val nodeExtractor by lazy { Regex("""([A-Z0-9]{3}) = \(([A-Z0-9]{3}), ([A-Z0-9]{3})\)""") }
    private val data by lazy { readPuzzle(resourceStrings(2023, 8)) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: DesertInstructions): Long = data.walk("AAA", setOf("ZZZ"))
    fun doPart2(data: DesertInstructions): Long = data.parallel()

    data class DesertInstructions(val dirs: String, val nodes: Map<String, Pair<String, String>>) {
        fun walk(start: String, ends: Set<String>): Long {
            val directionInstructions = dirs.asSequence().cycle().iterator()
            var currentLocation = start
            var steps = 0L
            while (!ends.contains(currentLocation)) {
                val dir = directionInstructions.next()
                currentLocation = when (dir) {
                    'L' -> nodes[currentLocation]!!.first
                    'R' -> nodes[currentLocation]!!.second
                    else -> throw Exception("Unknown instruction $dir")
                }
                steps++
            }
            return steps
        }

        fun parallel(): Long {
            val starts = nodes.keys.filter { it.endsWith("A") }
            val ends = nodes.keys.filter { it.endsWith("Z") }.toSet()

            // for each start, find the length of the cycle until it hits an end node, then find LCM of all those cycle times
            return starts
                .map { node -> walk(node, ends) }
                .reduce { ac, f -> lcm(ac, f) }
        }
    }

    fun readPuzzle(data: List<String>): DesertInstructions {
        val sequence = data[0]

        val nodes = data[1].split("\n").fold(mutableMapOf<String, Pair<String, String>>()) { ac, line ->
            nodeExtractor.find(line)?.destructured!!.let { (a, b, c) ->
                ac[a] = Pair(b, c)
            }
            ac
        }
        return DesertInstructions(sequence, nodes)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}
