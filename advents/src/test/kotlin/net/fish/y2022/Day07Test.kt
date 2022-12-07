package net.fish.y2022

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day07Test {
    @Test
    fun `can do part 1`() {
        val root = Day07.readDisk(resourcePath("/2022/day07-test.txt"))
        assertThat(Day07.doPart1(root)).isEqualTo(95437)
    }

    @Test
    fun `can do part 2`() {
        val root = Day07.readDisk(resourcePath("/2022/day07-test.txt"))
        assertThat(Day07.doPart2(root)).isEqualTo(24933642)
    }

}