package net.fish.collections

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListsTests {
    @Test
    fun `permutations test`() {
        assertThat(listOf(1,2,3).permutations()).containsExactlyInAnyOrder(
            listOf(1,2,3), listOf(1,3,2),
            listOf(2,1,3), listOf(2,3,1),
            listOf(3,1,2), listOf(3,2,1)
        )
    }
}