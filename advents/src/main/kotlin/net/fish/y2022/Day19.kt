package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines
import kotlin.math.max
import kotlin.math.min

object Day19 : Day {
    private val blueprintExtractor by lazy { Regex("""Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""") }

    override fun part1() = doPart1(toBlueprints(resourceLines(2022, 19)))
    override fun part2() = doPart2(toBlueprints(resourceLines(2022, 19)))

    fun doPart1(blueprints: List<Blueprint>): Int {
        return blueprints.fold(0) {ac, bp ->
            ac + bp.id * score(bp, 24)
        }
    }

    fun doPart2(blueprints: List<Blueprint>): Int {
        return blueprints.take(min(3, blueprints.size)).map { score(it, 32) }.reduce(Int::times)
    }

    data class BuildState(
        val oreCount: Int,
        val clayCount: Int,
        val obsidianCount: Int,
        val geodeCount: Int,
        val oreRobots: Int,
        val clayRobots: Int,
        val obsidianRobots: Int,
        val geodeRobots: Int,
        val time: Int
    )

    fun score(blueprint: Blueprint, minutes: Int): Int {
        val largestOreCost = listOf(blueprint.oreRobotCost, blueprint.clayRobotCost, blueprint.obsidianRobotCost.ore, blueprint.geodeRobotCost.ore).max()
        val initialState = BuildState(0, 0, 0, 0, 1, 0, 0, 0, minutes)
        val queue = ArrayDeque<BuildState>()
        queue.addLast(initialState)
        val seen = mutableSetOf<BuildState>()

        var maxGeodeCount = 0
//        var maxSeenCount = 0
//        var maxQueueSize = 0
        while (queue.isNotEmpty()) {
            val state = queue.removeFirst()
            var (o, c, ob, g, r1, r2, r3, r4, t) = state

            maxGeodeCount = max(g, maxGeodeCount)
            if (state.time == 0) continue

            // Apply heuristics to limit the states generated. Without setting some limits, the state space is too wide.
            // Don't buy more robots than the (largest) cost of that type, else we generate more ore than we can use
            r1 = min(r1, largestOreCost)
            r2 = min(r2, blueprint.obsidianRobotCost.clay)
            r3 = min(r3, blueprint.geodeRobotCost.obsidian)
            // also, trim excess against future requirements. We can discard some of our resource if all future production less the amount that will be produced exceeds our current amount
            // This massively reduces the state space
            o = min(o, t * largestOreCost - r1 * (t - 1))
            c = min(c, t * blueprint.obsidianRobotCost.clay - r2 * (t - 1))
            ob = min(ob, t * blueprint.geodeRobotCost.obsidian - r3 * (t - 1))

            val newState = BuildState(o, c, ob, g, r1, r2, r3, r4, t)

            if (seen.contains(newState)) continue
            seen += newState
            // maxSeenCount = max(maxSeenCount, seen.size)

            // bloody AOC - p2 goes into 2M ish queue/seen sizes.
            // if (seen.size % 200000 == 0) println("t: $t, best: $maxGeodeCount, seen: ${seen.size}")

            // ADD NEW STATES TO QUEUE

            // don't buy anything
            queue.addLast(BuildState(o + r1, c + r2, ob + r3, g + r4, r1, r2, r3, r4, t - 1))
            // buy ore robot
            if (o >= blueprint.oreRobotCost) {
                queue.addLast(BuildState(o - blueprint.oreRobotCost + r1, c + r2, ob + r3, g + r4, r1 + 1, r2, r3, r4, t - 1))
            }
            // buy clay robot
            if (o >= blueprint.clayRobotCost) {
                queue.addLast(BuildState(o - blueprint.clayRobotCost + r1, c + r2, ob + r3, g + r4, r1, r2 + 1, r3, r4, t - 1))
            }
            // buy obsidian robot
            if (o >= blueprint.obsidianRobotCost.ore && c >= blueprint.obsidianRobotCost.clay) {
                queue.addLast(BuildState(o - blueprint.obsidianRobotCost.ore + r1, c - blueprint.obsidianRobotCost.clay + r2, ob + r3, g + r4, r1, r2, r3 + 1, r4, t - 1))
            }
            // buy geode robot
            if (o >= blueprint.geodeRobotCost.ore && ob >= blueprint.geodeRobotCost.obsidian) {
                queue.addLast(BuildState(o - blueprint.geodeRobotCost.ore + r1, c + r2, ob - blueprint.geodeRobotCost.obsidian + r3, g + r4, r1, r2, r3, r4 + 1, t - 1))
            }
            // MOAR DEBUG
            // maxQueueSize = max(maxQueueSize, queue.size)
            // if (maxQueueSize % 100000 == 0) println("max queue size: ${queue.size}")
        }
        // println("max seen: $maxSeenCount, max queue: $maxQueueSize")
        return maxGeodeCount
    }

    data class ObsidianRobotCost(val ore: Int, val clay: Int)
    data class GeodeRobotCost(val ore: Int, val obsidian: Int)
    data class Blueprint(val id: Int, val oreRobotCost: Int, val clayRobotCost: Int, val obsidianRobotCost: ObsidianRobotCost, val geodeRobotCost: GeodeRobotCost)

    fun toBlueprints(data: List<String>): List<Blueprint> {
        return data.map { line ->
            blueprintExtractor.find(line)?.destructured!!.let { (a, b, c, d, e, f, g) ->
                Blueprint(a.toInt(), b.toInt(), c.toInt(), ObsidianRobotCost(d.toInt(), e.toInt()), GeodeRobotCost(f.toInt(), g.toInt()))
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }
}