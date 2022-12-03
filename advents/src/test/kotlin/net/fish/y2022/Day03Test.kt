package net.fish.y2022

import net.fish.resourcePath
import net.fish.y2022.Day03.Compartment
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day03Test {
    val testData = resourcePath("/2022/day03-test.txt")

    @Test
    fun `can create rucksacks`() {
        assertThat(Day03.toRucksack(testData)).containsAll(listOf(
            Day03.Rucksack(
                left  = Compartment("vJrwpWtwJgWr".toList()),
                right = Compartment("hcsFMMfFFhFp".toList())
            ),
            Day03.Rucksack(
                left  = Compartment("jqHRNqRjqzjGDLGL".toList()),
                right = Compartment("rsFMfFZSrLrFZsSL".toList())
            ),
            Day03.Rucksack(
                left  = Compartment("PmmdzqPrV".toList()),
                right = Compartment("vPwwTWBwg".toList())
            ),
            Day03.Rucksack(
                left  = Compartment("wMqvLMZHhHMvwLH".toList()),
                right = Compartment("jbvcjnnSBnvTQFn".toList())
            ),
            Day03.Rucksack(
                left  = Compartment("ttgJtRGJ".toList()),
                right = Compartment("QctTZtZT".toList())
            ),
            Day03.Rucksack(
                left  = Compartment("CrZsJsPPZsGz".toList()),
                right = Compartment("wwsLwLmpwMDw".toList())
            ),
        ))
    }

    @Test
    fun `can get priority item`() {
        val rucksack = Day03.Rucksack(
            left  = Compartment("vJrwpWtwJgWr".toList()),
            right = Compartment("hcsFMMfFFhFp".toList())
        )
        assertThat(rucksack.priorityItem()).isEqualTo('p')
    }

    @Test
    fun `can get score of item`() {
        assertThat(Day03.itemScore('p')).isEqualTo(16)
        assertThat(Day03.itemScore('L')).isEqualTo(38)
    }

    @Test
    fun `can do part 1`() {
        assertThat(Day03.doPart1(Day03.toRucksack(testData))).isEqualTo(157)
    }

    @Test
    fun `can do part 2`() {
        assertThat(Day03.doPart2(Day03.toRucksack(testData))).isEqualTo(70)
    }

    @Test
    fun `can find common in all 3`() {
        val groups = Day03.createGroups(Day03.toRucksack(testData))
        assertThat(groups[0].findCommonItem()).isEqualTo('r')
        assertThat(groups[1].findCommonItem()).isEqualTo('Z')
    }
}