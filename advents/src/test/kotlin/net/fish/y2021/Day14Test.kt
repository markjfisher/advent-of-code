package net.fish.y2021

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day14Test {
    private val testData = resourcePath("/2021/day14-test.txt")
    @Test
    fun `can read data`() {
        val templateInstructions = Day14.parseInput(testData)
        assertThat(templateInstructions.template).isEqualTo("NNCB")
        assertThat(templateInstructions.insertions).containsAllEntriesOf(
            mapOf(
                "CH" to listOf("CB", "BH"),
                "HH" to listOf("HN", "NH"),
                "CB" to listOf("CH", "HB"),
                "NH" to listOf("NC", "CH"),
                "HB" to listOf("HC", "CB"),
                "HC" to listOf("HB", "BC"),
                "HN" to listOf("HC", "CN"),
                "NN" to listOf("NC", "CN"),
                "BH" to listOf("BH", "HH"),
                "NC" to listOf("NB", "BC"),
                "NB" to listOf("NB", "BB"),
                "BN" to listOf("BB", "BN"),
                "BB" to listOf("BN", "NB"),
                "BC" to listOf("BB", "BC"),
                "CC" to listOf("CN", "NC"),
                "CN" to listOf("CC", "CN")
            )
        )
    }

    @Test
    fun `can do grow test data`() {
        val templateInstructions = Day14.parseInput(testData)
        val grow1 = Day14.grow(1, templateInstructions)
        assertThat(grow1['B']).isEqualTo(2)
        assertThat(grow1['C']).isEqualTo(2)
        assertThat(grow1['H']).isEqualTo(1)
        assertThat(grow1['N']).isEqualTo(2)

        val grow2 = Day14.grow(2, templateInstructions)
        assertThat(grow2['B']).isEqualTo(6)
        assertThat(grow2['C']).isEqualTo(4)
        assertThat(grow2['H']).isEqualTo(1)
        assertThat(grow2['N']).isEqualTo(2)

        val grow10 = Day14.grow(10, templateInstructions)
        assertThat(grow10['B']).isEqualTo(1749)
        assertThat(grow10['C']).isEqualTo(298)
        assertThat(grow10['H']).isEqualTo(161)
        assertThat(grow10['N']).isEqualTo(865)

        val grow40 = Day14.grow(40, templateInstructions)
        assertThat(grow40['B']).isEqualTo(2192039569602L)
        assertThat(grow40['C']).isEqualTo(6597635301L)
        assertThat(grow40['H']).isEqualTo(3849876073L)
        assertThat(grow40['N']).isEqualTo(1096047802353L)
    }

    @Test
    fun `can do part 1 with test data`() {
        assertThat(Day14.doPart1(testData)).isEqualTo(1588L)
    }

    @Test
    fun `can do part 2 with test data`() {
        assertThat(Day14.doPart2(testData)).isEqualTo(2188189693529L)
    }
}