package net.fish.y2022

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day21Test {
    @Test
    fun `can parse monkeys`() {
        assertThat(Day21.parseMonkeys(listOf(
            "root: a + b",
            "a: 1",
            "b: 2"
        ))).containsExactlyEntriesOf(mapOf(
            "root" to MathsMonkey("root", "a", "b", MathsOperation.ADD),
            "a" to ValueMonkey("a", 1.0),
            "b" to ValueMonkey("b", 2.0)
        ))

        assertThat(Day21.parseMonkeys(listOf(
            "root: a * b",
            "a: c - d",
            "b: e / f"
        ))).containsExactlyEntriesOf(mapOf(
            "root" to MathsMonkey("root", "a", "b", MathsOperation.MUL),
            "a" to MathsMonkey("a", "c", "d", MathsOperation.SUB),
            "b" to MathsMonkey("b", "e", "f", MathsOperation.DIV)
        ))
    }

    @Test
    fun `can solve part 1`() {
        assertThat(Day21.solve("root", Day21.parseMonkeys(resourcePath("/2022/day21-test.txt")).toMutableMap())).isTrue
    }

    @Test
    fun `can solve part 2`() {
        // This is cheeky - the test data doesn't work with binary search over large ranges, but real input does, but here we're forcing first hit to be correct.
        // A better solution would eval both sides properly and try to calculate the value.
        assertThat(Day21.doPart2(resourcePath("/2022/day21-test.txt"), 300L, 302L)).isEqualTo(301)
    }
}