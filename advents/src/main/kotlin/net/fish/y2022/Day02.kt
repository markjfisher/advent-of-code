package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines
import net.fish.y2022.Day02.HandTypes.*

object Day02 : Day {
    private val data = resourceLines(2022, 2)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    enum class HandTypes(val score: Int) {
        ROCK(1), PAPER(2), SCISSORS(3)
    }

    private val r1StrategyMap = mapOf(
        "A" to ROCK,
        "B" to PAPER,
        "C" to SCISSORS,
        "X" to ROCK,
        "Y" to PAPER,
        "Z" to SCISSORS
    )

    // Lose/Draw/Win mappings
    private val r2StrategyMap = mapOf(
        ROCK to mapOf("X" to SCISSORS, "Y" to ROCK, "Z" to PAPER),
        PAPER to mapOf("X" to ROCK, "Y" to PAPER, "Z" to SCISSORS),
        SCISSORS to mapOf("X" to PAPER, "Y" to SCISSORS, "Z" to ROCK)
    )

    fun doPart1(data: List<String>): Int {
        val score = data.fold(0) { acc, l ->
            val (a,b) = l.split(" ", limit = 2)
            val aPlay = getType(a, r1StrategyMap)
            val bPlay = getType(b, r1StrategyMap)
            acc + scoreOf(aPlay, bPlay)
        }
        return score
    }

    fun scoreOf(aPlay: HandTypes, bPlay: HandTypes): Int {
        if (aPlay == ROCK && bPlay == ROCK) return ROCK.score + 3
        if (aPlay == ROCK && bPlay == PAPER) return PAPER.score + 6
        if (aPlay == PAPER && bPlay == PAPER) return PAPER.score + 3
        if (aPlay == PAPER && bPlay == SCISSORS) return SCISSORS.score + 6
        if (aPlay == SCISSORS && bPlay == SCISSORS) return SCISSORS.score + 3
        if (aPlay == SCISSORS && bPlay == ROCK) return ROCK.score + 6
        return bPlay.score
    }

    private fun getType(p: String, strategies: Map<String, HandTypes>): HandTypes = strategies[p] ?: throw Exception("Unknown play $p")

    fun doPart2(data: List<String>): Int {
        val score = data.fold(0) { acc, l ->
            val (a,b) = l.split(" ", limit = 2)
            val aPlay = getType(a, r1StrategyMap)
            val bPlay = r2StrategyMap[aPlay]!![b]!!
            acc + scoreOf(aPlay, bPlay)
        }
        return score
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}