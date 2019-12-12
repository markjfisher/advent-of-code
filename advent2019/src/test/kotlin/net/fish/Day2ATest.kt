package net.fish

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day2ATest {
    @Test
    fun `running programs`() {
        val m1 = Day2Machine(memory = mutableListOf(1, 0, 0, 0, 99), instructionPointer = 0)
        assertThat(m1.runProgram().memory).containsExactly(2, 0, 0, 0, 99)

        val m2 = Day2Machine(memory = mutableListOf(2,3,0,3,99), instructionPointer = 0)
        assertThat(m2.runProgram().memory).containsExactly(2,3,0,6,99)

        val m3 = Day2Machine(memory = mutableListOf(2,4,4,5,99,0), instructionPointer = 0)
        assertThat(m3.runProgram().memory).containsExactly(2,4,4,5,99,9801)

        val m4 = Day2Machine(memory = mutableListOf(1,1,1,4,99,5,6,0,99), instructionPointer = 0)
        assertThat(m4.runProgram().memory).containsExactly(30,1,1,4,2,5,6,0,99)
    }
}