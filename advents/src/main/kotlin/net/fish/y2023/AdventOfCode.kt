@file:JvmName("AdventOfCode2023")
package net.fish.y2023

import net.fish.Reflect
import net.fish.Runner

object AdventOfCode {
    val days = Reflect.getDays(2023)
}

fun main(args: Array<String>) {
    // WindowsAnsi.setup()
    val day = if(args.isEmpty()) 0 else args[0].toInt()
    Runner.run(2023, AdventOfCode.days, day)
}