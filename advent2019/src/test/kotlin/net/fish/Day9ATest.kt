package net.fish

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day9ATest {
    @Test
    fun `copy program`() {
        val bootMemory = longArrayOf(109, 1, 204, -1, 1001, 100, 1, 100, 1008, 100, 16, 101, 1006, 101, 0, 99).toMutableList()
        val computer = AdventComputer(bootMemory = bootMemory).runProgram()
        assertThat(computer.outputs).containsAll(bootMemory)
    }

    @Test
    fun `long multiplication`() {
        val bootMemory = longArrayOf(1102,34915192,34915192,7,4,7,99,0).toMutableList()
        val computer = AdventComputer(bootMemory).runProgram()
        assertThat(computer.takeOutput()).isEqualTo(1_219_070_632_396_864L)
    }

    @Test
    fun `output middle long number`() {
        val bootMemory = longArrayOf(104,1125899906842624,99).toMutableList()
        val computer = AdventComputer(bootMemory).runProgram()
        assertThat(computer.takeOutput()).isEqualTo(1125899906842624L)
    }
}