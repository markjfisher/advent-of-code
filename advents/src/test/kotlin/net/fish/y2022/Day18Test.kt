package net.fish.y2022

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day18Test {
    @Test
    fun `can do part 1`() {
        assertThat(Day18.solve(listOf("1,1,1", "2,1,1"))).isEqualTo(Pair(10, 10))
        assertThat(Day18.solve(resourcePath("/2022/day18-test.txt"))).isEqualTo(Pair(64, 58))
    }
}