@file:JvmName("Advent2022Day12")
package net.fish.y2022

fun main(args: Array<String>) {
    Day12.visualize = true
    if (args.isEmpty()) {
        throw Exception("Please pass 1 or 2 to indicate which part")
    }
    when (args.first().toInt()) {
        1 -> Day12.part1()
        2 -> Day12.part2()
        else -> throw Exception("1 or 2")
    }
}