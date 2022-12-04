package net.fish.y2022

import net.fish.resourcePath
import net.fish.y2022.Day04.Assignments
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day04Test {
    val testData = resourcePath("/2022/day04-test.txt")

    @Test
    fun `can create assignments`() {
        val assignments = Day04.toAssignments(testData)
        assertThat(assignments.first()).isEqualTo(Assignments(IntRange(2,4), IntRange(6,8)))

        val a1 = Day04.toAssignments(listOf("12-80,12-81", "13-94,14-93"))
        assertThat(a1[0]).isEqualTo(Assignments(IntRange(12,80), IntRange(12,81)))
        assertThat(a1[1]).isEqualTo(Assignments(IntRange(13,94), IntRange(14,93)))
    }

    @Test
    fun `fully contains`() {
        val a1 = Assignments(IntRange(2,4), IntRange(1,5))
        assertThat(a1.fullyContains()).isTrue
        val a2 = Assignments(IntRange(1,5), IntRange(2,4))
        assertThat(a2.fullyContains()).isTrue
        val a3 = Assignments(IntRange(2,6), IntRange(1,5))
        assertThat(a3.fullyContains()).isFalse
        val a4 = Assignments(IntRange(1,5), IntRange(2,6))
        assertThat(a4.fullyContains()).isFalse
    }

    @Test
    fun `overlaps tests`() {
        val a1 = Assignments(IntRange(2,3), IntRange(4,5))
        assertThat(a1.overlaps()).isFalse
        val a2 = Assignments(IntRange(5,7), IntRange(7,9))
        assertThat(a2.overlaps()).isTrue
    }

    @Test
    fun `can do part 1`() {
        assertThat(Day04.doPart1(Day04.toAssignments(testData))).isEqualTo(2)
    }
    @Test
    fun `can do part 2`() {
        assertThat(Day04.doPart2(Day04.toAssignments(testData))).isEqualTo(4)
    }
}