package net.fish.y2022

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day17Test {
    @Test
    fun `can do part 1`() {
        val chamberSimulator = Day17.ChamberSimulator(7, mutableSetOf(), Day17.toDirections(resourcePath("/2022/day17-test.txt").first()))
        chamberSimulator.step(2022)
        assertThat(chamberSimulator.height()).isEqualTo(3068)
    }

    @Test
    fun `can do part 2`() {
        val chamberSimulator = Day17.ChamberSimulator(7, mutableSetOf(), Day17.toDirections(resourcePath("/2022/day17-test.txt").first()))
        chamberSimulator.step(1000000000000L)
        assertThat(chamberSimulator.height()).isEqualTo(3068)
    }

    @Test
    fun `have correct shape points`() {
        assertThat(Day17Shapes.shapes[0]).containsExactlyInAnyOrder(Point(x = 0, y = 0), Point(x = 1, y = 0), Point(x = 2, y = 0), Point(x = 3, y = 0))
        assertThat(Day17Shapes.shapes[1]).containsExactlyInAnyOrder(Point(x = 1, y = 0), Point(x = 0, y = 1), Point(x = 1, y = 1), Point(x = 2, y = 1), Point(x = 1, y = 2))
        assertThat(Day17Shapes.shapes[2]).containsExactlyInAnyOrder(Point(x = 2, y = 2), Point(x = 2, y = 1), Point(x = 0, y = 0), Point(x = 1, y = 0), Point(x = 2, y = 0))
        assertThat(Day17Shapes.shapes[3]).containsExactlyInAnyOrder(Point(x = 0, y = 0), Point(x = 0, y = 1), Point(x = 0, y = 2), Point(x = 0, y = 3))
        assertThat(Day17Shapes.shapes[4]).containsExactlyInAnyOrder(Point(x = 0, y = 0), Point(x = 1, y = 0), Point(x = 0, y = 1), Point(x = 1, y = 1))
    }

    @Test
    fun `can read jet directions`() {
        assertThat(Day17.toDirections(">")).containsExactly(Day17.JetDirection.RIGHT)
        assertThat(Day17.toDirections("<")).containsExactly(Day17.JetDirection.LEFT)
        assertThat(Day17.toDirections("<>")).containsExactly(Day17.JetDirection.LEFT, Day17.JetDirection.RIGHT)
        assertThat(Day17.toDirections("><")).containsExactly(Day17.JetDirection.RIGHT, Day17.JetDirection.LEFT)
        assertThat(Day17.toDirections("><<")).containsExactly(Day17.JetDirection.RIGHT, Day17.JetDirection.LEFT, Day17.JetDirection.LEFT)
    }

    @Test
    fun `can simulate rock fall`() {
        val chamberSimulator = Day17.ChamberSimulator(7, mutableSetOf(), Day17.toDirections(resourcePath("/2022/day17-test.txt").first()))
        chamberSimulator.step()
        assertThat(chamberSimulator.points).containsExactlyInAnyOrder(Point(2, 0), Point(3, 0), Point(4, 0), Point(5, 0))
        chamberSimulator.step()
        assertThat(chamberSimulator.grid()).containsExactly(
            "...#...",
            "..###..",
            "...#...",
            "..####."
        )
        chamberSimulator.step(8)
        assertThat(chamberSimulator.grid()).containsExactly(
            "....#..",
            "....#..",
            "....##.",
            "##..##.",
            "######.",
            ".###...",
            "..#....",
            ".####..",
            "....##.",
            "....##.",
            "....#..",
            "..#.#..",
            "..#.#..",
            "#####..",
            "..###..",
            "...#...",
            "..####."
        )
    }
}