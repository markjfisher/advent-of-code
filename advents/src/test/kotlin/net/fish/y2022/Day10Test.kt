package net.fish.y2022

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day10Test {
    @Test
    fun `can do part 1`() {
        val computer = Day10.CRTComputer(Day10.toInstructions(resourcePath("/2022/day10b-test.txt")))
        assertThat(computer.signalStrengthsAt(listOf(20, 60, 100, 140, 180, 220)).sum()).isEqualTo(13140)
    }

    @Test
    fun `can create instructions`() {
        val instructions = Day10.toInstructions(resourcePath("/2022/day10-test.txt"))
        assertThat(instructions[0]).isEqualTo(Day10.Noop(2))
        assertThat(instructions[1]).isEqualTo(Day10.AddX(3, 4))
        assertThat(instructions[2]).isEqualTo(Day10.AddX(-5, 6))
    }

    @Test
    fun `can get signal strengths`() {
        val computer = Day10.CRTComputer(Day10.toInstructions(resourcePath("/2022/day10b-test.txt")))
        computer.step(20)
        assertThat(computer.x).isEqualTo(21)
        assertThat(computer.signalStrength()).isEqualTo(420)
        computer.step(40)
        assertThat(computer.x).isEqualTo(19)
        assertThat(computer.signalStrength()).isEqualTo(1140)
        computer.step(40)
        assertThat(computer.x).isEqualTo(18)
        assertThat(computer.signalStrength()).isEqualTo(1800)
        computer.step(40)
        assertThat(computer.x).isEqualTo(21)
        assertThat(computer.signalStrength()).isEqualTo(2940)
        computer.step(40)
        assertThat(computer.x).isEqualTo(16)
        assertThat(computer.signalStrength()).isEqualTo(2880)
        computer.step(40)
        assertThat(computer.x).isEqualTo(18)
        assertThat(computer.signalStrength()).isEqualTo(3960)
    }

    @Test
    fun `can generate crt`() {
        val computer = Day10.CRTComputer(Day10.toInstructions(resourcePath("/2022/day10b-test.txt")))
        val lines = computer.generateCRT(on = "#", off = ".")
        assertThat(lines).containsExactly(
            "##..##..##..##..##..##..##..##..##..##..",
            "###...###...###...###...###...###...###.",
            "####....####....####....####....####....",
            "#####.....#####.....#####.....#####.....",
            "######......######......######......####",
            "#######.......#######.......#######....."
        )
    }
}