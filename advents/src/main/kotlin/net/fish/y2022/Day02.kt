package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines
import net.fish.y2022.Day02.HandTypes.*

object Day02 : Day {
    private val data = resourceLines(2022, 2)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    enum class HandTypes(val score: Int) {
        ROCK(1), PAPER(2), SCISSORS(3);

        private fun beats(): HandTypes {
            return when(this) {
                ROCK -> SCISSORS
                PAPER -> ROCK
                SCISSORS -> PAPER
            }
        }

        fun score(against: HandTypes): Int {
            return this.score + when (against) {
                this -> 3
                this.beats() -> 6
                else -> 0
            }
        }
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
        return calculateScore(data) { s, _ -> getType(s, r1StrategyMap) }
    }

    fun doPart2(data: List<String>): Int {
        return calculateScore(data) { s, opponentPlays -> r2StrategyMap[opponentPlays]!![s]!! }
    }

    private fun calculateScore(data: List<String>, strategyPicker: (String, HandTypes) -> HandTypes): Int {
        return data.fold(0) { acc, l ->
            val (a,b) = l.split(" ", limit = 2)
            val aPlay = getType(a, r1StrategyMap)
            val bPlay = strategyPicker(b, aPlay)
            acc + bPlay.score(aPlay)
        }
    }

    private fun getType(p: String, strategies: Map<String, HandTypes>): HandTypes = strategies[p] ?: throw Exception("Unknown play $p")

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}