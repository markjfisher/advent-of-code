@file:JvmName("AdventOfCode2024")
package net.fish.y2024

import net.fish.Reflect
import net.fish.Runner

object AdventOfCode {
    val days = Reflect.getDays(2024)
}

fun main(args: Array<String>) {
    // WindowsAnsi.setup()
    val day = if(args.isEmpty()) 0 else args[0].toInt()
    Runner.run(2024, AdventOfCode.days, day)
}