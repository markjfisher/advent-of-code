package net.fish.y2020

import net.fish.resourceStrings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day19Test {
    private val testData1 = resourceStrings("/2020/day19-test.txt")
    private val testData2 = resourceStrings("/2020/day19-2-test.txt")

    private val testRules = Day19.toRulesMap(testData1[0])
    private val lines = testData1[1].split("\n")

    private val testRules2P1 = Day19.toRulesMap(testData2.first())
    private val testRules2P2 = testRules2P1 + listOf(8 to "42 | 42 8", 11 to "42 31 | 42 11 31").toMap()
    private val lines2 = testData2[1].split("\n")

    @Test
    fun `should match single rule`() {
        val t1Data = """
            0: a
        """.trimIndent()
        val r = Day19.toRulesMap(t1Data)
        assertThat(r).containsExactlyEntriesOf(mapOf(0 to "a"))

        val lines = listOf("a", "b", "a")
        assertThat(Day19.runPuzzle(r, lines)).isEqualTo(2)
    }

    @Test
    fun `should match simple rules`() {
        val t1Data = """
            0: 1
            1: a
        """.trimIndent()
        val r = Day19.toRulesMap(t1Data)
        assertThat(r).containsExactlyEntriesOf(mapOf(0 to "1", 1 to "a"))

        val lines = listOf("a", "b", "a")
        assertThat(Day19.runPuzzle(r, lines)).isEqualTo(2)
    }

    @Test
    fun `should match simple or rules`() {
        val t1Data = """
            0: 1 | 2
            1: a
            2: b
        """.trimIndent()
        val r = Day19.toRulesMap(t1Data)
        assertThat(r).containsExactlyEntriesOf(mapOf(0 to "1 | 2", 1 to "a", 2 to "b"))

        val lines = listOf("a", "b", "ab", "ba")
        assertThat(Day19.runPuzzle(r, lines)).isEqualTo(2)
    }

    @Test
    fun `should match multiple or rules`() {
        val t1Data = """
            0: 1 | 2
            1: 3 | 4
            2: 4 4
            3: 5 5
            4: a
            5: b
        """.trimIndent()
        val r = Day19.toRulesMap(t1Data)
        assertThat(r).containsExactlyEntriesOf(mapOf(0 to "1 | 2", 1 to "3 | 4", 2 to "4 4", 3 to "5 5", 4 to "a", 5 to "b"))

        val lines = listOf(
            "a",  // matches 0 -> 1 -> 4 -> a
            "bb", // matches 0 -> 1 -> 3 -> 5 5 -> b b
            "b",  // no
            "aa", // matches 0 -> 2 -> 4 4 -> a a
            "ab", // no
            "ba", // no
        )
        assertThat(Day19.runPuzzle(r, lines)).isEqualTo(3)
    }

    @Test
    fun `should match recursion of rule at end`() {
        val t1Data = """
            0: 1 | 2
            1: 3 | 3 1
            2: a
            3: b
        """.trimIndent()
        val r = Day19.toRulesMap(t1Data)
        assertThat(r).containsExactlyEntriesOf(mapOf(0 to "1 | 2", 1 to "3 | 3 1", 2 to "a", 3 to "b"))

        val lines = listOf(
            "a",     // matches 0 -> 2 -> a
            "b",     // matches 0 -> 1 -> 3
            "bb",    // matches 0 -> 1 -> 3 1 -> b 3 -> b b
            "bbb",   // matches 0 -> 1 -> 3 1 -> b 3 1 -> b b 3 -> b b b
        )
        assertThat(Day19.runPuzzle(r, lines)).isEqualTo(4)
    }

    @Test
    fun `should match recursion of rule at end in left part`() {
        val t1Data = """
            0: 1 | 2
            1: 3 1 | 3
            2: a
            3: b
        """.trimIndent()
        val r = Day19.toRulesMap(t1Data)
        assertThat(r).containsExactlyEntriesOf(mapOf(0 to "1 | 2", 1 to "3 1 | 3", 2 to "a", 3 to "b"))

        val lines = listOf(
            "a",     // matches 0 -> 2 -> a
            "b",     // matches 0 -> 1 -> 3
            "bb",    // matches 0 -> 1 -> 3 1 -> b 3 -> b b
            "bbb",   // matches 0 -> 1 -> 3 1 -> b 3 1 -> b b 3 -> b b b
        )
        assertThat(Day19.runPuzzle(r, lines)).isEqualTo(4)
    }

    @Test
    fun `should match recursion of rule in middle`() {
        val t1Data = """
            0: 1 | 2
            1: 3 4 | 3 1 4
            2: 4
            3: a
            4: b
        """.trimIndent()
        val r = Day19.toRulesMap(t1Data)
        assertThat(r).containsExactlyEntriesOf(mapOf(0 to "1 | 2", 1 to "3 4 | 3 1 4", 2 to "4", 3 to "a", 4 to "b"))

        val lines = listOf(
            "b",      // matches 0 -> 2 -> 4 -> b
            "ab",     // matches 0 -> 1 -> 3 4 -> a b
            "aabb",   // matches 0 -> 1 -> 3 1 4 -> a 1 4 -> a 3 4 4 -> a a b b
            "aaabbb", // matches 0 -> 1 -> 3 1 4 -> a 1 4 -> a 3 1 4 4 -> a a 1 4 4 -> a a 3 4 4 4 -> a a a b b b
        )
        assertThat(Day19.runPuzzle(r, lines)).isEqualTo(4)
    }

    @Test
    fun `matches recursion of rule in middle when on left`() {
        val t1Data = """
            0: 1 | 2
            1: 3 1 4 | 3 4
            2: 4
            3: a
            4: b
        """.trimIndent()
        val r = Day19.toRulesMap(t1Data)
        assertThat(r).containsExactlyEntriesOf(mapOf(0 to "1 | 2", 1 to "3 1 4 | 3 4", 2 to "4", 3 to "a", 4 to "b"))

        val lines = listOf(
            "b",      // matches 0 -> 2 -> 4 -> b
            "ab",     // matches 0 -> 1 -> 3 4 -> a b
            "aabb",   // matches 0 -> 1 -> 3 1 4 -> a 1 4 -> a 3 4 4 -> a a b b
            "aaabbb", // matches 0 -> 1 -> 3 1 4 -> a 1 4 -> a 3 1 4 4 -> a a 1 4 4 -> a a 3 4 4 4 -> a a a b b b
        )
        assertThat(Day19.runPuzzle(r, lines)).isEqualTo(4)
    }

    @Test
    fun `should work with test data p1`() {
        assertThat(Day19.runPuzzle(testRules, lines)).isEqualTo(2)
    }

    @Test
    fun `should work with test data p2`() {
        assertThat(Day19.runPuzzle(testRules2P1, lines2)).isEqualTo(3)
        assertThat(Day19.runPuzzle(testRules2P2, lines2)).isEqualTo(12)
    }

}