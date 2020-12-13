package net.fish.y2020

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger

class Day13Test {
    private val data = resourcePath("/2020/day13-test.txt")

    @Test
    fun `lowest multiple for p1 returns the frequency and lowest after`() {
        assertThat(Day13.lowestMultipleAfter(939, 59)).isEqualTo(Pair(59, 944))
    }

    @Test
    fun `the p2 sequence`() {
        assertThat(Day13.nextMultipleWithOffsetSteppingBy(7, BigInteger.valueOf(1), 0, BigInteger.ONE)).isEqualTo(7)
        assertThat(Day13.nextMultipleWithOffsetSteppingBy(13, BigInteger.valueOf(7), 1, BigInteger.valueOf(7))).isEqualTo(77)
        assertThat(Day13.nextMultipleWithOffsetSteppingBy(59, BigInteger.valueOf(77), 4, BigInteger.valueOf(91))).isEqualTo(350)
        assertThat(Day13.nextMultipleWithOffsetSteppingBy(31, BigInteger.valueOf(350), 6, BigInteger.valueOf(5369))).isEqualTo(70147)
        assertThat(Day13.nextMultipleWithOffsetSteppingBy(19, BigInteger.valueOf(70147), 7, BigInteger.valueOf(166439))).isEqualTo(1068781)
    }

    @Test
    fun `part 1 result`() {
        assertThat(Day13.doPart1(data)).isEqualTo(295)
    }

    @Test
    fun `part 2 result`() {
        assertThat(Day13.doPart2(data[1])).isEqualTo(1068781)
    }

    @Test
    fun `other patterns`() {
        assertThat(Day13.doPart2("2,x,3,x,5")).isEqualTo(16)
        assertThat(Day13.doPart2("2,x,3,x,10")).isEqualTo(16)
    }

    @Test
    fun `generate sequences`() {
        val largest = (0 until 1000).map {
            // val pattern = Day13.generateInput(8, 5)
            val pattern = Day13.generateInput(78, 9)
            print("$pattern -> ")
            val output = Day13.doPart2(pattern)
            println(output)
            output
        }.maxOrNull()!!
        println("largest: $largest")
    }
}