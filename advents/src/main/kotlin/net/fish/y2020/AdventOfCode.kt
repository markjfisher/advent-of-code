@file:JvmName("AdventOfCode2020")
package net.fish.y2020

import net.fish.Reflect
import net.fish.Runner

object AdventOfCode {
    val days = Reflect.getDays(2020)
}

fun main(args: Array<String>) {
    val day = if(args.isEmpty()) 0 else args[0].toInt()
    Runner.run(2020, AdventOfCode.days, day)
}