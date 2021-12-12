package net.fish

interface Day {
    fun part1(): Any
    fun part2(): Any

    val warmUps: Int
        get() = 5
}