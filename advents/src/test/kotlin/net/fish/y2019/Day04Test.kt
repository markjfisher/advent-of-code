package net.fish.y2019

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day04Test {
    @Test
    fun `convert to list of digits`() {
        assertThat(Day04.convertNumberToListOfDigits(123456)).containsExactly(1, 2, 3, 4, 5, 6)
        assertThat(Day04.convertNumberToListOfDigits(987654)).containsExactly(9, 8, 7, 6, 5, 4)
    }

    @Test
    fun `at least one adjacent number`() {
        assertThat(Day04.hasAdjacentDigits(123456)).isFalse()
        assertThat(Day04.hasAdjacentDigits(122345)).isTrue()
        assertThat(Day04.hasAdjacentDigits(122245)).isTrue()
        assertThat(Day04.hasAdjacentDigits(122334)).isTrue()
    }

    @Test
    fun `at least one adjacent number with only 2 numbers`() {
        assertThat(Day04.hasAtLeastOnePairOfAdjacentDigits(123456)).isFalse()
        assertThat(Day04.hasAtLeastOnePairOfAdjacentDigits(122345)).isTrue()
        assertThat(Day04.hasAtLeastOnePairOfAdjacentDigits(122245)).isFalse()
        assertThat(Day04.hasAtLeastOnePairOfAdjacentDigits(122334)).isTrue()
        assertThat(Day04.hasAtLeastOnePairOfAdjacentDigits(222334)).isTrue()
    }

    @Test
    fun `monotonically increasing numbers`() {
        assertThat(Day04.allNumbersMonotonicallyIncrease(111111)).isTrue()
        assertThat(Day04.allNumbersMonotonicallyIncrease(211111)).isFalse()
        assertThat(Day04.allNumbersMonotonicallyIncrease(112233)).isTrue()
        assertThat(Day04.allNumbersMonotonicallyIncrease(123450)).isFalse()
    }

    @Test
    fun `run lengths`() {
        val underTest = Day04
        assertThat(underTest.runLengths(underTest.convertNumberToListOfDigits(123456))).containsExactly(1, 1, 1, 1, 1, 1)
        assertThat(underTest.runLengths(underTest.convertNumberToListOfDigits(122456))).containsExactly(1, 2, 1, 1, 1)
        assertThat(underTest.runLengths(underTest.convertNumberToListOfDigits(122336))).containsExactly(1, 2, 2, 1)
        assertThat(underTest.runLengths(underTest.convertNumberToListOfDigits(122344))).containsExactly(1, 2, 1, 2)
        assertThat(underTest.runLengths(underTest.convertNumberToListOfDigits(111111))).containsExactly(6)
    }
}