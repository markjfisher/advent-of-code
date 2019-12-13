package net.fish.y2019

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AdventCalendarTest {
    @Test
    fun `running programs`() {
        assertThat(AdventComputer(program = mutableListOf(1, 0, 0, 0, 99)).run().memoryAt(0)).isEqualTo(2)
        assertThat(AdventComputer(program = mutableListOf(2,3,0,3,99)).run().memoryAt(3)).isEqualTo(6)
        assertThat(AdventComputer(program = mutableListOf(2,4,4,5,99,0)).run().memoryAt(5)).isEqualTo(9801)
        assertThat(AdventComputer(program = mutableListOf(1,1,1,4,99,5,6,0,99)).run().memoryAt(0)).isEqualTo(30)
        assertThat(AdventComputer(program = mutableListOf(1002, 4, 3, 4, 33)).run().memoryAt(4)).isEqualTo(99)
        assertThat(AdventComputer(program = mutableListOf(1101, 100, -1, 4, 0)).run().memoryAt(4)).isEqualTo(99)
    }

    @Test
    fun `8 test`() {
        val around8Program = listOf(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31, 1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104, 999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99).map { it.toLong() }

        val m1 = AdventComputer(program = around8Program.toMutableList(), inputs = listOf(0)).run()
        assertThat(m1.outputs).hasSize(1)
        assertThat(m1.outputs.first()).isEqualTo(999)

        val m2 = AdventComputer(program = around8Program.toMutableList(), inputs = listOf(8)).run()
        assertThat(m2.outputs).hasSize(1)
        assertThat(m2.outputs.first()).isEqualTo(1000)

        val m3 = AdventComputer(program = around8Program.toMutableList(), inputs = listOf(10)).run()
        assertThat(m3.outputs).hasSize(1)
        assertThat(m3.outputs.first()).isEqualTo(1001)
    }

    @Test
    fun `copy program`() {
        val bootMemory = longArrayOf(109, 1, 204, -1, 1001, 100, 1, 100, 1008, 100, 16, 101, 1006, 101, 0, 99).toMutableList()
        val computer = AdventComputer(program = bootMemory).run()
        assertThat(computer.outputs).containsAll(bootMemory)
    }

    @Test
    fun `long multiplication`() {
        val bootMemory = longArrayOf(1102,34915192,34915192,7,4,7,99,0).toMutableList()
        val computer = AdventComputer(bootMemory).run()
        assertThat(computer.out()).isEqualTo(1_219_070_632_396_864L)
    }

    @Test
    fun `output middle long number`() {
        val bootMemory = longArrayOf(104,1125899906842624,99).toMutableList()
        val computer = AdventComputer(bootMemory).run()
        assertThat(computer.out()).isEqualTo(1125899906842624L)
    }

}