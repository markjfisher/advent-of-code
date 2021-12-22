package net.fish.y2021

import net.fish.resourcePath
import net.fish.resourceStrings
import net.fish.y2021.Day22.Cuboid
import org.assertj.core.api.Assertions.assertThat
import org.joml.Vector3i
import org.junit.jupiter.api.Test

class Day22Test {
    val testData = resourcePath("/2021/day22-test.txt")

    @Test
    fun `can extract cuboid lines`() {
        val lines = Day22.toCuboidLines(listOf("on x=-10..12,y=-15..-12,z=10..12", "off x=9..11,y=9..11,z=9..11"))
        assertThat(lines).hasSize(2)
        assertThat(lines[0]).isEqualTo(Day22.CuboidLine(true, Cuboid(Vector3i(-10, -15, 10), Vector3i(12, -12, 12))))
        assertThat(lines[1]).isEqualTo(Day22.CuboidLine(false, Cuboid(Vector3i(9, 9, 9), Vector3i(11, 11, 11))))
    }

    @Test
    fun `can convert test input to result`() {
        val cuboidLines = Day22.toCuboidLines(testData)
        val cuboids = Day22.applyCuboidLinesInArea(cuboidLines, 50)
        assertThat(cuboids).hasSize(590784)
    }

    @Test
    fun `can do part1`() {
        val cuboidLines = Day22.toCuboidLines(testData)
        assertThat(Day22.doPart1(cuboidLines)).isEqualTo(590784)
    }

    @Test
    fun `can do part1 part 2 data`() {
        val cuboidLines = Day22.toCuboidLines(resourcePath("/2021/day22-2-test.txt"))
        assertThat(Day22.doPart1(cuboidLines)).isEqualTo(474140)
    }

    @Test
    fun `can do simple data`() {
        val allLines = """
            on x=10..12,y=10..12,z=10..12
            on x=11..13,y=11..13,z=11..13
            off x=9..11,y=9..11,z=9..11
            on x=10..10,y=10..10,z=10..10
        """.trimIndent().lines()

        assertThat(Day22.process(Day22.toCuboidLines(listOf(allLines[0])))).hasSize(27)
        assertThat(Day22.process(Day22.toCuboidLines(listOf(allLines[0], allLines[1])))).hasSize(27 + 19)
        assertThat(Day22.process(Day22.toCuboidLines(listOf(allLines[0], allLines[1], allLines[2])))).hasSize(27 + 19 - 8)
        assertThat(Day22.process(Day22.toCuboidLines(allLines))).hasSize(27 + 19 - 8 + 1)
    }
}