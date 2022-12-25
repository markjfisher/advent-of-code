package net.fish.y2022

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class Day25Test {
    @ParameterizedTest
    @CsvSource(
        "1=-0-2, 1747",
        "12111, 906",
        "2=0=, 198",
        "21, 11",
        "2=01, 201",
        "111, 31",
        "20012, 1257",
        "112, 32",
        "1=-1=, 353",
        "1-12, 107",
        "12, 7",
        "1=, 3",
        "122, 37"
    )
    fun `can convert to and from snafu`(s: String, d: String) {
        assertThat(Day25.fromSnafu(s)).isEqualTo(d.toLong())
        assertThat(Day25.toSnafu(d.toLong())).isEqualTo(s)
    }
}