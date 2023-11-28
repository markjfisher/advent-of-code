package net.fish.y2018

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day03Test {
    @Test
    fun `can generate all points in claim`() {
        val c1 = Day03.Claim(1, 0, 0, 2, 3)
        assertThat(c1.allPoints()).containsExactly(
            Point(0, 0), Point(0, 1), Point(0, 2),
            Point(1, 0), Point(1, 1), Point(1, 2)
        )

        val c2 = Day03.Claim(2, 2,3, 1, 1)
        assertThat(c2.allPoints()).containsExactly(
            Point(2, 3)
        )
    }

    @Test
    fun `can do part 1 on test data`() {
        val claims = Day03.toClaims(resourcePath("/2018/day03-test.txt"))
        val answer =  Day03.doPart1(claims)
        assertThat(answer).isEqualTo(4)
    }

    @Test
    fun `can do part 2 on test data`() {
        val claims = Day03.toClaims(resourcePath("/2018/day03-test.txt"))
        val answer =  Day03.doPart2(claims)
        assertThat(answer).isEqualTo(3)
    }

}