package net.fish.y2018

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day05Test {
    @Test
    fun `can reduce strings`() {
        // dabAcCaCBAcCcaDA
        assertThat(Day05.reduce("dabAcCaCBAcCcaDA")).isEqualTo("dabCBAcaDA")
    }

    @Test
    fun `can do part 2`() {
        assertThat(Day05.doPart2("dabAcCaCBAcCcaDA")).isEqualTo(4)
    }
}