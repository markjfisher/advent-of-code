package net.fish

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day5ATest {
    @Test
    fun `test day 5 machine`() {
        assertThat(AdventComputer(memory = mutableListOf(1002,4,3,4,33), inputs = emptyList()).runProgram().memory[4]).isEqualTo(99)
        assertThat(AdventComputer(memory = mutableListOf(1101,100,-1,4,0), inputs = emptyList()).runProgram().memory[4]).isEqualTo(99)
    }
}