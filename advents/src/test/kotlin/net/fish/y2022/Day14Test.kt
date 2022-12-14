package net.fish.y2022

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day14Test {
    @Test
    fun `can simulate sand movement`() {
        val simulator = Day14.SandSimulator(Day14.toWallPoints(resourcePath("/2022/day14-test.txt")))
        simulator.step()
        assertThat(simulator.allPoints - simulator.wall).containsExactly(Point(500, 8))
        simulator.step()
        assertThat(simulator.allPoints - simulator.wall).containsExactly(Point(500, 8), Point(499, 8))
        simulator.step()
        assertThat(simulator.allPoints - simulator.wall).containsExactly(Point(500, 8), Point(499, 8), Point(501,8))
        simulator.step()
        assertThat(simulator.allPoints - simulator.wall).containsExactly(Point(500, 8), Point(499, 8), Point(501,8), Point(500, 7))
        simulator.step()
        assertThat(simulator.allPoints - simulator.wall).containsExactly(Point(500, 8), Point(499, 8), Point(501,8), Point(500, 7), Point(498, 8))
    }

    @Test
    fun `can simulate multiple steps and draw output`() {
        val simulator = Day14.SandSimulator(Day14.toWallPoints(resourcePath("/2022/day14-test.txt")))
        simulator.step(22)
        assertThat(simulator.grid()).containsExactly(
            "..........",
            "..........",
            "......o...",
            ".....ooo..",
            "....#ooo##",
            "....#ooo#.",
            "..###ooo#.",
            "....oooo#.",
            "...ooooo#.",
            "#########."
        )
    }

    @Test
    fun `can do part 1`() {
        val count = Day14.doPart1(Day14.toWallPoints(resourcePath("/2022/day14-test.txt")))
        assertThat(count).isEqualTo(24)
    }

    @Test
    fun `can do part 2`() {
        val count = Day14.doPart2(Day14.toWallPoints(resourcePath("/2022/day14-test.txt")))
        assertThat(count).isEqualTo(93)
    }

    @Test
    fun `can build grid`() {
        val wallPoints = Day14.toWallPoints(resourcePath("/2022/day14-test.txt"))
        assertThat(wallPoints).containsExactly(
            Point(x = 498, y = 4),
            Point(x = 498, y = 5),
            Point(x = 498, y = 6),
            Point(x = 496, y = 6),
            Point(x = 497, y = 6),
            Point(x = 502, y = 4),
            Point(x = 503, y = 4),
            Point(x = 502, y = 5),
            Point(x = 502, y = 6),
            Point(x = 502, y = 7),
            Point(x = 502, y = 8),
            Point(x = 502, y = 9),
            Point(x = 494, y = 9),
            Point(x = 495, y = 9),
            Point(x = 496, y = 9),
            Point(x = 497, y = 9),
            Point(x = 498, y = 9),
            Point(x = 499, y = 9),
            Point(x = 500, y = 9),
            Point(x = 501, y = 9)
        )
    }
}