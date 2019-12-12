package net.fish

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day3BTest {
    @Test
    fun `stepsTo counts correctly`() {
        assertThat(
            Day3B().stepsTo(
                Pair(0, 0),
                listOf(Pair(1, 0), Pair(1, 1), Pair(2, 1), Pair(2, 2), Pair(3, 2))
            )
        ).isEqualTo(0) // not found
        assertThat(
            Day3B().stepsTo(
                Pair(2, 2),
                listOf(Pair(1, 0), Pair(1, 1), Pair(2, 1), Pair(2, 2), Pair(3, 2))
            )
        ).isEqualTo(4)
    }

    @Test
    fun `calculating minimum steps`() {
        assertThat(
            Day3B().calculateMinimumSignalDelay(
                Pair(
                    listOf("R8", "U5", "L5", "D3"),
                    listOf("U7", "R6", "D4", "L4")
                )
            )
        ).isEqualTo(30)

        assertThat(
            Day3B().calculateMinimumSignalDelay(
                Pair(
                    listOf("R75", "D30", "R83", "U83", "L12", "D49", "R71", "U7", "L72"),
                    listOf("U62", "R66", "U55", "R34", "D71", "R55", "D58", "R83")
                )
            )
        ).isEqualTo(610)

        println("---------------")
        assertThat(
            Day3B().calculateMinimumSignalDelay(
                Pair(
                    listOf("R98", "U47", "R26", "D63", "R33", "U87", "L62", "D20", "R33", "U53", "R51"),
                    listOf("U98", "R91", "D20", "R16", "D67", "R40", "U7", "R15", "U6", "R7")
                )
            )
        ).isEqualTo(410)
    }

}