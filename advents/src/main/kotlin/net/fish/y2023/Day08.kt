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
            // println("steps: $steps")
            return steps
        }

        fun parallel(): Long {
            val starts = nodes.keys.filter { it.endsWith("A") }
            val ends = nodes.keys.filter { it.endsWith("Z") }.toSet()

            // for each start, find the length of the cycle until it hits an end node, then find LCM of all those cycle times
            // NOTE: this only works because the input data is set up to make:
            // - the distance from A to Z node = distance from Z to Z node
            // - paths don't cross each other (there are 6 independent paths)
            // - the path length is exactly a multiple of the LR data length (which is prime)
            return starts
                .map { node -> walk(node, ends) }
                .reduce { ac, f -> lcm(ac, f) }
        }

        // Function to help test the paths. Not used in solution
        fun walking(start: String, steps: Int, directionInstructions: Iterator<Char>, debug: Boolean = false): String {
            val visited = mutableSetOf<String>()
            var currentLocation = start
            println("Start: $start")
            visited.add(start)
            for (i in 0 until steps) {
                val dir = directionInstructions.next()
                currentLocation = when (dir) {
                    'L' -> nodes[currentLocation]!!.first
                    'R' -> nodes[currentLocation]!!.second
                    else -> throw Exception("Unknown instruction $dir")
                }
                if (debug) {
                    println("$dir ->  $currentLocation")
                }
                visited.add(currentLocation)
            }
            println("End: $currentLocation, visited ${visited.size}: $visited")
            return currentLocation
        }
    }

    private fun analyse() {
        val instructions = data
        val directionInstructions = instructions.dirs.asSequence().cycle().iterator()
        val last = instructions.walking("AAA", 20513, directionInstructions, true)
        println(last)
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
        //analyse()
    }

}
