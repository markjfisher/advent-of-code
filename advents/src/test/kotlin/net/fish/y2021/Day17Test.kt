package net.fish.y2021

import net.fish.y2021.Day17.Target
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day17Test {
    @Test
    fun `can extract target from data`() {
        assertThat(Day17.toTarget("target area: x=20..30, y=-10..-5")).isEqualTo(Target(20, 30, -10, -5))
    }

    @Test
    fun `can find max height for test data`() {
        assertThat(Day17.doPart1(Day17.toTarget("target area: x=20..30, y=-10..-5"))).isEqualTo(45)
    }

    @Test
    fun `can find target hits for test data`() {
        assertThat(Day17.doPart2(Day17.toTarget("target area: x=20..30, y=-10..-5"))).isEqualTo(112)
    }

    @Test
    fun `PJ part 1`() {
        assertThat(Day17.doPart1(Day17.toTarget("target area: x=169..206, y=-108..-68"))).isEqualTo(5778)
    }

    @Test
    fun `PJ part 2`() {
        assertThat(Day17.doPart2(Day17.toTarget("target area: x=169..206, y=-108..-68"))).isEqualTo(2576)
    }

}