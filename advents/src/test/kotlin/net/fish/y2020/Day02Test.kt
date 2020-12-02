package net.fish.y2020

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day02Test {
    private val rules = Day02.toRules(resourcePath("/2020/day02-test.txt"))
    @Test
    fun `should validate input passwords for part 1`() {
        assertThat(rules.filter { it.isValidInPart1() }.count()).isEqualTo(2)
    }

    @Test
    fun `should validate input passwords for part 2`() {
        assertThat(rules.filter { it.isValidInPart2() }.count()).isEqualTo(1)
    }
}