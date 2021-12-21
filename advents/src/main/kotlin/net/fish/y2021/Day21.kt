package net.fish.y2021

import net.fish.Day
import java.lang.Integer.min
import java.lang.Long.max

object Day21 : Day {
    private val data = Pair(10, 2)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: Pair<Int, Int>): Long {
        val scores = generateScores(data.first, data.second)
        val winning = scores.dropWhile { it.first < 1000 && it.second < 1000 }.first()
        return min(winning.first, winning.second).toLong() * winning.third.toLong()
    }

    fun doPart2(data: Pair<Int, Int>): Long {
//        return solveP2(data)

        // alternate crazy fast
         val (w1, w2) = solveP2Crazy(data)
         return max(w1, w2)
    }

    private fun solveP2(data: Pair<Int, Int>): Long {
        var gameStates = mutableMapOf<Game, Long>()
        val initGame = Game(Player(0, data.first, 0), Player(1, data.second, 0))
        gameStates[initGame] = 1

        while(gameStates.any {it.key.winner(21) == null}) {
            gameStates = gameStates.entries.fold(mutableMapOf<Game, Long>()) { newGameStates, (game, count) ->
                if (game.winner(21) != null) {
                    newGameStates[game] = newGameStates.getOrDefault(game, 0L) + count
                } else {
                    splitToNewGames(game).forEach { newGameStates[it] = newGameStates.getOrDefault(it, 0L) + count }
                }
                newGameStates
            }
        }

        return gameStates.map { Pair(it.key.winner(21)!!.who, it.value ) }
            .groupBy { it.first }
            .mapValues { it.value.sumOf { l -> l.second } }
            .maxOf { it.value }
    }

    private fun splitToNewGames(game: Game): List<Game> {
        val games = listOf(game.deepCopy(), game.deepCopy(), game.deepCopy())
        games.forEachIndexed { i, g -> g.roll(i + 1) }
        return games
    }

    data class Game(
        var p1: Player,
        var p2: Player
    ) {
        private var current: Player = p1
        private var currentRoll = 0

        fun winner(minScore: Int): Player? = listOf(p1, p2).firstOrNull { it.score >= minScore }
        fun loser(): Player = listOf(p1, p2).minByOrNull { it.score }!!

        fun roll(roll: Int): Game {
            advance(current, roll)
            return this
        }

        private fun advance(player: Player, roll: Int) {
            player.move(roll)
            currentRoll++
            if (currentRoll == 3) {
                current.incScore()
                current = if (current == p1) p2 else p1
                currentRoll = 0
            }
        }

        fun deepCopy(): Game {
            val copy = Game(p1.copy(), p2.copy())
            if (current == p1) copy.current = copy.p1 else copy.current = copy.p2
            copy.currentRoll = currentRoll
            return copy
        }

    }

    data class Player(
        val who: Int,
        var pos: Int,
        var score: Int
    ) {
        fun move(roll: Int) {
            pos = (pos + roll - 1) % 10 + 1
        }

        fun incScore() {
            score += pos
        }
    }

    // This is a crazy fast version using an algorithm i found
    private fun solveP2Crazy(data: Pair<Int, Int>): Pair<Long, Long> {
        val comboMap = mutableMapOf<Combination, Long>()
        comboMap[Combination(0, 0, data.first, data.second, 0)] = 1L

        // Pascal's triangle for 3 rolls of 3 sided dice
        // 0, 0, 0, 1, 3, 6, 7, 6, 3, 1
        val rolls = Array(10) { 0 }
        for (r1 in 1..3) {
            for (r2 in 1..3) {
                for (r3 in 1..3) {
                    rolls[r1 + r2 + r3]++
                }
            }
        }
        var p1WinScore = 0L
        var p2WinScore = 0L

        for (s1 in 0..20) {
            for (s2 in 0..20) {
                for (p1 in 1..10) {
                    for (p2 in 1..10) {
                        for (r in 1..9) {
                            val c0 = comboMap.getOrDefault(Combination(s1, s2, p1, p2, 0), 0L)
                            val c1 = comboMap.getOrDefault(Combination(s1, s2, p1, p2, 1), 0L)
                            if (c0 > 0L) {
                                val p1NextPos = ((p1 + r) - 1) % 10 + 1
                                val p1NewScore = s1 + p1NextPos
                                val nc = c0 * rolls[r]
                                if (p1NewScore > 20) {
                                    p1WinScore += nc
                                } else {
                                    val cP1 = Combination(p1NewScore, s2, p1NextPos, p2, 1)
                                    comboMap[cP1] = comboMap.getOrDefault(cP1, 0L) + nc
                                }
                            }

                            if (c1 > 0L) {
                                val p2NextPos = ((p2 + r) - 1) % 10 + 1
                                val p2NewScore = s2 + p2NextPos
                                val nc = c1 * rolls[r]
                                if (p2NewScore > 20) {
                                    p2WinScore += nc
                                } else {
                                    val cP0 = Combination(s1, p2NewScore, p1, p2NextPos, 0)
                                    comboMap[cP0] = comboMap.getOrDefault(cP0, 0L) + nc
                                }
                            }

                        }
                    }
                }
            }
        }
        return Pair(p1WinScore, p2WinScore)
    }

    data class Combination(
        val s1: Int,
        val s2: Int,
        val p1: Int,
        val p2: Int,
        val i: Int
    )

    // Generated sequences of the initial game for part 0,
    // e.g. Triple(10, 0, 3), Triple(10, 3, 6), Triple(14, 3, 9), Triple(14, 9, 12)
    // but sadly couldn't shoehorn into part 2 solution yet.
    fun generateScores(p1Pos: Int, p2Pos: Int): Sequence<Triple<Int, Int, Int>> {
        var i = 0
        var p1Data = Pair(p1Pos, 0)
        var p2Data = Pair(p2Pos, 0)
        return generateSequence {
            when {
                i % 2 == 0 -> {
                    // Player 1
                    val newPos = (p1Data.first + 6 + 18 * i / 2 - 1) % 10 + 1
                    val newScore = p1Data.second + newPos
                    p1Data = Pair(newPos, newScore)
                }
                else -> {
                    // Player 2
                    val newPos = (p2Data.first + 15 + 18 * (i - 1) / 2 - 1) % 10 + 1
                    val newScore = p2Data.second + newPos
                    p2Data = Pair(newPos, newScore)
                }
            }
            i++
            Triple(p1Data.second, p2Data.second, i * 3)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}