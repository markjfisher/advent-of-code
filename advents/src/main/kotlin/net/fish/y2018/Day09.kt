package net.fish.y2018

import net.fish.Day
import java.util.ArrayDeque

object Day09 : Day {
    override fun part1() = doPart1(428, 70825)
    override fun part2() = doPart2(428, 7082500)

    fun doPart1(numPlayers: Int, lastWorth: Int): Long = playMarblesBoard(numPlayers, lastWorth)
    fun doPart2(numPlayers: Int, lastWorth: Int): Long = playMarblesBoard(numPlayers, lastWorth)

    // Ridiculously fast circlular array
    class Board : ArrayDeque<Int>() {
        fun rotate(amount: Int) {
            if (amount >= 0) {
                for (i in 0 until amount) {
                    addFirst(removeLast())
                }
            } else {
                for (i in 0 until -amount - 1) {
                    addLast(remove())
                }
            }
        }
    }

    fun playMarblesBoard(numPlayers: Int, lastWorth: Int): Long {
        val playerScores = LongArray(numPlayers)
        val board = Board()
        board.addFirst(0)
        for (marble in (1..lastWorth)) {
            if (marble % 23 == 0) {
                board.rotate(-7)
                playerScores[marble % numPlayers] += board.pop().toLong() + marble
            } else {
                board.rotate(2)
                board.addLast(marble)
            }
        }
        return playerScores.max()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}