package net.fish.y2020

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day07Test {
    private val rules = Day07.toRules(resourcePath("/2020/day07-test.txt"))

    @Test
    fun `can read rules`() {
        assertThat(rules).hasSize(9)
        assertThat(rules.first()).isEqualTo(
            Day07.Rule(
                bag = Day07.Bag(name = "light red"),
                contains = listOf(
                    Day07.Contain(num = 1, bag = Day07.Bag(name = "bright white")),
                    Day07.Contain(num = 2, bag = Day07.Bag(name = "muted yellow")),
                )
            )
        )
    }

    @Test
    fun `should find bags that can contain us`() {
        val foundBagsCount = Day07.doPart1("shiny gold", rules)
        assertThat(foundBagsCount).isEqualTo(4)
    }

    @Test
    fun `should count how many contained in bag`() {
        assertThat(Day07.doPart2("shiny gold", rules)).isEqualTo(32)
    }

}