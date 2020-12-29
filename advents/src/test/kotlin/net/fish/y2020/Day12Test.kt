package net.fish.y2020

import net.fish.geometry.manhattenDistance
import net.fish.resourcePath
import net.fish.y2020.Day12.toPathP1
import net.fish.y2020.Day12.toPathP2
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day12Test {
    private val instructions = resourcePath("/2020/day12-test-01.txt")

    @Test
    fun `paths are correct for ferry instructions`() {
        // move forward (east) 1
        assertThat(toPathP1(listOf("F1"))).containsExactly(Pair(0, 0), Pair(1, 0))

        // move forward (east) 2
        assertThat(toPathP1(listOf("F2"))).containsExactly(Pair(0, 0), Pair(1, 0), Pair(2, 0))

        // turn R90 to south, move 1
        assertThat(toPathP1(listOf("R90", "F1"))).containsExactly(Pair(0, 0), Pair(0, -1))

        // turn L90 to north, move 1
        assertThat(toPathP1(listOf("L90", "F1"))).containsExactly(Pair(0, 0), Pair(0, 1))

        // turn R180 to west, move 1
        assertThat(toPathP1(listOf("R180", "F1"))).containsExactly(Pair(0, 0), Pair(-1, 0))
        // turn L180 to west, move 1
        assertThat(toPathP1(listOf("L180", "F1"))).containsExactly(Pair(0, 0), Pair(-1, 0))

        // turn R270 to north, move 1
        assertThat(toPathP1(listOf("R270", "F1"))).containsExactly(Pair(0, 0), Pair(0, 1))
        // turn L270 to south, move 1
        assertThat(toPathP1(listOf("L270", "F1"))).containsExactly(Pair(0, 0), Pair(0, -1))

        // move north 1
        assertThat(toPathP1(listOf("N1"))).containsExactly(Pair(0, 0), Pair(0, 1))
        // move east 1
        assertThat(toPathP1(listOf("E1"))).containsExactly(Pair(0, 0), Pair(1, 0))
        // move south 1
        assertThat(toPathP1(listOf("S1"))).containsExactly(Pair(0, 0), Pair(0, -1))
        // move west 1
        assertThat(toPathP1(listOf("W1"))).containsExactly(Pair(0, 0), Pair(-1, 0))

        // move north 2
        assertThat(toPathP1(listOf("N2"))).containsExactly(Pair(0, 0), Pair(0, 1), Pair(0, 2))
        // move east 2
        assertThat(toPathP1(listOf("E2"))).containsExactly(Pair(0, 0), Pair(1, 0), Pair(2, 0))
        // move south 2
        assertThat(toPathP1(listOf("S2"))).containsExactly(Pair(0, 0), Pair(0, -1), Pair(0, -2))
        // move west 2
        assertThat(toPathP1(listOf("W2"))).containsExactly(Pair(0, 0), Pair(-1, 0), Pair(-2, 0))
    }

    @Test
    fun `p1 solution`() {
        assertThat(manhattenDistance(toPathP1(instructions).last())).isEqualTo(25)
    }

    @Test
    fun `p2 solution`() {
        assertThat(manhattenDistance(toPathP2(instructions).last())).isEqualTo(286)
    }
}