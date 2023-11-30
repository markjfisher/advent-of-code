package net.fish.y2018

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day10Test {
    @Test
    fun `can read stars`() {
        assertThat(Day10.toStars(resourcePath("/2018/day10-test.txt"))).containsExactly(
            Pair(Point(x = 9, y = 1), Point(x = 0, y = 2)),
            Pair(Point(x = 7, y = 0), Point(x = -1, y = 0)),
            Pair(Point(x = 3, y = -2), Point(x = -1, y = 1)),
            Pair(Point(x = 6, y = 10), Point(x = -2, y = -1)),
            Pair(Point(x = 2, y = -4), Point(x = 2, y = 2)),
            Pair(Point(x = -6, y = 10), Point(x = 2, y = -2)),
            Pair(Point(x = 1, y = 8), Point(x = 1, y = -1)),
            Pair(Point(x = 1, y = 7), Point(x = 1, y = 0)),
            Pair(Point(x = -3, y = 11), Point(x = 1, y = -2)),
            Pair(Point(x = 7, y = 6), Point(x = -1, y = -1)),
            Pair(Point(x = -2, y = 3), Point(x = 1, y = 0)),
            Pair(Point(x = -4, y = 3), Point(x = 2, y = 0)),
            Pair(Point(x = 10, y = -3), Point(x = -1, y = 1)),
            Pair(Point(x = 5, y = 11), Point(x = 1, y = -2)),
            Pair(Point(x = 4, y = 7), Point(x = 0, y = -1)),
            Pair(Point(x = 8, y = -2), Point(x = 0, y = 1)),
            Pair(Point(x = 15, y = 0), Point(x = -2, y = 0)),
            Pair(Point(x = 1, y = 6), Point(x = 1, y = 0)),
            Pair(Point(x = 8, y = 9), Point(x = 0, y = -1)),
            Pair(Point(x = 3, y = 3), Point(x = -1, y = 1)),
            Pair(Point(x = 0, y = 5), Point(x = 0, y = -1)),
            Pair(Point(x = -2, y = 2), Point(x = 2, y = 0)),
            Pair(Point(x = 5, y = -2), Point(x = 1, y = 2)),
            Pair(Point(x = 1, y = 4), Point(x = 2, y = 1)),
            Pair(Point(x = -2, y = 7), Point(x = 2, y = -2)),
            Pair(Point(x = 3, y = 6), Point(x = -1, y = -1)),
            Pair(Point(x = 5, y = 0), Point(x = 1, y = 0)),
            Pair(Point(x = -6, y = 0), Point(x = 2, y = 0)),
            Pair(Point(x = 5, y = 9), Point(x = 1, y = -2)),
            Pair(Point(x = 14, y = 7), Point(x = -2, y = 0)),
            Pair(Point(x = -3, y = 6), Point(x = 2, y = -1))
        )
    }

    @Test
    fun `can move point`() {
        val p1 = Pair(Point(x = -3, y = 6), Point(x = 2, y = -1))
        assertThat(Day10.moveStars(listOf(p1))).containsExactly(Pair(Point(-1, 5), Point(2, -1)))
        assertThat(Day10.moveStars(listOf(p1), 2)).containsExactly(Pair(Point(1, 4), Point(2, -1)))
    }

    @Test
    fun `can get area of stars`() {
        val p1 = Pair(Point(x = 0, y = 0), Point(x = 2, y = -1))
        val p2 = Pair(Point(x = 2, y = 2), Point(x = -1, y = 2))
        val s1 = listOf(p1, p2)
        assertThat(Day10.boudaryArea(s1)).isEqualTo(4)
        val s2 = Day10.moveStars(s1)
        assertThat(Day10.boudaryArea(s2)).isEqualTo(5)
    }

    @Test
    fun `can print stars`() {
        val stars = Day10.toStars(resourcePath("/2018/day10-test.txt"))
        val day3Stars = Day10.moveStars(stars, 3)
        val s = Day10.displayStars(day3Stars).trim()
        assertThat(s).isEqualTo("""
            ▓░░░▓░░▓▓▓
            ▓░░░▓░░░▓░
            ▓░░░▓░░░▓░
            ▓▓▓▓▓░░░▓░
            ▓░░░▓░░░▓░
            ▓░░░▓░░░▓░
            ▓░░░▓░░░▓░
            ▓░░░▓░░▓▓▓
        """.trimIndent())
    }

    @Test
    fun `can find minimum solution`() {
        val stars = Day10.toStars(resourcePath("/2018/day10-test.txt"))
        val (minimum, t) = Day10.findMinimum(stars)
        val s = Day10.displayStars(minimum).trim()
        assertThat(s).isEqualTo("""
            ▓░░░▓░░▓▓▓
            ▓░░░▓░░░▓░
            ▓░░░▓░░░▓░
            ▓▓▓▓▓░░░▓░
            ▓░░░▓░░░▓░
            ▓░░░▓░░░▓░
            ▓░░░▓░░░▓░
            ▓░░░▓░░▓▓▓
        """.trimIndent())
        assertThat(t).isEqualTo(3)
    }
}