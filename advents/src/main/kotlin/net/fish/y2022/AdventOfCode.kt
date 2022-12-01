@file:JvmName("AdventOfCode2022")
package net.fish.y2022

import net.fish.Reflect
import net.fish.Runner

object AdventOfCode {
    val days = Reflect.getDays(2022)
}

fun main(args: Array<String>) {
    val day = if(args.isEmpty()) 0 else args[0].toInt()
    Runner.run(2022, AdventOfCode.days, day)
}