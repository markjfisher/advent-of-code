@file:JvmName("AdventOfCode2021")
package net.fish.y2021

import net.fish.Reflect
import net.fish.Runner

object AdventOfCode {
    val days = Reflect.getDays(2021)
}

fun main(args: Array<String>) {
    val day = if(args.isEmpty()) 0 else args[0].toInt()
    Runner.run(2021, AdventOfCode.days, day)
}