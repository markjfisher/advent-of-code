package net.fish.y2020

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Day08Test {
    private val instructions = resourcePath("/2020/day08-test.txt")

    @BeforeEach
    fun clear() {
        Day08.pcLocations.clear()
    }

    @Test
    fun `should stop itself looping and return accumulator instead of infinitely looping`() {
        assertThat(Day08.runPC1(instructions)).isEqualTo(5)
    }

    @Test
    fun `should be able to amend program to stop looping and return final acc`() {
        assertThat(Day08.runPC2(instructions)).isEqualTo(8)
    }

    @Test
    fun `should generate sequences of programs`() {
        val i1 = listOf("nop +1", "acc +2", "jmp +3", "nop +4")
        assertThat(Day08.generatePrograms(i1).toList()).containsExactly(
            listOf("jmp 1", "acc +2", "jmp +3", "nop +4"),
            listOf("nop +1", "acc +2", "nop 3", "nop +4"),
            listOf("nop +1", "acc +2", "jmp +3", "jmp 4")
        )
    }
}