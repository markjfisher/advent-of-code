package net.fish.y2020

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day01Test {
    @Test
    fun `should calculate expense part 1`() {
        val receipts = listOf(1721, 979, 366, 299, 675, 1456)
        assertThat(Day01.findMatchingExpensesMultiplied(receipts, 2)).isEqualTo(setOf(514579))
    }
}