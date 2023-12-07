package net.fish.y2023

import net.fish.resourcePath
import net.fish.resourceStrings
import net.fish.y2023.Day07.HandType.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day07Test {

    @Test
    fun `can create cards and hands`() {
        val c1 = Day07.Card.from('1')
        assertThat(c1.value).isEqualTo(1)

        val cJ = Day07.Card.from('J')
        assertThat(cJ.value).isEqualTo(11)

        val h1 = Day07.Hand.from("TJQKA")
        assertThat(h1.cards[0]).isIn(Day07.Card.from('T'))
        assertThat(h1.cards[1]).isIn(Day07.Card.from('J'))
        assertThat(h1.cards[2]).isIn(Day07.Card.from('Q'))
        assertThat(h1.cards[3]).isIn(Day07.Card.from('K'))
        assertThat(h1.cards[4]).isIn(Day07.Card.from('A'))
    }

    @Test
    fun `can compare hands`() {
        assertThat(Day07.Hand.from("KK677")).isGreaterThan(Day07.Hand.from("KTJJT"))
        assertThat(Day07.Hand.from("33332")).isGreaterThan(Day07.Hand.from("2AAAA"))
        assertThat(Day07.Hand.from("33332")).isGreaterThan(Day07.Hand.from("45678"))
        assertThat(Day07.Hand.from("45678")).isLessThan(Day07.Hand.from("33332"))
        assertThat(Day07.Hand.from("33332")).isEqualTo(Day07.Hand.from("33332"))
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
        assertThat(Day07.Hand.from("AAAAA").type).isEqualTo(FIVE_OF_KIND)
        assertThat(Day07.Hand.from("1AAAA").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand.from("A1AAA").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand.from("AA1AA").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand.from("AAA1A").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand.from("AAAA1").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand.from("11AAA").type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand.from("1AAA1").type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand.from("1AA1A").type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand.from("1A1AA").type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand.from("TTT98").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand.from("TT9T8").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand.from("T9TT8").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand.from("9TTT8").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand.from("23432").type).isEqualTo(TWO_PAIR)
        assertThat(Day07.Hand.from("34232").type).isEqualTo(TWO_PAIR)
        assertThat(Day07.Hand.from("A23A4").type).isEqualTo(ONE_PAIR)
        assertThat(Day07.Hand.from("23456").type).isEqualTo(HIGH_CARD)
    }

    @Test
    fun `can read and sort hands`() {
        val hands = Day07.toHands(resourcePath("/2023/day07-test.txt"))
        assertThat(hands).containsExactly(
            Day07.Hand.from("32T3K", 765),
            Day07.Hand.from("T55J5", 684),
            Day07.Hand.from("KK677", 28),
            Day07.Hand.from("KTJJT", 220),
            Day07.Hand.from("QQQJA", 483),
        )

        val sorted = hands.sorted()
        assertThat(sorted).containsExactly(
            Day07.Hand.from("32T3K", 765),
            Day07.Hand.from("KTJJT", 220),
            Day07.Hand.from("KK677", 28),
            Day07.Hand.from("T55J5", 684),
            Day07.Hand.from("QQQJA", 483),
        )
    }

    @Test
    fun `can get hand2 types`() {
        assertThat(Day07.Hand2.from("AAAAA").type).isEqualTo(FIVE_OF_KIND)
        assertThat(Day07.Hand2.from("1AAAA").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand2.from("A1AAA").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand2.from("AA1AA").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand2.from("AAA1A").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand2.from("AAAA1").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand2.from("11AAA").type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand2.from("1AAA1").type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand2.from("1AA1A").type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand2.from("1A1AA").type).isEqualTo(FULL_HOUSE)
        assertThat(Day07.Hand2.from("TTT98").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand2.from("TT9T8").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand2.from("T9TT8").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand2.from("9TTT8").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand2.from("23432").type).isEqualTo(TWO_PAIR)
        assertThat(Day07.Hand2.from("34232").type).isEqualTo(TWO_PAIR)
        assertThat(Day07.Hand2.from("A23A4").type).isEqualTo(ONE_PAIR)
        assertThat(Day07.Hand2.from("23456").type).isEqualTo(HIGH_CARD)

        // jokers
        assertThat(Day07.Hand2.from("T55J5").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand2.from("KTJJT").type).isEqualTo(FOUR_OF_KIND)
        assertThat(Day07.Hand2.from("QQQJA").type).isEqualTo(FOUR_OF_KIND)

        assertThat(Day07.Hand2.from("JJJJJ").type).isEqualTo(FIVE_OF_KIND)
        assertThat(Day07.Hand2.from("1JJJJ").type).isEqualTo(FIVE_OF_KIND)


    }

    @Test
    fun `can parse full house and 3 of kind part 2`() {
        assertThat(Day07.Hand2.from("J2326").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand2.from("J2449").type).isEqualTo(THREE_OF_KIND)
        assertThat(Day07.Hand2.from("J2442").type).isEqualTo(FULL_HOUSE)
    }

    @Test
    fun `can compare hands2`() {
        assertThat(Day07.Hand2.from("JKKK2")).isLessThan(Day07.Hand2.from("QQQQ2"))
        assertThat(Day07.Hand2.from("JKKK2")).isLessThan(Day07.Hand2.from("33332"))

        assertThat(Day07.Hand2.from("J27AK")).isLessThan(Day07.Hand2.from("J29K7"))
    }

    @Test
    fun `can read and sort hands2`() {
        val hands = Day07.toHands2(resourcePath("/2023/day07-test.txt"))
        assertThat(hands).containsExactly(
            Day07.Hand2.from("32T3K", 765),
            Day07.Hand2.from("T55J5", 684),
            Day07.Hand2.from("KK677", 28),
            Day07.Hand2.from("KTJJT", 220),
            Day07.Hand2.from("QQQJA", 483),
        )

        val sorted = hands.sorted()
        assertThat(sorted).containsExactly(
            Day07.Hand2.from("32T3K", 765),
            Day07.Hand2.from("KK677", 28),
            Day07.Hand2.from("T55J5", 684),
            Day07.Hand2.from("QQQJA", 483),
            Day07.Hand2.from("KTJJT", 220),
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