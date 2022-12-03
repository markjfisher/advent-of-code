package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines

object Day03 : Day {
    private val data = toRucksack(resourceLines(2022, 3))

    fun itemScore(item: Char): Int {
        return when(item) {
            in 'a'..'z' -> item - 'a' + 1
            in 'A' .. 'Z' -> item - 'A' + 27
            else -> throw Exception("Unknown item: $item")
        }
    }

    data class Group(val rucksacks: List<Rucksack>) {
        fun findCommonItem(): Char {
            val common = rucksacks[0].allItems().intersect(rucksacks[1].allItems().toSet()).intersect(rucksacks[2].allItems().toSet())
            if (common.size != 1) throw Exception("More than 1 item common in rucksack: $common")
            return common.first()
        }
    }

    data class Compartment(val items: List<Char>)

    data class Rucksack(val left: Compartment, val right: Compartment) {
        fun priorityItem(): Char {
            val shared = left.items.intersect(right.items.toSet())
            if (shared.size != 1) throw Exception("More than 1 item shared between compartments: $shared")
            return shared.first()
        }

        fun allItems(): List<Char> = left.items + right.items
    }

    fun toRucksack(data: List<String>): List<Rucksack> {
        val rucksacks = data.fold(mutableListOf<Rucksack>()) { ac, s ->
            val cL = s.substring(0, s.length / 2)
            val cR = s.substring(s.length / 2, s.length)
            assert(cL.length == cR.length)
            val r = Rucksack(left = Compartment(cL.toList()), right = Compartment(cR.toList()))
            ac += r
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