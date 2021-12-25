package net.fish.y2021

import net.fish.geometry.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GridDataUtilsTest {
    @Test
    fun `can convert input to map of points to ints`() {
        val input = """
            123
            456
        """.trimIndent()

        val map = GridDataUtils.mapIntPointsFromLines(input.lines())
        assertThat(map).containsExactlyEntriesOf(
            mapOf(
                Point(0, 0) to 1, Point(1, 0) to 2, Point(2, 0) to 3,
                Point(0, 1) to 4, Point(1, 1) to 5, Point(2, 1) to 6
            )
        )
    }

    @Test
    fun `can convert input to map of points to chars`() {
        val input = """
            .x.
            y.z
        """.trimIndent()

        val map = GridDataUtils.mapCharPointsFromLines(input.lines())
        assertThat(map).containsExactlyEntriesOf(
            mapOf(
                Point(0, 0) to '.', Point(1, 0) to 'x', Point(2, 0) to '.',
                Point(0, 1) to 'y', Point(1, 1) to '.', Point(2, 1) to 'z'
            )
        )
    }
}