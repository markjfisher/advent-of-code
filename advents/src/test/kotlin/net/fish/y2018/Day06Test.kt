package net.fish.y2018

import net.fish.geometry.Point
import net.fish.resourcePath
import net.fish.y2018.Day06.Location
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day06Test {
    @Test
    fun `can parse locations`() {
        val locations = Day06.toLocations(resourcePath("/2018/day06-test.txt"))
        assertThat(locations).containsExactly(
            Location(Point(1,1), isBounded = false),
            Location(Point(1,6), isBounded = false),
            Location(Point(8,3), isBounded = false),
            Location(Point(3,4), isBounded = true),
            Location(Point(5,5), isBounded = true),
            Location(Point(8,9), isBounded = false)
        )
    }


    @Test
    fun `can do part 1`() {
        val locations = Day06.toLocations(resourcePath("/2018/day06-test.txt"))
        assertThat(Day06.doPart1(locations)).isEqualTo(17)
        assertThat(Day06.ownershipString(locations).trim()).isEqualTo("""
            aaaaa.cccc
            aAaaa.cccc
            aaaddecccc
            aadddeccCc
            ..dDdeeccc
            bb.deEeecc
            bBb.eeee..
            bbb.eeefff
            bbb.eeffff
            bbb.ffffFf
            bbb.ffffff
        """.trimIndent())
    }

    @Test
    fun `can do part 2`() {
        val locations = Day06.toLocations(resourcePath("/2018/day06-test.txt"))
        assertThat(Day06.safeLocations(locations, 32)).isEqualTo(16)
    }
}