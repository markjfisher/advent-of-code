package net.fish.y2020

import net.fish.resourcePath
import net.fish.resourceStrings
import net.fish.y2020.HEX_DIRECTION.E
import net.fish.y2020.HEX_DIRECTION.NE
import net.fish.y2020.HEX_DIRECTION.NW
import net.fish.y2020.HEX_DIRECTION.SE
import net.fish.y2020.HEX_DIRECTION.SW
import net.fish.y2020.HEX_DIRECTION.W
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day24Test {
    private val data = resourcePath("/2020/day24-test.txt")
    @Test
    fun `can walk around hex`() {
        assertThat(Day24.toHexDirections("esenee")).containsExactly(E, SE, NE, E)
        assertThat(Day24.toHexDirections("nwwswee")).containsExactly(NW, W, SW, E, E)
    }

    @Test
    fun `hex walk in locations`() {
        assertThat(Day24.toHexWalks(listOf("esenee")).first()).containsExactly(Pair(0, 0), Pair(2, 0), Pair(3, -1), Pair(4, 0), Pair(6, 0))
        assertThat(Day24.toHexWalks(listOf("nwwswee")).first()).containsExactly(Pair(0, 0), Pair(-1, 1), Pair(-3, 1), Pair(-4, 0), Pair(-2, 0), Pair(0, 0))
    }

    @Test
    fun `part 1 test data`() {
        assertThat(Day24.doPart1(data)).isEqualTo(10)
    }
}