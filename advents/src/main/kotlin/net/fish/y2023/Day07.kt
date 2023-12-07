package net.fish.y2023

import net.fish.Day
import net.fish.geometry.Point
import net.fish.resourceLines

object Day07 : Day {
    private val data by lazy { resourceLines(2023, 7) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Long {
        val hands = toHands(data).sorted()
        return hands.foldIndexed(0L) { i, ac, h ->
            ac + (i + 1) * h.value
        }
    }
    fun doPart2(data: List<String>): Long {
        val hands = toHands2(data).sorted()
        hands.forEach { println(it) }
        return hands.foldIndexed(0L) { i, ac, h ->
            ac + (i + 1) * h.value
        }
    }

    fun toHands(data: List<String>): List<Hand> {
        return data.map { line ->
            val parts = line.split(" ", limit = 2)
            Hand.from(parts[0], parts[1].trim().toInt())
        }
    }

    fun toHands2(data: List<String>): List<Hand2> {
        return data.map { line ->
            val parts = line.split(" ", limit = 2)
            Hand2.from(parts[0], parts[1].trim().toInt())
        }
    }

    data class Card(val value: Int): Comparable<Card> {
        override fun compareTo(other: Card): Int {
            return value.compareTo(other.value)
        }

        companion object {
            private val charToCard = mapOf(
                '1' to Card(1),
                '2' to Card(2),
                '3' to Card(3),
                '4' to Card(4),
                '5' to Card(5),
                '6' to Card(6),
                '7' to Card(7),
                '8' to Card(8),
                '9' to Card(9),
                'T' to Card(10),
                'J' to Card(11),
                'Q' to Card(12),
                'K' to Card(13),
                'A' to Card(14),
            )

            fun from(c: Char): Card = charToCard[c] ?: throw Exception("Could not find card $c")
        }
    }

    data class Card2(val value: Int): Comparable<Card2> {
        override fun compareTo(other: Card2): Int {
            return value.compareTo(other.value)
        }

        fun toChar(): Char = when(value) {
            0 -> 'J'
            1 -> '1'
            2 -> '2'
            3 -> '3'
            4 -> '4'
            5 -> '5'
            6 -> '6'
            7 -> '7'
            8 -> '8'
            9 -> '9'
            10 -> 'T'
            12 -> 'Q'
            13 -> 'K'
            14 -> 'A'
            else -> throw Exception("Unknown $value")
        }

        companion object {
            private val charToCard = mapOf(
                'J' to Card2(0),
                '1' to Card2(1),
                '2' to Card2(2),
                '3' to Card2(3),
                '4' to Card2(4),
                '5' to Card2(5),
                '6' to Card2(6),
                '7' to Card2(7),
                '8' to Card2(8),
                '9' to Card2(9),
                'T' to Card2(10),
                'Q' to Card2(12),
                'K' to Card2(13),
                'A' to Card2(14),
            )

            fun from(c: Char): Card2 = charToCard[c] ?: throw Exception("Could not find card $c")
        }
    }

    enum class HandType {
        NO_VALUE,
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_KIND,
        FULL_HOUSE,
        FOUR_OF_KIND,
        FIVE_OF_KIND
    }

    data class Hand(val cards: List<Card>, val value: Int = 0): Comparable<Hand> {
        init {
            if (cards.size != 5) throw Exception("Invalid Hand specified: $cards, must have 5 cards")
        }

        val type = toType()

        override fun compareTo(other: Hand): Int {
            val typeTest = type.compareTo(other.type)
            if (typeTest != 0) return typeTest
            for (i in cards.indices) {
                val comparison = cards[i].compareTo(other.cards[i])
                if (comparison != 0) {
                    return comparison
                }
            }
            return 0
        }

        private fun toType(): HandType {
            val valueCounts = cards.groupingBy(Card::value).eachCount()

            return when {
                valueCounts.containsValue(5) -> HandType.FIVE_OF_KIND
                valueCounts.containsValue(4) -> HandType.FOUR_OF_KIND
                valueCounts.containsValue(3) && valueCounts.containsValue(2) -> HandType.FULL_HOUSE
                valueCounts.containsValue(3) -> HandType.THREE_OF_KIND
                valueCounts.filterValues { it == 2 }.size == 2 -> HandType.TWO_PAIR
                valueCounts.containsValue(2) -> HandType.ONE_PAIR
                else -> HandType.HIGH_CARD
            }
        }

        companion object {
            fun from(cardString: String, value: Int = 0): Hand {
                return Hand(cardString.map { Card.from(it) }, value)
            }
        }
    }

    data class Hand2(val cards: List<Card2>, val value: Int = 0): Comparable<Hand2> {
        init {
            if (cards.size != 5) throw Exception("Invalid Hand specified: $cards, must have 5 cards")
        }

        val type = toType()

        override fun compareTo(other: Hand2): Int {
            val typeTest = type.compareTo(other.type)
            if (typeTest != 0) return typeTest

            for (i in cards.indices) {
                val comparison = cards[i].compareTo(other.cards[i])
                if (comparison != 0) {
                    return comparison
                }
            }
            return 0
        }

        fun toType(): HandType {
            val wildCardCount = cards.count { it.value == 0 }
            if (wildCardCount == 5) return HandType.FIVE_OF_KIND

            val regularCards = cards.filter { it.value != 0 }
            val valueCounts = regularCards.groupingBy(Card2::value).eachCount().toList().sortedByDescending { it.second }.toMap()

            if (wildCardCount > 0) {
                var best = HandType.NO_VALUE
                for ((value, count) in valueCounts) {
                    for (i in 1..wildCardCount) {
                        val newCount = count + i
                        if (newCount == 5 && best < HandType.FIVE_OF_KIND) best = HandType.FIVE_OF_KIND
                        if (newCount == 4 && best < HandType.FOUR_OF_KIND) best = HandType.FOUR_OF_KIND
                        if (newCount == 3 && valueCounts.values.count { it == 2 } == 2 && best < HandType.FULL_HOUSE) best = HandType.FULL_HOUSE
                        if (newCount == 3 && best < HandType.THREE_OF_KIND) best = HandType.THREE_OF_KIND
                        if (newCount == 2 && (valueCounts.filterValues { it == 2 }.size + 1 == 2) && best < HandType.TWO_PAIR) best = HandType.TWO_PAIR
                        if (newCount == 2 && best < HandType.ONE_PAIR) best = HandType.ONE_PAIR
                    }
                }
                if (best != HandType.NO_VALUE) return best
            }

            return when {
                valueCounts.containsValue(5) -> HandType.FIVE_OF_KIND
                valueCounts.containsValue(4) -> HandType.FOUR_OF_KIND
                valueCounts.containsValue(3) && valueCounts.containsValue(2) -> HandType.FULL_HOUSE
                valueCounts.containsValue(3) -> HandType.THREE_OF_KIND
                valueCounts.filterValues { it == 2 }.size == 2 -> HandType.TWO_PAIR
                valueCounts.containsValue(2) -> HandType.ONE_PAIR
                else -> HandType.HIGH_CARD
            }
        }

        companion object {
            fun from(cardString: String, value: Int = 0): Hand2 {
                return Hand2(cardString.map { Card2.from(it) }, value)
            }
        }

        override fun toString(): String {
            val cString = cards.map { it.toChar() }.joinToString("")
            return "Card2['$cString', $value, $type]"
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        // 251139436, 251386612, 251428428 too high
        println(part2())
    }

}