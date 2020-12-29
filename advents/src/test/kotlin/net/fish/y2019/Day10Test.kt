package net.fish.y2019

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day10Test {
    @Test
    fun `visible count on maps`() {
        val map1 = resourcePath("/2019/day10-test1.txt").map { it.toCharArray().toList() }
        assertThat(Day10.highestVisiblePointAndCount(map1)).isEqualTo(Pair(Point(5, 8), 33))

        val map2 = resourcePath("/2019/day10-test2.txt").map { it.toCharArray().toList() }
        assertThat(Day10.highestVisiblePointAndCount(map2)).isEqualTo(Pair(Point(1, 2), 35))

        val map3 = resourcePath("/2019/day10-test3.txt").map { it.toCharArray().toList() }
        assertThat(Day10.highestVisiblePointAndCount(map3)).isEqualTo(Pair(Point(6, 3), 41))

        val map4 = resourcePath("/2019/day10-test4.txt").map { it.toCharArray().toList() }
        assertThat(Day10.highestVisiblePointAndCount(map4)).isEqualTo(Pair(Point(11, 13), 210))
    }
}