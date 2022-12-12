package net.fish.geometry.square

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SquareGridTest {
    @Test
    fun `can constrain grid`() {
        val grid = SquareGrid(10, 10)
        val neighbours = grid.square(0, 0)!!.neighbours()
        assertThat(neighbours).containsExactlyInAnyOrder(
            grid.square(1, 0), grid.square(0, 1), grid.square(1, 1)
        )
    }
}