package net.fish

import com.github.ajalt.mordant.rendering.BorderType.Companion.SQUARE
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

// Shamelessly lifted from https://github.com/nielsutrecht/adventofcode/blob/master/src/main/kotlin/com/nibado/projects/advent/Runner.kt

object Runner {
    private val dayOfWeek = DateTimeFormatter.ofPattern("EE")

    fun run(year: Int, days: List<Day>, day: Int = 0) {
        val results = if (day == 0) {
            days.map {
                run(it)
            }
        } else {
            if (day < 1 || day > days.size) {
                println("Day can't be less than 1 or larger than ${days.size}")
                exitProcess(1)
            }
            listOf(run(days[day - 1]))
        }

        val termWidth = System.getenv("AOC_WIDTH")?.toInt() ?: 150
        val t = Terminal(width = termWidth)

        val tb = table {
            borderType = SQUARE
            align = TextAlign.RIGHT

            column(0) {
                align = TextAlign.RIGHT
                cellBorders = Borders.NONE
                style(TextColors.magenta)
                width = ColumnWidth.Expand(1)
            }
            column(1) {
                width = ColumnWidth.Expand(2)
            }
            column(2) {
                width = ColumnWidth.Expand(2)
            }
            column(3) {
                width = ColumnWidth.Expand(2)
            }
            column(4) {
                width = ColumnWidth.Expand(5)
            }
            column(5) {
                width = ColumnWidth.Expand(5)
            }
            header {
                style(TextColors.magenta)
                row("", "p1", "p2", "total", "result 1", "result 2")
            }
            body {
                rowStyles(TextColors.white, TextColors.brightWhite)
                results.forEach { result ->
                    val date = LocalDate.of(year, 12, result.dayNumber)
                    val d = "" + result.dayNumber + " " + date.format(dayOfWeek)
                    row(d, formatDuration(result.d1), formatDuration(result.d2), formatDuration(result.d1 + result.d2), result.p1, result.p2)
                }
            }
        }
        t.println(tb)
    }

    private fun run(day: Day): Result {
        val dayNumber = day.javaClass.simpleName.replace("Day", "").toInt()

        // warm up the tests and the JIT compiler. This usually speeds the final run up considerably
        repeat((0 until day.warmUps).count()) {
            day.part1()
            day.part2()
        }

        val start1 = System.nanoTime()
        val p1 = day.part1()
        val dur1 = System.nanoTime() - start1
        val start2 = System.nanoTime()
        val p2 = day.part2()
        val dur2 = System.nanoTime() - start2

        return Result(dayNumber, p1, p2, dur1, dur2)
    }

    data class Result(val dayNumber: Int, val p1: Any, val p2: Any, val d1: Long, val d2: Long)
}

fun formatDuration(nanos: Long): String {
    val d = Duration.ofNanos(nanos)
    val ms = nanos / 1_000_000.0
    return when {
        ms > 60000 -> String.format("%s m %s s", d.toMinutes(), d.minusMinutes(d.toMinutes()).seconds)
        ms > 10000 -> String.format("%s s", d.seconds)
        ms > 1000 -> String.format("%.2f s", ms / 1000.0)
        else -> String.format("%.2f ms", ms)
    }
}