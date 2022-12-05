package net.fish.y2022

import net.fish.resourceStrings
import net.fish.y2022.Day05.Instruction
import net.fish.y2022.Day05.MoverModel.M9000
import net.fish.y2022.Day05.MoverModel.M9001
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day05Test {
    @Test
    fun `can read stacks`() {
        val stacks = Day05.toStacks(resourceStrings(path = "/2022/day05-test.txt", trim = false))
        assertThat(stacks.columns).containsExactlyEntriesOf(mapOf(
            0 to ArrayDeque(listOf('Z', 'N')),
            1 to ArrayDeque(listOf('M', 'C', 'D')),
            2 to ArrayDeque(listOf('P'))
        ))
        assertThat(stacks.instructions).containsExactly(
            Instruction(1, 2, 1),
            Instruction(3, 1, 3),
            Instruction(2, 2, 1),
            Instruction(1, 1, 2)
        )
    }

    @Test
    fun `can do part 1`() {
        val stacks = Day05.toStacks(resourceStrings(path = "/2022/day05-test.txt", trim = false))
        stacks.processAllInstructions(M9000)
        assertThat(stacks.columns).containsExactlyEntriesOf(mapOf(
            0 to ArrayDeque(listOf('C')),
            1 to ArrayDeque(listOf('M')),
            2 to ArrayDeque(listOf('P', 'D', 'N', 'Z'))
        ))
        assertThat(stacks.tops()).isEqualTo("CMZ")
    }

    @Test
    fun `can do part 2`() {
        val stacks = Day05.toStacks(resourceStrings(path = "/2022/day05-test.txt", trim = false))
        stacks.processAllInstructions(M9001)
        assertThat(stacks.columns).containsExactlyEntriesOf(mapOf(
            0 to ArrayDeque(listOf('M')),
            1 to ArrayDeque(listOf('C')),
            2 to ArrayDeque(listOf('P', 'Z', 'N', 'D'))
        ))
        assertThat(stacks.tops()).isEqualTo("MCD")
    }
}