@file:JvmName("AdventOfCode2023")
package net.fish.y2018

import net.fish.Reflect
import net.fish.Runner

object AdventOfCode {
    val days = Reflect.getDays(2018)
}

fun main(args: Array<String>) {
    val day = if(args.isEmpty()) 0 else args[0].toInt()
    Runner.run(2018, AdventOfCode.days, day)
}