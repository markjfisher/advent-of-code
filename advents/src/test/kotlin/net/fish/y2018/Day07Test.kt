package net.fish.y2018

import net.fish.collections.sortedMutableListOf
import net.fish.resourcePath
import net.fish.y2018.Day07.Step
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day07Test {
    @Test
    fun `can parse steps`() {
        val stepData = Day07.toSteps(resourcePath("/2018/day07-test.txt"))
        assertThat(stepData).containsExactlyInAnyOrderEntriesOf(
            mapOf(
                'A' to Step('A', sortedMutableListOf('C'), sortedMutableListOf('B', 'D')),
                'B' to Step('B', sortedMutableListOf('A'), sortedMutableListOf('E')),
                'C' to Step('C', sortedMutableListOf(), sortedMutableListOf('A', 'F')),
                'D' to Step('D', sortedMutableListOf('A'), sortedMutableListOf('E')),
                'E' to Step('E', sortedMutableListOf('B', 'D', 'F'), sortedMutableListOf()),
                'F' to Step('F', sortedMutableListOf('C'), sortedMutableListOf('E'))
            )
        )
    }

    @Test
    fun `step available checks if there are any required steps before this can be used`() {
        val s1 = Step('A', sortedMutableListOf('B'), sortedMutableListOf())
        assertThat(s1.available()).isFalse
        val s2 = Step('B', sortedMutableListOf(), sortedMutableListOf('C'))
        assertThat(s2.available()).isTrue
    }

    @Test
    fun `can generate correct order of operations`() {
        val stepData = Day07.toSteps(resourcePath("/2018/day07-test.txt"))
        assertThat(Day07.generateStepOrder(stepData)).isEqualTo("CABDFE")
    }

    @Test
    fun `can only take available from a list`() {
        val myList = listOf(1, 2, 3)
        assertThat(myList.take(5)).containsExactly(1, 2, 3)
    }

    @Test
    fun `can run parallel workers`() {
        val stepData = Day07.toSteps(resourcePath("/2018/day07-test.txt"))
        assertThat(Day07.parallelWorkers(stepData, 2, 0)).isEqualTo(15)
    }
}