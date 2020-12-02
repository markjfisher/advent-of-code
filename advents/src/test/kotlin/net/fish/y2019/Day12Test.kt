package net.fish.y2019

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day12Test {
    @Test
    fun `run sim`() {
        val bodies = Day12.toBodies(resourcePath("/2019/day12-test1.txt"))
        Day12.runSimulation(bodies.toMutableList(), 1)
        assertThat(bodies[0].position).isEqualTo(Day12.Point3D(2, -1, 1))
        assertThat(bodies[0].velocity).isEqualTo(Day12.Point3D(3, -1, -1))
        assertThat(bodies[1].position).isEqualTo(Day12.Point3D(3, -7, -4))
        assertThat(bodies[1].velocity).isEqualTo(Day12.Point3D(1, 3, 3))
        assertThat(bodies[2].position).isEqualTo(Day12.Point3D(1, -7, 5))
        assertThat(bodies[2].velocity).isEqualTo(Day12.Point3D(-3, 1, -3))
        assertThat(bodies[3].position).isEqualTo(Day12.Point3D(2, 2, 0))
        assertThat(bodies[3].velocity).isEqualTo(Day12.Point3D(-1, -3, 1))

        Day12.runSimulation(bodies.toMutableList(), 1)
        assertThat(bodies[0].position).isEqualTo(Day12.Point3D(5, -3, -1))
        assertThat(bodies[0].velocity).isEqualTo(Day12.Point3D(3, -2, -2))
        assertThat(bodies[1].position).isEqualTo(Day12.Point3D(1, -2, 2))
        assertThat(bodies[1].velocity).isEqualTo(Day12.Point3D(-2, 5, 6))
        assertThat(bodies[2].position).isEqualTo(Day12.Point3D(1, -4, -1))
        assertThat(bodies[2].velocity).isEqualTo(Day12.Point3D(0, 3, -6))
        assertThat(bodies[3].position).isEqualTo(Day12.Point3D(1, -4, 2))
        assertThat(bodies[3].velocity).isEqualTo(Day12.Point3D(-1, -6, 2))

        Day12.runSimulation(bodies.toMutableList(), 8)
        assertThat(bodies[0].position).isEqualTo(Day12.Point3D(2, 1, -3))
        assertThat(bodies[0].velocity).isEqualTo(Day12.Point3D(-3, -2, 1))
        assertThat(bodies[1].position).isEqualTo(Day12.Point3D(1, -8, 0))
        assertThat(bodies[1].velocity).isEqualTo(Day12.Point3D(-1, 1, 3))
        assertThat(bodies[2].position).isEqualTo(Day12.Point3D(3, -6, 1))
        assertThat(bodies[2].velocity).isEqualTo(Day12.Point3D(3, 2, -3))
        assertThat(bodies[3].position).isEqualTo(Day12.Point3D(2, 0, 4))
        assertThat(bodies[3].velocity).isEqualTo(Day12.Point3D(1, -1, -1))
    }
}