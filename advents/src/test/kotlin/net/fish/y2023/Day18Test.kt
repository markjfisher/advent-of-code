package net.fish.y2023

import net.fish.geometry.Direction.Companion.from
import net.fish.geometry.Point
import net.fish.resourcePath
import net.fish.y2023.Day18.DigInstruction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day18Test {

    @Test
    fun `can parse input`() {
        val data = resourcePath("/2023/day18-test.txt")
        val digSite = Day18.toDigSite(data)
        assertThat(digSite).isEqualTo(Day18.DigSite(
            instructions = listOf(
                DigInstruction(from('R'), 6, "70c710"),
                DigInstruction(from('D'), 5, "0dc571"),
                DigInstruction(from('L'), 2, "5713f0"),
                DigInstruction(from('D'), 2, "d2c081"),
                DigInstruction(from('R'), 2, "59c680"),
                DigInstruction(from('D'), 2, "411b91"),
                DigInstruction(from('L'), 5, "8ceee2"),
                DigInstruction(from('U'), 2, "caa173"),
                DigInstruction(from('L'), 1, "1b58a2"),
                DigInstruction(from('U'), 2, "caa171"),
                DigInstruction(from('R'), 2, "7807d2"),
                DigInstruction(from('U'), 3, "a77fa3"),
                DigInstruction(from('L'), 2, "015232"),
                DigInstruction(from('U'), 2, "7a21e3"),
            )

        ))
    }

    @Test
    fun `can find edge`() {
        val data = resourcePath("/2023/day18-test.txt")
        val digSite = Day18.toDigSite(data)
        val perimeter = digSite.perimeterLength()
        assertThat(perimeter).isEqualTo(38)
    }

    @Test
    fun `can make vertices`() {
        val data = resourcePath("/2023/day18-test.txt")
        val digSite = Day18.toDigSite(data)
        val vertices = digSite.makeVertices()
        assertThat(vertices).containsExactly(
            Point(x=0, y=0),
            Point(x=6, y=0),
            Point(x=6, y=5),
            Point(x=4, y=5),
            Point(x=4, y=7),
            Point(x=6, y=7),
            Point(x=6, y=9),
            Point(x=1, y=9),
            Point(x=1, y=7),
            Point(x=0, y=7),
            Point(x=0, y=5),
            Point(x=2, y=5),
            Point(x=2, y=2),
            Point(x=0, y=2),
            Point(x=0, y=0)
        )

    }

    @Test
    fun `can find area in polygon`() {
        val data = resourcePath("/2023/day18-test.txt")
        val digSite = Day18.toDigSite(data)
        val vertices = digSite.makeVertices()
        assertThat(digSite.shoelace(vertices)).isEqualTo(42L)
        val answer = digSite.coveredSquareCount()
        assertThat(answer).isEqualTo(62)
    }

    @Test
    fun `can find simple area`() {
        val data = """
            R 2 (#70c710)
            D 2 (#0dc571)
            L 2 (#5713f0)
            U 2 (#d2c081)
        """.trimIndent().split("\n")
        val digSite = Day18.toDigSite(data)
        val vertices = digSite.makeVertices()
        val shoeLace = digSite.shoelace(vertices)
        val squaresCovered = digSite.coveredSquareCount()
        // println("$vertices\n$shoeLace\n$answer")
        assertThat(shoeLace).isEqualTo(4L)
        assertThat(squaresCovered).isEqualTo(9L)
    }

    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day18-test.txt")
        val v = Day18.doPart1(data)
        assertThat(v).isEqualTo(62)
    }
    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day18-test.txt")
        val v = Day18.doPart2(data)
        assertThat(v).isEqualTo(952408144115L)
    }
}