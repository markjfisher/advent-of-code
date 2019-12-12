package net.fish

import net.fish.Helpers.loadResourceLinesAsInts

/*
Fuel required to launch a given module is based on its mass. Specifically, to find the fuel
required for a module, take its mass, divide by three, round down, and subtract 2.

For example:

For a mass of 12, divide by 3 and round down to get 4, then subtract 2 to get 2.
For a mass of 14, dividing by 3 and rounding down still yields 4, so the fuel required is also 2.
For a mass of 1969, the fuel required is 654.
For a mass of 100756, the fuel required is 33583.
The Fuel Counter-Upper needs to know the total fuel requirement. To find it, individually calculate
the fuel needed for the mass of each module (your puzzle input), then add together all the fuel values.

What is the sum of the fuel requirements for all of the modules on your spacecraft?
 */

object Day1A {
    @JvmStatic
    fun main(args: Array<String>) {
        val fuel = calculateFuel(loadResourceLinesAsInts("/day1-input.txt"))
        println(fuel) // 3363929
    }

    fun calculateFuel(masses: List<Int>): Int {
        return masses.fold(0) { total, mass ->
            val fuelForMass = mass / 3 - 2
            total + fuelForMass
        }
    }

}