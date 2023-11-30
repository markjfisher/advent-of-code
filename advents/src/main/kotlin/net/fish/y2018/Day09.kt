package net.fish.y2018

import net.fish.Day
import net.fish.maths.CircularArray
import net.fish.resourceLines

object Day09 : Day {
    override fun part1() = doPart1(428, 70825)
    override fun part2() = doPart2(428, 7082500)

    fun doPart1(numPlayers: Long, lastWorth: Long): Long = playMarbles(numPlayers, lastWorth)
    fun doPart2(numPlayers: Long, lastWorth: Long): Long = playMarbles(numPlayers, lastWorth)

    fun playMarbles(numPlayers: Long, lastWorth: Long): Long {
        val playerScores = Array(numPlayers.toInt()) { 0L }.toMutableList()
        val marbleCircle = CircularArray(listOf(0))
        var currentMarble = 1
        var currentPlayer = 1
        while (currentMarble <= lastWorth) {
            // the rule simplifies to Rotate Left 2, add current marble at location 0, and this keeps the current marble in virtual slot 0 of the circle
            // except when currentMarble is divisible by 23

            if (currentMarble % 23 == 0) {
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
        }

        return playerScores.max()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}