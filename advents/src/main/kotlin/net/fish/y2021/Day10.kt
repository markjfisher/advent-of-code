package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines

object Day10 : Day {
    private val data by lazy { resourceLines(2021, 10) }
    private val bracketScores = mapOf(')' to 3L, ']' to 57L, '}' to 1197L, '>' to 25137L)
    private val closeChars = bracketScores.keys
    private val matchingOpenBracket = mapOf(')' to '(', '}' to '{', ']' to '[', '>' to '<')

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Long {
        val score = data.fold(0L) { acc, line ->
            acc + scoreLine(line).first
        }
        return score
    }

    fun scoreLine(line: String): Pair<Long, List<Char>> {
        val charIter = line.iterator()
        val openChars: MutableList<Char> = mutableListOf()
        var isCorrupt = false
        var score = 0L
        while (!isCorrupt && charIter.hasNext()) {
            val nextChar = charIter.next()
            when {
                isCloseChar(nextChar) -> {
                    // either matches latest open bracket, or it's corrupt
                    if (openChars.lastOrNull() == matchingOpenBracket[nextChar]!!) {
                        openChars.removeAt(openChars.size - 1)
                    } else {
                        isCorrupt = true
                        score = bracketScores[nextChar]!!
                    }
                }
                else -> openChars += nextChar
            }
        }
        return Pair(score, openChars)
    }

    private fun isCloseChar(c: Char): Boolean = closeChars.contains(c)

    fun doPart2(data: List<String>): Long {
        val scoreMap = mapOf('(' to 1L, '[' to 2L, '{' to 3L, '<' to 4L)
        val scores = data.mapNotNull { line ->
            val score = scoreLine(line)
            if (score.first == 0L) {
                score.second.reversed().fold(0L) { acc, c ->
                    acc * 5L + scoreMap[c]!!
                }
            } else null
        }
        // middle value is required
        return scores.sorted()[scores.size / 2]
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}