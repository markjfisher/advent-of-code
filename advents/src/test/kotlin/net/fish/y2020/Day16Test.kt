package net.fish.y2020

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day16Test {
    private val rules = Day16.toRules(resourcePath("/2020/day16-test-1.txt"))
    private val otherTickets = resourcePath("/2020/day16-test-3.txt").map { line -> line.split(",").map { it.toInt() } }

    private val rulesp2 = Day16.toRules(resourcePath("/2020/day16-test-1p2.txt"))
    private val myTicketp2 = resourcePath("/2020/day16-test-2p2.txt").first().split(",").map { it.toInt() }
    private val otherTicketsp2 = resourcePath("/2020/day16-test-3p2.txt").map { line -> line.split(",").map { it.toInt() } }

    @Test
    fun `rules to ids`() {
        val rule1 = Day16.Rule(name = "r1", low1 = 1, low2 = 5, high1 = 8, high2 = 10)
        val rule2 = Day16.Rule(name = "r2", low1 = 1, low2 = 5, high1 = 4, high2 = 7)
        assertThat(rule1.toValidIds()).containsExactly(1, 2, 3, 4, 5, 8, 9, 10)
        assertThat(rule2.toValidIds()).containsExactly(1, 2, 3, 4, 5, 6, 7)
    }

    @Test
    fun `convert data to rules`() {
        val expectedRule = Day16.Rule("departure location", 25, 863, 882, 957)
        assertThat(Day16.toRules(listOf("departure location: 25-863 or 882-957")).first()).isEqualTo(expectedRule)
    }

    @Test
    fun `p1 solution`() {
        assertThat(Day16.doPart1(rules, otherTickets)).isEqualTo(71)
    }

    @Test
    fun `remove all`() {
        val ids1 = listOf(1, 1, 2, 3).filterNot { setOf(1).contains(it) }
        assertThat(ids1).containsExactly(2, 3)
    }

    @Test
    fun `should match a rule`() {
        val rule = Day16.Rule("r1", 0, 5, 8, 12)
        assertThat(rule.matches(listOf(1, 2, 3, 9, 10))).isTrue
        assertThat(rule.matches(listOf(0))).isTrue
        assertThat(rule.matches(listOf(5))).isTrue
        assertThat(rule.matches(listOf(8))).isTrue
        assertThat(rule.matches(listOf(12))).isTrue
        assertThat(rule.matches(listOf(6))).isFalse
    }

    @Test
    fun `should match columns to rules`() {
        assertThat(Day16.createColumnToRuleMapping(rulesp2, otherTicketsp2)).containsAllEntriesOf(
            mutableMapOf(
                0 to Day16.Rule("departure row", 0, 5, 8, 19),
                1 to Day16.Rule("class", 0, 1, 4, 19),
                2 to Day16.Rule("departure seat", 0, 13, 16, 19),
            )
        )
    }

    @Test
    fun `should calculate p2 answer`() {
        assertThat(Day16.doPart2(rulesp2, myTicketp2, otherTicketsp2)).isEqualTo(143)
    }
}