package net.fish.y2023

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.joml.Vector3d
import org.joml.Vector3i
import org.junit.jupiter.api.Test

class Day24Test {
    @Test
    fun `can parse test data`() {
        val data = resourcePath("/2023/day24-test.txt")
        val sim = Day24.toHailstoneSimulator(data)
        assertThat(sim.hailstones).containsExactly(
            Pair(Vector3d(19.0, 13.0, 30.0), Vector3d(-2.0, 1.0, -2.0)),
            Pair(Vector3d(18.0, 19.0, 22.0), Vector3d(-1.0, -1.0, -2.0)),
            Pair(Vector3d(20.0, 25.0, 34.0), Vector3d(-2.0, -2.0, -4.0)),
            Pair(Vector3d(12.0, 31.0, 28.0), Vector3d(-1.0, -2.0, -1.0)),
            Pair(Vector3d(20.0, 19.0, 15.0), Vector3d(1.0, -5.0, -3.0)),
        )
    }

    @Test
    fun `testing data`() {
        val data = resourcePath("/2023/day24-test2.txt")
        val sim = Day24.toHailstoneSimulator(data)
        println(sim.hailstones)
    }

    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day24-test.txt")
        val v = Day24.doPart1(data, 7L, 27L)
        assertThat(v).isEqualTo(2)
    }
    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day24-test.txt")
        val v = Day24.doPart2(data)
        assertThat(v).isEqualTo(0)
    }

}