package net.fish.y2023

import net.fish.geometry.Direction
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day14Test {
    @Test
    fun `can parse panel2`() {
        val data = resourcePath("/2023/day14-test.txt")
        val panel = Day14.createPanel2(data)
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
    fun `can move panel2 north`() {
        val data = resourcePath("/2023/day14-test.txt")
        val panel = Day14.createPanel2(data)
        panel.move(Direction.NORTH)
        assertThat(panel.toString()).isEqualTo("""
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
    fun `can move panel2 west`() {
        val data = """
            ...
            O..
            .O.
            ..O
            #O.
            #OO
            #.O
            #..
            .#O
            .#.
            O#.
            ..#
            O.#
            .O#
            OO#
            O.O
            ..O
            .OO
            OOO
        """.trimIndent()
        val panel = Day14.createPanel2(data.split("\n"))
        panel.move(Direction.WEST)
        assertThat(panel.toString()).isEqualTo("""
            ...
            O..
            O..
            O..
            #O.
            #OO
            #O.
            #..
            .#O
            .#.
            O#.
            ..#
            O.#
            O.#
            OO#
            OO.
            O..
            OO.
            OOO
        """.trimIndent())
    }

    @Test
    fun `can move panel2 south`() {
        val data = """
            O..OO.O.O..####..O.
            OO..OO..###..OO.O..
            OOOO####..O.OO.O...
        """.trimIndent()
        val panel = Day14.createPanel2(data.split("\n"))
        panel.move(Direction.SOUTH)
        assertThat(panel.toString()).isEqualTo("""
            O...O...O..####....
            OO.OOOO.###..O.....
            OOOO####..O.OOOOOO.
        """.trimIndent())
    }

    @Test
    fun `can move panel2 east`() {
        val data = """
            ...
            O..
            .O.
            ..O
            #O.
            #OO
            #.O
            #..
            .#O
            .#.
            O#.
            ..#
            O.#
            .O#
            OO#
            O.O
            ..O
            .OO
            OOO
        """.trimIndent()
        val panel = Day14.createPanel2(data.split("\n"))
        panel.move(Direction.EAST)
        assertThat(panel.toString()).isEqualTo("""
            ...
            ..O
            ..O
            ..O
            #.O
            #OO
            #.O
            #..
            .#O
            .#.
            O#.
            ..#
            .O#
            .O#
            OO#
            .OO
            ..O
            .OO
            OOO
        """.trimIndent())
    }

    @Test
    fun `can spin panel2`() {
        val data = resourcePath("/2023/day14-test.txt")
        val panel = Day14.createPanel2(data)
        panel.spin()
        assertThat(panel.toString()).isEqualTo("""
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

        panel.spin()
        assertThat(panel.toString()).isEqualTo("""
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

        panel.spin()
        assertThat(panel.toString()).isEqualTo("""
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
    fun `can find cycle time`() {
        val data = resourcePath("/2023/day14-test.txt")
        val panel = Day14.createPanel2(data)
        val startAndCycle = panel.findPanelCycle()
        assertThat(startAndCycle).isEqualTo(Pair(3, 7))
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