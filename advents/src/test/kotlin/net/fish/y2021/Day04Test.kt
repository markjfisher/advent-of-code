package net.fish.y2021

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day04Test {
    private val boardData = listOf(
        14, 21, 17, 24,  4,
        10, 16, 15,  9, 19,
        18,  8, 23, 26, 20,
        22, 11, 13,  6,  5,
        2,  0, 12,  3,  7
    )

    @Test
    fun `part1 on test data scores 4512`() {
        val boardDataRaw = resourcePath("/2021/day04-test.txt")
        val score = Day04.doPart1(boardDataRaw)
        assertThat(score).isEqualTo(4512)
    }

    @Test
    fun `part2 on test data scores 1924`() {
        val boardDataRaw = resourcePath("/2021/day04-test.txt")
        val score = Day04.doPart2(boardDataRaw)
        assertThat(score).isEqualTo(1924)
    }

    @Test
    fun `can create boards`() {
        val boardDataRaw = resourcePath("/2021/day04-test.txt")
        val (calls, boards) = Day04.createBoards(boardDataRaw)
        assertThat(calls).containsExactly(7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1)
        assertThat(boards).hasSize(3)

        assertThat(boards[0].numbers).containsExactly(22, 13, 17, 11, 0, 8, 2, 23, 4, 24, 21, 9, 14, 16, 7, 6, 10, 3, 18, 5, 1, 12, 20, 15, 19)
    }

    @Test
    fun `can get score of a board`() {
        val board = Day04.Board(boardData)
        assertThat(board.score(listOf(7, 4, 9, 5, 11, 17, 23, 2, 0, 14, 21, 24))).isEqualTo(4512)
    }

    @Test
    fun `can detect horizontal board win`() {
        val board = Day04.Board(boardData)
        assertThat(board.hasWon(listOf(14))).isFalse
        assertThat(board.hasWon(listOf(14, 21, 17, 24))).isFalse
        assertThat(board.hasWon(listOf(14, 21, 17, 24, 4))).isTrue

        assertThat(board.hasWon(listOf(16))).isFalse
        assertThat(board.hasWon(listOf(16, 15,  9, 19))).isFalse
        assertThat(board.hasWon(listOf(16, 15,  9, 19, 10))).isTrue

        assertThat(board.hasWon(listOf(23))).isFalse
        assertThat(board.hasWon(listOf(23, 26, 20, 18))).isFalse
        assertThat(board.hasWon(listOf(23, 26, 20, 18, 8))).isTrue

        assertThat(board.hasWon(listOf(6))).isFalse
        assertThat(board.hasWon(listOf(22, 11, 13,  6))).isFalse
        assertThat(board.hasWon(listOf(22, 11, 13,  6,  5))).isTrue

        assertThat(board.hasWon(listOf(7))).isFalse
        assertThat(board.hasWon(listOf(2,  0, 12,  7))).isFalse
        assertThat(board.hasWon(listOf(2,  0, 12,  3,  7))).isTrue
    }

    @Test
    fun `can detect vertical board win`() {
        val board = Day04.Board(boardData)
        assertThat(board.hasWon(listOf(14))).isFalse
        assertThat(board.hasWon(listOf(14, 10, 18, 22))).isFalse
        assertThat(board.hasWon(listOf(14, 10, 18, 22, 2))).isTrue
    }

}