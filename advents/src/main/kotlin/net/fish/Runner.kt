package net.fish

import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Shamelessly lifted from https://github.com/nielsutrecht/adventofcode/blob/master/src/main/kotlin/com/nibado/projects/advent/Runner.kt

object Runner {
    private const val RESULT_WIDTH = 33
    private const val TIME_WIDTH = 9
    private const val SINGLE_ITER = 1

    private val dayOfWeek = DateTimeFormatter.ofPattern("EE")
    private val format = "%6s: %${RESULT_WIDTH}s %${RESULT_WIDTH}s %${TIME_WIDTH}s %${TIME_WIDTH}s %${TIME_WIDTH}s"

    fun run(year: Int, days: List<Day>, day: Int = 0) {
        println(format.format("Day", "Part 1", "Part 2", "Time", "P1", "P2"))
        if (day == 0) {
            days.forEach { run(year, it) }
        } else {
            if (day < 1 || day > days.size) {
                println("Day can't be less than 1 or larger than ${days.size}")
                return
            }
            (0 until SINGLE_ITER).forEach {
                run(year, days[day - 1])
            }
        }
    }

    private fun run(year: Int, day: Day) {
        val dayName = day.javaClass.simpleName.replace("Day", "").toInt()

        val date = LocalDate.of(year, 12, dayName)
        // warm up the tests and the JIT compiler. This speeds the final run up massively
        repeat((0 until 5).count()) {
            day.part1()
            day.part2()
        }

        val start1 = System.nanoTime()
        val p1 = day.part1()
        val dur1 = System.nanoTime() - start1
        val start2 = System.nanoTime()
        val p2 = day.part2()
        val dur2 = System.nanoTime() - start2

        println(format.format("" + dayName + " " + date.format(dayOfWeek), p1, p2, formatDuration(System.nanoTime() - start1), formatDuration(dur1), (formatDuration(dur2))))
    }
}