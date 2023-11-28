package net.fish.y2018

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day04Test {
    @Test
    fun `should parse test data`() {
        val d = Day04.parseInstructions(resourcePath("/2018/day04-test.txt"))
        assertThat(d.keys).containsExactly(10, 99)
        assertThat(d[10]!!.sleeps).hasSize(49) // unique minutes asleep, not total time asleep!
        assertThat(d[99]!!.sleeps).hasSize(19)

        assertThat(d[10]!!.sleeps[24]).isEqualTo(2)
        assertThat(d[99]!!.sleeps[45]).isEqualTo(3)

        assertThat(d[10]!!.totalSleep()).isEqualTo(50)
        assertThat(d[99]!!.totalSleep()).isEqualTo(30)

        assertThat(d[10]!!.mostAsleepAt()).isEqualTo(24)
        assertThat(d[99]!!.mostAsleepAt()).isEqualTo(45)
//        println(d[10])
//        println(d[99])
    }

    @Test
    fun `can do part 1 with test data`() {
        val d = Day04.parseInstructions(resourcePath("/2018/day04-test.txt"))
        assertThat(Day04.doPart1(d)).isEqualTo(240)
    }

    @Test
    fun `can do part 2 with test data`() {
        val d = Day04.parseInstructions(resourcePath("/2018/day04-test.txt"))
        assertThat(Day04.doPart2(d)).isEqualTo(4455)
    }
}