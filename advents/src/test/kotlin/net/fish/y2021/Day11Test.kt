package net.fish.y2021

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day11Test {
    private val testData = resourcePath("/2021/day11-test.txt")

    @Test
    fun `test data simulates as expected`() {
        val simulator = DumboOctopusSimulator(testData)
        val sim10Score = simulator.doSteps(10)
        assertThat(sim10Score).isEqualTo(204)

        // do another 90 steps, add its score to first 10
        val sim100Score = simulator.doSteps(90)
        assertThat(sim100Score + 204).isEqualTo(1656)
    }

    @Test
    fun `find sync point of flashing`() {
        val simulator = DumboOctopusSimulator(testData)
        val syncStep = simulator.findSync()
        assertThat(syncStep).isEqualTo(195)
    }
}