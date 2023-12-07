package net.fish.y2023

import net.fish.resourcePath
import net.fish.y2023.Day07.HandType.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day07Test {

    @Test
    fun `can compare hands`() {
        assertThat(Day07.Hand("KK677")).isGreaterThan(Day07.Hand("KTJJT"))
        assertThat(Day07.Hand("33332")).isGreaterThan(Day07.Hand("2AAAA"))
        assertThat(Day07.Hand("33332")).isGreaterThan(Day07.Hand("45678"))
        assertThat(Day07.Hand("45678")).isLessThan(Day07.Hand("33332"))
        assertThat(Day07.Hand("33332")).isEqualTo(Day07.Hand("33332"))
    }

    @Test
    fun `can compare hand types`() {
        assertThat(HIGH_CARD).isLessThan(ONE_PAIR)
        assertThat(ONE_PAIR).isLessThan(TWO_PAIR)
        assertThat(TWO_PAIR).isLessThan(THREE_OF_KIND)
        assertThat(THREE_OF_KIND).isLessThan(FULL_HOUSE)
        assertThat(FULL_HOUSE).isLessThan(FOUR_OF_KIND)
        assertThat(FOUR_OF_KIND).isLessThan(FIVE_OF_KIND)
    }

    @Test
    fun `can get hand types`() {
        assertThat(Day07.Hand("AAAAA").type).isEqualTo(FIVE_OF_KIND)
        assertThat(Day07.Hand("1AAAA").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand("A1AAA").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand("AA1AA").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand("AAA1A").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand("AAAA1").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand("11AAA").type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand("1AAA1").type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand("1AA1A").type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand("1A1AA").type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand("TTT98").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand("TT9T8").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand("T9TT8").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand("9TTT8").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand("23432").type).isEqualTo(TWO_PAIR)
        assertThat(Day07.Hand("34232").type).isEqualTo(TWO_PAIR)
        assertThat(Day07.Hand("A23A4").type).isEqualTo(ONE_PAIR)
        assertThat(Day07.Hand("23456").type).isEqualTo(HIGH_CARD)
    }

    @Test
    fun `can read and sort hands`() {
        val hands = Day07.toHands(resourcePath("/2023/day07-test.txt"))
        assertThat(hands).containsExactly(
            Day07.Hand("32T3K", 765),
            Day07.Hand("T55J5", 684),
            Day07.Hand("KK677", 28),
            Day07.Hand("KTJJT", 220),
            Day07.Hand("QQQJA", 483),
        )

        val sorted = hands.sorted()
        assertThat(sorted).containsExactly(
            Day07.Hand("32T3K", 765),
            Day07.Hand("KTJJT", 220),
            Day07.Hand("KK677", 28),
            Day07.Hand("T55J5", 684),
            Day07.Hand("QQQJA", 483),
        )
    }

    @Test
    fun `can get hand2 types`() {
        assertThat(Day07.Hand("AAAAA", 0, true).type).isEqualTo(FIVE_OF_KIND)
        assertThat(Day07.Hand("1AAAA", 0, true).type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand("A1AAA", 0, true).type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand("AA1AA", 0, true).type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand("AAA1A", 0, true).type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand("AAAA1", 0, true).type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand("11AAA", 0, true).type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand("1AAA1", 0, true).type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand("1AA1A", 0, true).type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand("1A1AA", 0, true).type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand("TTT98", 0, true).type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand("TT9T8", 0, true).type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand("T9TT8", 0, true).type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand("9TTT8", 0, true).type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand("23432", 0, true).type).isEqualTo(TWO_PAIR)
        assertThat(Day07.Hand("34232", 0, true).type).isEqualTo(TWO_PAIR)
        assertThat(Day07.Hand("A23A4", 0, true).type).isEqualTo(ONE_PAIR)
        assertThat(Day07.Hand("23456", 0, true).type).isEqualTo(HIGH_CARD)

        // jokers
        assertThat(Day07.Hand("T55J5", 0, true).type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand("KTJJT", 0, true).type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand("QQQJA", 0, true).type).isEqualTo(FOUR_OF_KIND)

        assertThat(Day07.Hand("JJJJJ", 0, true).type).isEqualTo(FIVE_OF_KIND)
        assertThat(Day07.Hand("1JJJJ", 0, true).type).isEqualTo(FIVE_OF_KIND)

    }

    @Test
    fun `can parse full house and 3 of kind part 2`() {
        assertThat(Day07.Hand("J2326", 0, true).type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand("J2449", 0, true).type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand("J2442", 0, true).type).isEqualTo(FULL_HOUSE)
    }

    @Test
    fun `can compare hands2`() {
        assertThat(Day07.Hand("JKKK2", 0, true)).isLessThan(Day07.Hand("QQQQ2", 0, true))
        assertThat(Day07.Hand("JKKK2", 0, true)).isLessThan(Day07.Hand("33332", 0, true))

        assertThat(Day07.Hand("J27AK", 0, true)).isLessThan(Day07.Hand("J29K7", 0, true))
    }

    @Test
    fun `can read and sort hands2`() {
        val hands = Day07.toHands(resourcePath("/2023/day07-test.txt"), true)
        assertThat(hands).containsExactly(
            Day07.Hand("32T3K", 765, true),
            Day07.Hand("T55J5", 684, true),
            Day07.Hand("KK677", 28, true),
            Day07.Hand("KTJJT", 220, true),
            Day07.Hand("QQQJA", 483, true),
        )

        val sorted = hands.sorted()
        assertThat(sorted).containsExactly(
            Day07.Hand("32T3K", 765, true),
            Day07.Hand("KK677", 28, true),
            Day07.Hand("T55J5", 684, true),
            Day07.Hand("QQQJA", 483, true),
            Day07.Hand("KTJJT", 220, true),
        )
    }

    @Test
    fun `can do part 1`() {
        assertThat(Day07.doPart1(resourcePath("/2023/day07-test.txt"))).isEqualTo(6440)
    }

    @Test
    fun `can do part 2`() {
        assertThat(Day07.doPart2(resourcePath("/2023/day07-test.txt"))).isEqualTo(5905)
    }

}