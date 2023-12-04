package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines
import kotlin.math.pow

object Day04 : Day {
    private val data by lazy { resourceLines(2023, 4) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val cards = toCards(data)
        return cards.fold(0) { ac, card ->
            val winnersCount = card.numbers.toSet().intersect(card.winners.toSet()).count()
            val score = 2.0.pow(winnersCount.toDouble() - 1.0).toInt()
            ac + score
        }
    }

    fun doPart2(data: List<String>): Int {
        val cardsCount: MutableMap<Int, Int> = mutableMapOf()
        for (i in 1 .. data.size) {
            cardsCount[i] = 1
        }
        val cards = toCards(data)
        cards.forEach { card ->
            val winnersCount = card.numbers.toSet().intersect(card.winners.toSet()).count()
            repeat((0 until winnersCount).count()) { i ->
                cardsCount[card.num + i + 1] = cardsCount[card.num + i + 1]!! + cardsCount[card.num]!!
            }
        }

        return cardsCount.values.sum()
    }

    data class Card(val num: Int, val numbers: List<Int>, val winners: List<Int>)

    fun toCards(data: List<String>): List<Card> {
        return data.fold(listOf()) { ac, line ->
            val parse1 = line.split(":", limit = 2)
            val num = parse1[0].split("\\s+".toRegex())[1].toInt()

            val numsSplit = parse1[1].split("|")
            val numbers = numsSplit[0].trim().split("\\s+".toRegex()).map { it.trim().toInt() }
            val winners = numsSplit[1].trim().split("\\s+".toRegex()).map { it.trim().toInt() }

            ac + Card(num, numbers, winners)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}