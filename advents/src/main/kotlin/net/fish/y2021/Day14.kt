package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines

object Day14 : Day {
    private val data = resourceLines(2021, 14)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Long = solution(data, 10)
    fun doPart2(data: List<String>): Long = solution(data, 40)

    private fun solution(data: List<String>, iterations: Int): Long {
        val ti = parseInput(data)
        val counts = grow(iterations, ti)
        val minSize = counts.minByOrNull { it.value } ?: throw Exception("crazy min")
        val maxSize = counts.maxByOrNull { it.value } ?: throw Exception("crazy max")
        return maxSize.value - minSize.value
    }

    fun parseInput(data: List<String>): TemplateInstructions {
        var template = ""
        val insertions = mutableMapOf<String, List<String>>()
        data.forEach { line ->
            when {
                line.contains("->") -> {
                    val (pair, newCharAsStr) = line.split(" -> ")
                    insertions[pair] = listOf(pair[0] + newCharAsStr, newCharAsStr + pair[1])
                }
                line.isNotEmpty() -> template = line
            }
        }
        return TemplateInstructions(template, insertions)
    }

    data class TemplateInstructions(
        val template: String,
        val insertions: Map<String, List<String>>
    )

    fun grow(iterations: Int, templateInstructions: TemplateInstructions): Map<Char, Long> {
        val counts = templateInstructions.template.windowed(2, 1).groupBy { it }.map { it.key to it.value.size.toLong() }.toMap()
        val grown = (0 until iterations).fold(counts) { c, _ ->
            val newCounts = mutableMapOf<String, Long>()
            c.forEach { (pair, pairCount) ->
                templateInstructions.insertions[pair]!!.forEach { newPair ->
                    newCounts[newPair] = newCounts.getOrDefault(newPair, 0L) + pairCount
                }
            }
            newCounts
        }

        // convert the pair counts into letters count. Care on last letter! it doesn't overlap, so has to be incremented at the end
        val lettersCount = grown.map { it.key.first() to it.value }
            .groupBy { it.first }
            .map { it.key to it.value.sumOf { (_, c) -> c } }.toMap()
            .toMutableMap()
            .also { c2l ->
                val lastChar = templateInstructions.template.last()
                c2l[lastChar] = c2l.getOrDefault(lastChar, 0) + 1
            }

        return lettersCount

    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}