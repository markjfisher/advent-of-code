package net.fish.y2022

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day06Test {
    @Test
    fun `find sequence of length 4`() {
        assertThat(Day06.findMarker("mjqjpqmgbljsphdztnvjfqwrcgsmlb".toList(), 4)).isEqualTo(7)
        assertThat(Day06.findMarker("bvwbjplbgvbhsrlpgdmjqwftvncz".toList(), 4)).isEqualTo(5)
        assertThat(Day06.findMarker("nppdvjthqldpwncqszvftbrmjlhg".toList(), 4)).isEqualTo(6)
        assertThat(Day06.findMarker("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg".toList(), 4)).isEqualTo(10)
        assertThat(Day06.findMarker("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw".toList(), 4)).isEqualTo(11)
    }

    @Test
    fun `find sequence of length 14`() {
        assertThat(Day06.findMarker("mjqjpqmgbljsphdztnvjfqwrcgsmlb".toList(), 14)).isEqualTo(19)
        assertThat(Day06.findMarker("bvwbjplbgvbhsrlpgdmjqwftvncz".toList(), 14)).isEqualTo(23)
        assertThat(Day06.findMarker("nppdvjthqldpwncqszvftbrmjlhg".toList(), 14)).isEqualTo(23)
        assertThat(Day06.findMarker("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg".toList(), 14)).isEqualTo(29)
        assertThat(Day06.findMarker("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw".toList(), 14)).isEqualTo(26)
    }
}