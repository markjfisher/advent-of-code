package net.fish.y2019

import net.fish.Reflect
import net.fish.Runner

object AdventOfCode {
    val days = Reflect.getDays(2019)
}

fun main(args: Array<String>) {
    val day = if(args.isEmpty()) 0 else args[0].toInt()
    Runner.run(2019, AdventOfCode.days, day)
}