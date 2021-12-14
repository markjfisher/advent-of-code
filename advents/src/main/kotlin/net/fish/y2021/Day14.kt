package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines

object Day14 : Day {
    private val data = resourceLines(2021, 14)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val ti = parseInput(data)
        val letterLengths = grow(10, ti).groupingBy { it }.eachCount().values
        val minSize = letterLengths.minOrNull() ?: throw Exception("crazy min")
        val maxSize = letterLengths.maxOrNull() ?: throw Exception("crazy max")
        return maxSize - minSize
    }

    fun doPart2(data: List<String>): Long = 0

    fun parseInput(data: List<String>): TemplateInstructions {
        var template = ""
        val insertions = mutableMapOf<String, String>()
        data.forEach { line ->
            when {
                line.contains("->") -> insertions[line.split(" -> ")[0]] = line.split(" -> ")[1]
                line.isNotEmpty() -> template = line
            }
        }
        return TemplateInstructions(template, insertions)
    }

    data class TemplateInstructions(
        val template: String,
        val insertions: Map<String, String>
    )

    fun grow(iterations: Int, templateInstructions: TemplateInstructions): String {
        tailrec fun doGrow(pairs: List<String>, newTemplate: String): String {
            if (pairs.isEmpty()) return newTemplate
            val currentPair = pairs.first()
            val insertion = templateInstructions.insertions[currentPair]!!
            val reducedPairs = pairs.drop(1)
            val lastChar = if(reducedPairs.isEmpty()) currentPair[1] else ""
            return doGrow(reducedPairs, "${newTemplate}${currentPair[0]}${insertion}${lastChar}")
        }

        return (0 until iterations).fold(templateInstructions.template) { template, iter ->
            doGrow(template.windowed(2, 1), "")
        }

    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}