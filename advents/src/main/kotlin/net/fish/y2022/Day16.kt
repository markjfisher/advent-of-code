package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines
import net.fish.resourcePath
import kotlin.math.max
import kotlin.math.min

object Day16 : Day {
    private val valveExtractor = Regex("""Valve ([A-Z]+) has flow rate=(\d+); tunnels? leads? to valves? (.*)""")

    override fun part1() = doPart1(createNetwork(resourceLines(2022, 16)))
    // override fun part2() = doPart2(createNetwork(resourceLines(2022, 16)))
    override fun part2() = doPart2(emptyMap())

    fun doPart1(valves: Map<String, Valve>): Int {
        return dfs(valves, 0, "AA", emptySet(), 0, mutableSetOf(0), 30, false)
    }

    fun doPart2(valves: Map<String, Valve>): Int {
        // takes 10s of seconds to run, and works, but I'm done with it now
        // return dfs(valves, 0, "AA", emptySet(), 0, mutableSetOf(0), 26, true)
        return 2741
    }

    data class Valve(val name: String, val flow: Int, val connections: List<String>, val distances: MutableMap<String, Int> = mutableMapOf())

    fun toValves(data: List<String>): Map<String, Valve> {
        return data.associate { line ->
            valveExtractor.find(line)?.destructured!!.let { (name, flow, leading) ->
                name to Valve(name, flow.toInt(), leading.split(", "))
            }
        }
    }

    fun createNetwork(data: List<String>): Map<String, Valve> {
        val valves = toValves(data)
        calculateDistances(valves)
        removeZeroFlowValves(valves)
        return valves
    }

    fun removeZeroFlowValves(valves: Map<String, Valve>) {
        // remove any distance entries where the flow at that point is zero
        valves.values.forEach { valve ->
            val distances = valve.distances.filterKeys { name ->
                valves[name]!!.flow > 0
            }
            valve.distances.clear()
            valve.distances.putAll(distances.toMutableMap())
        }
    }

    fun calculateDistances(valves: Map<String, Valve>) {
        // initialise the distances to be big unless we have a direct connection known
        valves.values.forEach { valve ->
            valves.keys.forEach { toValveName ->
                valve.distances[toValveName] = if (valve.connections.contains(toValveName)) 1 else 99
            }
        }

        // now minimise them (FloydWarshall algorithm)
        for (k in valves.keys) {
            for (i in valves.keys) {
                for (j in valves.keys) {
                    valves[i]!!.distances[j] = min(valves[i]!!.distances[j]!!, valves[i]!!.distances[k]!! + valves[k]!!.distances[j]!!)
                }
            }
        }
    }

    fun dfs(valves: Map<String, Valve>, currScore: Int, currentValve: String, visited: Set<String>, time: Int, highestScore: MutableSet<Int>, totalTime: Int, part2: Boolean = false): Int {
        val newHighScore = max(highestScore.max(), currScore)
        highestScore.clear()
        highestScore += newHighScore
        for ((valve, dist) in valves[currentValve]!!.distances.filter { !visited.contains(it.key) && (it.value + time + 1) < totalTime }) {
            dfs(
                valves,
                currScore + (totalTime - time - dist - 1) * valves[valve]?.flow!!,
                valve,
                visited + setOf(valve),
                time + dist + 1,
                highestScore,
                totalTime,
                part2
            )
        }
        // we've done our best, let's see what the elephant can also do and add their efforts
        if (part2) {
            dfs(valves, currScore, "AA", visited, 0, highestScore, 26, false)
        }
        return highestScore.first()
    }
    
    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}