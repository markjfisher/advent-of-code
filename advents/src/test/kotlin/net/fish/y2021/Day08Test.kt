package net.fish.y2021

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day08Test {
    private val raw = resourcePath("/2021/day08-test.txt")
    private val raw2 = resourcePath("/2021/day08-test-2.txt")

    @Test
    fun `should do part 2`() {
        assertThat(Day08.doPart2(raw)).isEqualTo(5353)

        assertThat(Day08.doPart2(raw2)).isEqualTo(61229)
    }
}