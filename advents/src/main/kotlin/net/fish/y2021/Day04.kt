package net.fish.y2021

import net.fish.Day
import net.fish.maths.transpose
import net.fish.resourceLines


object Day04 : Day {
    private val data = resourceLines(2021, 4)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val (calls, boards) = createBoards(data)
        var foundWinner = false
        var currentCallCount = 0
        var score = 0
        while(!foundWinner) {
            currentCallCount++
            val winningBoards = boards.filter { it.hasWon(calls.subList(0, currentCallCount)) }
            if (winningBoards.isNotEmpty()) {
                if (winningBoards.size > 1) throw Exception("Found multiple winning boards: $winningBoards")
                foundWinner = true
                score = winningBoards[0].score(calls.subList(0, currentCallCount))
            }
        }
        return score
    }

    fun doPart2(data: List<String>): Int {
        val (calls, boards) = createBoards(data)
        val boardsLeftToCheck = boards.toMutableList()
        val winningBoardsOrdered = mutableListOf<Board>()
        var currentCallCount = 0
        while(winningBoardsOrdered.size < boards.size) {
            currentCallCount++
            val winningBoards = boardsLeftToCheck.filter { it.hasWon(calls.subList(0, currentCallCount)) }
            if (winningBoards.isNotEmpty()) {
                winningBoardsOrdered.addAll(winningBoards)
                boardsLeftToCheck.removeAll(winningBoards)
            }
        }

        return winningBoardsOrdered.last().score(calls.subList(0, currentCallCount))
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

    data class Board(
        val numbers: List<Int>
    ) {
        private val rows = numbers.windowed(5, 5)
        private val columns = transpose(rows)

        fun score(calls: List<Int>): Int {
            val latestCall = calls.last()
            val unmarked = numbers - calls.toSet()
            return unmarked.sum() * latestCall
        }

        fun hasWon(calls: List<Int>): Boolean {
            if (rows.any { (it - calls.toSet()).isEmpty() }) return true
            if (columns.any { (it - calls.toSet()).isEmpty() }) return true
            return false
        }

    }

    fun createBoards(data: List<String>): Pair<List<Int>, List<Board>> {
        val calls = data[0].split(",").map { it.toInt() }
        val totalBoards = (data.size - 1) / 6 // calls line + (blank line + 5 rows of data) x boards
        val boards = (0 until totalBoards).map { i ->
            val currentBoardStartRow = 1 + i*6 + 1 // skip line 1, jump to i'th board, skip blank line in data
            val boardNumbers = mutableListOf<Int>()
            (0..4).forEach { line ->
                val boardRow = data[currentBoardStartRow + line].trim().split("\\s+".toRegex()).map { it.toInt() }
                boardNumbers.addAll(boardRow)
            }
            Board(boardNumbers)
        }
        return Pair(calls, boards)
    }
}