@file:JvmName("Advent2022Day14")
package net.fish.y2022

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    Day14.visualize = true
    Day14.displayDrop = true
    Day14.emptyChar = " "
    Day14.wallChar = "█"
    Day14.sandChar = "o"

    if (args.isEmpty()) {
        println("Usage: <class> 1|2 /path/to/input.txt displayDrop sleepTime")
        exitProcess(1)
    }
    Day14.doVisual(args[0], args[1], args[2], args[3])
}