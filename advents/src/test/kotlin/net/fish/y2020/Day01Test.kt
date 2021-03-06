package net.fish.y2020

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day01Test {
    private val receipts = listOf(1721, 979, 366, 299, 675, 1456)

    @Test
    fun `should find pairs that add to 2020 and multiply the pairs values`() {
        assertThat(Day01.doPart1(receipts)).isEqualTo(514579)
    }

    @Test
    fun `should find triples that add to 2020 and multiply the sets of values`() {
        assertThat(Day01.doPart2(receipts)).isEqualTo(241861950)
    }
}