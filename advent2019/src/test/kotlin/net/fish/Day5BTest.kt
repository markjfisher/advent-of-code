package net.fish

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day5BTest {
    @Test
    fun `day 5 machine tests - around 8`() {
        val around8Program = listOf(
            3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
            1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
            999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99
        ).map { it.toLong() }
        val machine1 = AdventComputer(bootMemory = around8Program.toMutableList(), inputs = listOf(0)).runProgram()
        assertThat(machine1.outputs).hasSize(1)
        assertThat(machine1.outputs.first()).isEqualTo(999)

        val machine2 = AdventComputer(bootMemory = around8Program.toMutableList(), inputs = listOf(8)).runProgram()
        assertThat(machine2.outputs).hasSize(1)
        assertThat(machine2.outputs.first()).isEqualTo(1000)

        val machine3 = AdventComputer(bootMemory = around8Program.toMutableList(), inputs = listOf(10)).runProgram()
        assertThat(machine3.outputs).hasSize(1)
        assertThat(machine3.outputs.first()).isEqualTo(1001)
    }
}