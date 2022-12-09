package net.fish.y2022

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day09Test {
    @Test
    fun `can do part 1`() {
        assertThat(Day09.doPart1(Day09.toMovement(resourcePath("/2022/day09-test.txt")))).isEqualTo(13)
    }

    @Test
    fun `can do part 2`() {
        assertThat(Day09.doPart2(Day09.toMovement(resourcePath("/2022/day09b-test.txt")))).isEqualTo(36)
    }

    @Test
    fun `can process test moves and show grid`() {
        val moves = Day09.toMovement(resourcePath("/2022/day09b-test.txt"))
        val knotPositions = Day09.processMoves(moves = moves, knotCount = 10, printDebug = true, finalOnly = true)
        assertThat(knotPositions.last().count()).isEqualTo(36)
    }

    @Test
    fun `knot follows parent when it moves 2 away`() {
        // straight
        assertThat(Day09.processMove(Point(2,0), Point(0, 0))).isEqualTo(Point(1,0))
        assertThat(Day09.processMove(Point(0,2), Point(0, 0))).isEqualTo(Point(0,1))
        assertThat(Day09.processMove(Point(-2,0), Point(0, 0))).isEqualTo(Point(-1,0))
        assertThat(Day09.processMove(Point(0,-2), Point(0, 0))).isEqualTo(Point(0,-1))

        assertThat(Day09.processMove(Point(2,2), Point(0, 0))).isEqualTo(Point(1,1))
        assertThat(Day09.processMove(Point(2,-2), Point(0, 0))).isEqualTo(Point(1,-1))
        assertThat(Day09.processMove(Point(-2,2), Point(0, 0))).isEqualTo(Point(-1,1))
        assertThat(Day09.processMove(Point(-2,-2), Point(0, 0))).isEqualTo(Point(-1,-1))

        // knight move
        assertThat(Day09.processMove(Point(2,1), Point(0, 0))).isEqualTo(Point(1,1))
        assertThat(Day09.processMove(Point(1,2), Point(0, 0))).isEqualTo(Point(1,1))

        assertThat(Day09.processMove(Point(2,-1), Point(0, 0))).isEqualTo(Point(1,-1))
        assertThat(Day09.processMove(Point(1,-2), Point(0, 0))).isEqualTo(Point(1,-1))

        assertThat(Day09.processMove(Point(-2,1), Point(0, 0))).isEqualTo(Point(-1,1))
        assertThat(Day09.processMove(Point(-1,2), Point(0, 0))).isEqualTo(Point(-1,1))

        assertThat(Day09.processMove(Point(-2,-1), Point(0, 0))).isEqualTo(Point(-1,-1))
        assertThat(Day09.processMove(Point(-1,-2), Point(0, 0))).isEqualTo(Point(-1,-1))

    }

    @Test
    fun `knot stays if parent still touching`() {

    }
}