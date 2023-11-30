package net.fish.y2018

import net.fish.Day
import net.fish.maths.CircularArray
import java.util.*

object Day09 : Day {
    override fun part1() = doPart1(428, 70825)
    override fun part2() = doPart2(428, 7082500)

    fun doPart1(numPlayers: Int, lastWorth: Int): Long = playMarblesBoard(numPlayers, lastWorth)
    fun doPart2(numPlayers: Int, lastWorth: Int): Long = playMarblesBoard(numPlayers, lastWorth)

    // This is too slow on part 2, the CircularArray is taking too long to move things about.
    fun playMarbles(numPlayers: Int, lastWorth: Int): Long {
        val playerScores = LongArray(numPlayers)
        val marbleCircle = CircularArray(listOf(0))
        var currentMarble = 1L
        var currentPlayer = 1
        while (currentMarble <= lastWorth) {
            val s = System.nanoTime()
            // the rule simplifies to Rotate Left 2, add current marble at location 0, and this keeps the current marble in virtual slot 0 of the circle
            // except when currentMarble is divisible by 23

            if (currentMarble % 23 == 0L) {
                marbleCircle.rotateRight(7)
                val extra = marbleCircle.remove()
                playerScores[currentPlayer] = playerScores[currentPlayer] + currentMarble + extra
            } else {
                marbleCircle.rotateLeft(2)
                marbleCircle.add(0, currentMarble.toLong())
            }
            currentMarble++
            currentPlayer++
            if (currentPlayer >= numPlayers) currentPlayer = 0

            val t1 = System.nanoTime()
            println("m: ${currentMarble}, t: ${t1 - s}")
        }

        return playerScores.max()
    }

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