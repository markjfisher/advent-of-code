package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines

object Day07 : Day {
    private val data by lazy { resourceLines(2023, 7) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Long = toHands(data).sorted().score()
    fun doPart2(data: List<String>): Long = toHands(data, true).sorted().score()

    fun toHands(data: List<String>, wildCards: Boolean = false): List<Hand> {
        return data.map { line ->
            val parts = line.split(" ", limit = 2)
            Hand(parts[0], parts[1].trim().toInt(), wildCards)
        }
    }

    data class Hand(val cards: String, val score: Int = 0, val wildCards: Boolean = false): Comparable<Hand> {
        init {
            if (cards.length != 5) throw Exception("Hand is wrong size, cards: $cards")
        }
        val type = typeOf()

        private fun typeOf(): HandType {
            val numWildcards = if (wildCards) cards.count { it == 'J' } else 0
            val c2 = if (wildCards) cards.replace("J", "") else cards
            if (c2 == "") return HandType.FIVE_OF_KIND // all J's

            val byLength = c2.groupingBy { it }.eachCount().values.sortedByDescending { it }.toMutableList()
            // Add all the wild cards to the highest group count.
            byLength[0] += numWildcards

            // now we have list of counts of same card, e.g. [5], [4, 1], [3, 2], [3, 1, 1], [2, 2, 1], [2, 1, 1, 1], [1, 1, 1, 1, 1]
            return when {
                byLength.size == 1 -> HandType.FIVE_OF_KIND
                byLength[0] == 4 -> HandType.FOUR_OF_KIND
                byLength[0] == 3 && byLength.size == 2 -> HandType.FULL_HOUSE
                byLength[0] == 3 -> HandType.THREE_OF_KIND
                byLength[0] == 2 && byLength.size == 3 -> HandType.TWO_PAIR
                byLength[0] == 2 -> HandType.ONE_PAIR
                byLength.size == 5 -> HandType.HIGH_CARD
                else -> throw Exception("Unknown type for hand: $cards")
            }
        }

        override fun compareTo(other: Hand): Int {
            val typeTest = type.compareTo(other.type)
            if (typeTest != 0) return typeTest

            for (i in cards.indices) {
                val comparison = valueOfCard(cards[i]).compareTo(valueOfCard(other.cards[i]))
                if (comparison != 0) {
                    return comparison
                }
            }
            return 0
        }

        private fun valueOfCard(c: Char): Int {
            if (c == 'J' && wildCards) return 0
            return when (c) {
                in '1' .. '9' -> c - '0'
                'T' -> 10
                'J' -> 11
                'Q' -> 12
                'K' -> 13
                'A' -> 14
                else -> throw Exception("Unknown card value: $c")
            }
        }

    }

    enum class HandType {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_KIND,
        FULL_HOUSE,
        FOUR_OF_KIND,
        FIVE_OF_KIND
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

private fun List<Day07.Hand>.score(): Long {
    return foldIndexed(0L) { i, ac, h ->
        ac + (i + 1) * h.score
    }
}
