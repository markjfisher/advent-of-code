package net.fish.y2023

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day21Test {
    @Test
    fun `can find all points within X`() {
        val data = resourcePath("/2023/day21-test2.txt")
        val (points, start) = Day21.toWalkableGrid(data)
        // takes 4s to compute.
//        val every = Day21.countEvery(points, start, 65, 3, data.size)
//        assertThat(every).containsExactly(3791, 33646, 93223)
        val every = Day21.countEvery(points, start, 65, 1, data.size)
        assertThat(every).containsExactly(3791)
    }

    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day21-test.txt")
        val v = Day21.doPart1(data, 6)
        assertThat(v).isEqualTo(16)
    }

}