package net.fish.y2021

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DumboOctopusSimulatorTest {
    private val testData = resourcePath("/2021/day11-test.txt")
    @Test
    fun `can create simulator`() {
        val simulator = DumboOctopusSimulator(testData)
        assertThat(simulator.grid.width).isEqualTo(10)
        assertThat(simulator.grid.height).isEqualTo(10)

        val gridValues = simulator.gridValues()
        assertThat(gridValues).containsExactly(
            "5483143223",
            "2745854711",
            "5264556173",
            "6141336146",
            "6357385478",
            "4167524645",
            "2176841721",
            "6882881134",
            "4846848554",
            "5283751526"
        )
    }

    @Test
    fun `can step simulator`() {
        val simpleData = listOf(
            "11111",
            "19991",
            "19191",
            "19991",
            "11111"
        )
        val simulator = DumboOctopusSimulator(simpleData)
        assertThat(simulator.gridValues()).containsExactly(
            "11111",
            "19991",
            "19191",
            "19991",
            "11111"
        )

        var score = simulator.engine.step()
        assertThat(score).isEqualTo(9)

        var gridValues = simulator.gridValues()
        assertThat(gridValues).containsExactly(
            "34543",
            "40004",
            "50005",
            "40004",
            "34543"
        )

        score = simulator.engine.step()
        assertThat(score).isEqualTo(0)

        gridValues = simulator.gridValues()
        assertThat(gridValues).containsExactly(
            "45654",
            "51115",
            "61116",
            "51115",
            "45654"
        )
    }
}