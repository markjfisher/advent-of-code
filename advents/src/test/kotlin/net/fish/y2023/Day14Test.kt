package net.fish.y2023

import net.fish.geometry.Direction
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day14Test {
    @Test
    fun `can parse panel`() {
        val data = resourcePath("/2023/day14-test.txt")
        val panel = Day14.createPanel(data)
        assertThat(panel.toString()).isEqualTo("""
            O....#....
            O.OO#....#
            .....##...
            OO.#O....O
            .O.....O#.
            O.#..O.#.#
            ..O..#O..O
            .......O..
            #....###..
            #OO..#....
        """.trimIndent())
    }

    @Test
    fun `can move panel north`() {
        val data = resourcePath("/2023/day14-test.txt")
        val panel = Day14.createPanel(data)
        val newPanel = panel.move(Direction.NORTH)
        assertThat(newPanel.toString()).isEqualTo("""
            OOOO.#.O..
            OO..#....#
            OO..O##..O
            O..#.OO...
            ........#.
            ..#....#.#
            ..O..#.O.O
            ..O.......
            #....###..
            #....#....
        """.trimIndent())
    }

    @Test
    fun `can spin panel`() {
        val data = resourcePath("/2023/day14-test.txt")
        val panel = Day14.createPanel(data)
        val p1 = panel.spin(1)
        assertThat(p1.toString()).isEqualTo("""
            .....#....
            ....#...O#
            ...OO##...
            .OO#......
            .....OOO#.
            .O#...O#.#
            ....O#....
            ......OOOO
            #...O###..
            #..OO#....
        """.trimIndent())

        val p2 = p1.spin(1)
        assertThat(p2.toString()).isEqualTo("""
            .....#....
            ....#...O#
            .....##...
            ..O#......
            .....OOO#.
            .O#...O#.#
            ....O#...O
            .......OOO
            #..OO###..
            #.OOO#...O
        """.trimIndent())

        val p3 = p2.spin(1)
        assertThat(p3.toString()).isEqualTo("""
            .....#....
            ....#...O#
            .....##...
            ..O#......
            .....OOO#.
            .O#...O#.#
            ....O#...O
            .......OOO
            #...O###.O
            #.OOO#...O
        """.trimIndent())
    }

    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day14-test.txt")
        val v = Day14.doPart1(data)
        assertThat(v).isEqualTo(136)
    }

    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day14-test.txt")
        val v = Day14.doPart2(data)
        assertThat(v).isEqualTo(64)
    }

}