package net.fish.y2020

import net.fish.resourcePath
import net.fish.y2020.Day21.toRecipes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day21Test {
    private val recipes = toRecipes(resourcePath("/2020/day21-test.txt"))

    @Test
    fun `should read foods`() {
        assertThat(recipes).hasSize(4)
        assertThat(recipes[0]).isEqualTo(Recipe(setOf("mxmxvkd", "kfcds", "sqjhc", "nhms"), setOf("dairy", "fish")))
    }

    @Test
    fun `solution p1`() {
        assertThat(Day21.doPart1(recipes)).isEqualTo(5)
    }

    @Test
    fun `solution p2`() {
        assertThat(Day21.doPart2(recipes)).isEqualTo("mxmxvkd,sqjhc,fvjkl")
    }

}