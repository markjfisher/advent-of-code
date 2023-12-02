package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines

object Day02 : Day {
    private val data by lazy { toGames(resourceLines(2023, 2)) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(games: List<Game>): Int {
        val valid = games.filter { it.subGames.all { sg -> sg.red <= 12 && sg.green <= 13 && sg.blue <= 14 } }
        return valid.sumOf { it.id }
    }
    fun doPart2(games: List<Game>): Long {
        val reds = games.map { it.subGames.maxOfOrNull { sg -> sg.red } }
        val greens = games.map { it.subGames.maxOfOrNull { sg -> sg.green } }
        val blues = games.map { it.subGames.maxOfOrNull { sg -> sg.blue } }
        return games.indices.sumOf { index ->
            1L * reds[index]!! * greens[index]!! * blues[index]!!
        }
    }

    data class SubGame(val red: Int, val green: Int, val blue: Int)
    data class Game(val id: Int, val subGames: List<SubGame>)

    fun toGames(lines: List<String>): List<Game> = lines.fold(listOf<Game>()) { ac, line ->
        val parts = line.split(":")
        val gameId = parts[0].trim().split(" ")[1].toInt()
        val subGamesStr = parts[1].trim()

        val subGames = subGamesStr.split(";").map { subGameStr ->
            val scores = subGameStr.trim().split(",").map { it.trim() }
            val red = scores.find { it.contains("red", ignoreCase = true) }?.split(" ")?.first()?.toIntOrNull() ?: 0
            val green = scores.find { it.contains("green", ignoreCase = true) }?.split(" ")?.first()?.toIntOrNull() ?: 0
            val blue = scores.find { it.contains("blue", ignoreCase = true) }?.split(" ")?.first()?.toIntOrNull() ?: 0
            SubGame(red, green, blue)
        }
        ac + Game(gameId, subGames)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}