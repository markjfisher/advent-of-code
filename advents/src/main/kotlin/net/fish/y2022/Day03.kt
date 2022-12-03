package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines

object Day03 : Day {
    private val data = toRucksack(resourceLines(2022, 3))

    fun itemScore(item: Char): Int {
        return when (item) {
            in 'a'..'z' -> item - 'a' + 1
            in 'A'..'Z' -> item - 'A' + 27
            else -> throw Exception("Unknown item: $item")
        }
    }

    data class Group(val rucksacks: List<Rucksack>) {
        fun findCommonItem(): Char {
            // reduce by intersecting all items in each group's rucksack
            val common = rucksacks.map { it.allItems().toSet() }.reduce { ac, items ->
                ac.intersect(items.toSet())
            }
            if (common.size != 1) throw Exception("More than 1 item common in group: $common")
            return common.first()
        }
    }

    data class Rucksack(val left: List<Char>, val right: List<Char>) {
        fun priorityItem(): Char {
            val shared = left.intersect(right.toSet())
            if (shared.size != 1) throw Exception("More than 1 item shared between compartments: $shared")
            return shared.first()
        }

        fun allItems(): List<Char> = left + right
    }

    fun toRucksack(data: List<String>): List<Rucksack> {
        val rucksacks = data.fold(mutableListOf<Rucksack>()) { ac, s ->
            val charLists = s.toList().chunked(s.length / 2)
            ac += Rucksack(left = charLists[0], right = charLists[1])
            ac
        }
        return rucksacks
    }

    fun createGroups(rucksacks: List<Rucksack>): List<Group> = rucksacks.chunked(3).map { Group(it) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<Rucksack>): Int {
        return data.sumOf { itemScore(it.priorityItem()) }
    }

    fun doPart2(data: List<Rucksack>): Int {
        return createGroups(data).sumOf { itemScore(it.findCommonItem()) }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }
}