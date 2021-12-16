package net.fish.y2021

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day16Test {
    @Test
    fun `part 1 on 8A004A801A8002F478`() {
        Assertions.assertThat(Day16.doPart1("8A004A801A8002F478")).isEqualTo(16)
    }

    @Test
    fun `part 2 on C200B40A82`() {
        Assertions.assertThat(Day16.doPart2("C200B40A82")).isEqualTo(3)
    }

}