package net.fish.y2022

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day09Test {
    //resourcePath("/2022/day09-test.txt")

    @Test
    fun `can do part 1`() {
        assertThat(Day09.doPart1(Day09.toMovement(resourcePath("/2022/day09-test.txt")))).isEqualTo(13)
    }

    @Test
    fun `can do part 2`() {
        assertThat(Day09.doPart2(Day09.toMovement(resourcePath("/2022/day09b-test.txt")))).isEqualTo(36)
    }
}