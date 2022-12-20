package net.fish.y2020

import net.fish.Day
import net.fish.maths.createArray
import net.fish.resourceLines
import net.fish.y2020.Day11.Location
import net.fish.y2020.Day11.Location.EMPTY
import net.fish.y2020.Day11.Location.FLOOR
import net.fish.y2020.Day11.Location.OCCUPIED
import net.fish.y2020.Day11.Location.OUTSIDE
import java.lang.Exception
import java.lang.Integer.max

typealias SeatPlan<T> = Array<Array<T>>

object Day11 : Day {
    private val seatPlan by lazy { toSeatPlan(resourceLines(2020, 11)) }

    override fun part1() = simulatePassengers(seatPlan, 4, ::around).countOccupied()
    override fun part2() = simulatePassengers(seatPlan, 5, ::canSee).countOccupied()

    fun toSeatPlan(rawData: List<String>): SeatPlan<Location> {
        val columns = rawData[0].length
        val rows = rawData.size
        val locations = Pair(rows, columns).createArray(FLOOR)
        rawData.forEachIndexed { row, line ->
            line.forEachIndexed { column, c ->
                locations[row][column] = Location.from(c)
            }
        }
        return locations
    }

    fun simulatePassengers(seatPlan: SeatPlan<Location>, tollerance: Int, los: (Int, Int, SeatPlan<Location>) -> List<Location>): SeatPlan<Location> {
        var newPlan: SeatPlan<Location> = seatPlan.copyPlan()
        var iterations = 0
        do {
            val oldPlan = newPlan.copyPlan()
            newPlan = iteratePlan(newPlan, tollerance, los)
            iterations++
        } while (!newPlan.identical(oldPlan))
        // println("p2: $iterations")
        return newPlan
    }

    fun iteratePlan(seatPlan: SeatPlan<Location>, tollerance: Int, los: (Int, Int, SeatPlan<Location>) -> List<Location>): SeatPlan<Location> {
        val newPlan = createEmptyPlanFrom(seatPlan)
        lr@ for(row in 0 until seatPlan.rows()) {
            lc@ for (column in 0 until seatPlan.columns()) {
                val currentLocation = seatPlan.locationAt(column, row)
                if (currentLocation == FLOOR) continue@lc
                val seenLocations = los(column, row, seatPlan)
                newPlan[row][column] = when {
                    (currentLocation == EMPTY) && (seenLocations.count { it == OCCUPIED } == 0) -> OCCUPIED
                    (currentLocation == OCCUPIED) && (seenLocations.count { it == OCCUPIED } >= tollerance) -> EMPTY
                    else -> currentLocation
                }
            }
        }
        return newPlan
    }

    fun createEmptyPlanFrom(seatPlan: SeatPlan<Location>): SeatPlan<Location> {
        return Pair(seatPlan.rows(), seatPlan.columns()).createArray(FLOOR)
    }

    fun around(x: Int, y: Int, seatPlan: SeatPlan<Location>): List<Location> {
        return listOf(
            seatPlan.locationAt(x-1, y-1),
            seatPlan.locationAt(x, y-1),
            seatPlan.locationAt(x+1, y-1),
            seatPlan.locationAt(x-1, y),
            // locationAt(x-0, y  , seatPlan),
            seatPlan.locationAt(x+1, y),
            seatPlan.locationAt(x-1, y+1),
            seatPlan.locationAt(x, y+1),
            seatPlan.locationAt(x+1, y+1),
        )
    }

    fun canSee(x: Int, y: Int, seatPlan: SeatPlan<Location>): List<Location> {
        val locations = mutableListOf<Location>()
        var hasSeenD0 = false
        var hasSeenD1 = false
        var hasSeenD2 = false
        var hasSeenD3 = false
        var hasSeenD4 = false
        var hasSeenD5 = false
        var hasSeenD6 = false
        var hasSeenD7 = false
        var inD0: Location = FLOOR
        var inD1: Location = FLOOR
        var inD2: Location = FLOOR
        var inD3: Location = FLOOR
        var inD4: Location = FLOOR
        var inD5: Location = FLOOR
        var inD6: Location = FLOOR
        var inD7: Location = FLOOR
        for (delta in 1 until max(seatPlan.columns(), seatPlan.rows())) {
            if (!hasSeenD0) inD0 = seatPlan.locationAt(x, y-delta)
            if (!hasSeenD1) inD1 = seatPlan.locationAt(x+delta, y-delta)
            if (!hasSeenD2) inD2 = seatPlan.locationAt(x+delta, y)
            if (!hasSeenD3) inD3 = seatPlan.locationAt(x+delta, y+delta)
            if (!hasSeenD4) inD4 = seatPlan.locationAt(x, y+delta)
            if (!hasSeenD5) inD5 = seatPlan.locationAt(x-delta, y+delta)
            if (!hasSeenD6) inD6 = seatPlan.locationAt(x-delta, y)
            if (!hasSeenD7) inD7 = seatPlan.locationAt(x-delta, y-delta)
            if (!hasSeenD0 && inD0 != FLOOR) { if (inD0 != OUTSIDE) locations.add(inD0); hasSeenD0 = true }
            if (!hasSeenD1 && inD1 != FLOOR) { if (inD1 != OUTSIDE) locations.add(inD1); hasSeenD1 = true }
            if (!hasSeenD2 && inD2 != FLOOR) { if (inD2 != OUTSIDE) locations.add(inD2); hasSeenD2 = true }
            if (!hasSeenD3 && inD3 != FLOOR) { if (inD3 != OUTSIDE) locations.add(inD3); hasSeenD3 = true }
            if (!hasSeenD4 && inD4 != FLOOR) { if (inD4 != OUTSIDE) locations.add(inD4); hasSeenD4 = true }
            if (!hasSeenD5 && inD5 != FLOOR) { if (inD5 != OUTSIDE) locations.add(inD5); hasSeenD5 = true }
            if (!hasSeenD6 && inD6 != FLOOR) { if (inD6 != OUTSIDE) locations.add(inD6); hasSeenD6 = true }
            if (!hasSeenD7 && inD7 != FLOOR) { if (inD7 != OUTSIDE) locations.add(inD7); hasSeenD7 = true }
            if (hasSeenD0 && hasSeenD1 && hasSeenD2 && hasSeenD3 && hasSeenD4 && hasSeenD5 && hasSeenD6  && hasSeenD7) break
        }

        return locations
    }

    enum class Location(val value: Char) {
        FLOOR('.'), EMPTY('L'), OCCUPIED('#'), OUTSIDE('X');

        companion object {
            fun from(v: Char) = values().find { it.value == v } ?: throw Exception("Unknown value $v")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

fun SeatPlan<Location>.rows() = this.size
fun SeatPlan<Location>.columns() = this[0].size

fun SeatPlan<Location>.locationAt(x: Int, y: Int): Location {
    return when {
        x >= this[0].size || x < 0 -> OUTSIDE
        y >= size || y < 0 -> OUTSIDE
        else -> this[y][x]
    }
}

fun SeatPlan<Location>.printPlan() {
    for(row in 0 until this.rows()) {
        for(column in 0 until this.columns()) {
            print(this.locationAt(column, row).value)
        }
        println()
    }
    println()
}

fun SeatPlan<Location>.copyPlan(): SeatPlan<Location> {
    val newPlan = Day11.createEmptyPlanFrom(this)

    this.forEachIndexed { x, y, location ->
        newPlan[y][x] = location
    }

    return newPlan
}

fun SeatPlan<Location>.identical(other: SeatPlan<Location>): Boolean {
    for(row in 0 until this.rows()) {
        for(column in 0 until this.columns()) {
            if (this.locationAt(column, row) != other.locationAt(column, row)) {
                return false
            }
        }
    }
    return true
}

fun SeatPlan<Location>.countOccupied(): Int {
    var count = 0
    for(row in 0 until this.rows()) {
        for(column in 0 until this.columns()) {
            if (this.locationAt(column, row) == OCCUPIED) count++
        }
    }
    return count
}

inline fun <R> SeatPlan<Location>.forEachIndexed(action: (Int, Int, Location) -> R) {
    for(row in 0 until this.rows()) {
        for (column in 0 until this.columns()) {
            action(column, row, this.locationAt(column, row))
        }
    }
}

/*

Your plane lands with plenty of time to spare. The final leg of your journey is a ferry that goes directly
to the tropical island where you can finally start your vacation. As you reach the waiting area to board
the ferry, you realize you're so early, nobody else has even arrived yet!

By modeling the process people use to choose (or abandon) their seat in the waiting area, you're pretty
sure you can predict the best place to sit. You make a quick map of the seat layout (your puzzle input).

The seat layout fits neatly on a grid. Each position is either floor (.), an empty seat (L), or an occupied
seat (#). For example, the initial seat layout might look like this:

L.LL.LL.LL
LLLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLLL
L.LLLLLL.L
L.LLLLL.LL

Now, you just need to model the people who will be arriving shortly. Fortunately, people are entirely predictable
and always follow a simple set of rules. All decisions are based on the number of occupied seats adjacent to a given
seat (one of the eight positions immediately up, down, left, right, or diagonal from the seat). The following
rules are applied to every seat simultaneously:

If a seat is empty (L) and there are no occupied seats adjacent to it, the seat becomes occupied.
If a seat is occupied (#) and four or more seats adjacent to it are also occupied, the seat becomes empty.
Otherwise, the seat's state does not change.
Floor (.) never changes; seats don't move, and nobody sits on the floor.

After one round of these rules, every seat in the example layout becomes occupied:

#.##.##.##
#######.##
#.#.#..#..
####.##.##
#.##.##.##
#.#####.##
..#.#.....
##########
#.######.#
#.#####.##

After a second round, the seats with four or more occupied adjacent seats become empty again:

#.LL.L#.##
#LLLLLL.L#
L.L.L..L..
#LLL.LL.L#
#.LL.LL.LL
#.LLLL#.##
..L.L.....
#LLLLLLLL#
#.LLLLLL.L
#.#LLLL.##

This process continues for three more rounds:

#.##.L#.##
#L###LL.L#
L.#.#..#..
#L##.##.L#
#.##.LL.LL
#.###L#.##
..#.#.....
#L######L#
#.LL###L.L
#.#L###.##

#.#L.L#.##
#LLL#LL.L#
L.L.L..#..
#LLL.##.L#
#.LL.LL.LL
#.LL#L#.##
..L.L.....
#L#LLLL#L#
#.LLLLLL.L
#.#L#L#.##

#.#L.L#.##
#LLL#LL.L#
L.#.L..#..
#L##.##.L#
#.#L.LL.LL
#.#L#L#.##
..L.L.....
#L#L##L#L#
#.LLLLLL.L
#.#L#L#.##

At this point, something interesting happens: the chaos stabilizes and further applications of these rules cause
no seats to change state! Once people stop moving around, you count 37 occupied seats.

Simulate your seating area by applying the seating rules repeatedly until no seats change state. How many seats
end up occupied?
 */





/*
As soon as people start to arrive, you realize your mistake. People don't just care about adjacent seats -
they care about the first seat they can see in each of those eight directions!

Now, instead of considering just the eight immediately adjacent seats, consider the first seat in each of
those eight directions. For example, the empty seat below would see eight occupied seats:

.......#.
...#.....
.#.......
.........
..#L....#
....#....
.........
#........
...#.....

The leftmost empty seat below would only see one empty seat, but cannot see any of the occupied ones:

.............
.L.L.#.#.#.#.
.............

The empty seat below would see no occupied seats:

.##.##.
#.#.#.#
##...##
...L...
##...##
#.#.#.#
.##.##.

Also, people seem to be more tolerant than you expected: it now takes five or more visible occupied seats
for an occupied seat to become empty (rather than four or more from the previous rules).

The other rules still apply: empty seats that see no occupied seats become occupied, seats matching no rule
don't change, and floor never changes.

Given the same starting layout as above, these new rules cause the seating area to shift around as follows:

L.LL.LL.LL
LLLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLLL
L.LLLLLL.L
L.LLLLL.LL

#.##.##.##
#######.##
#.#.#..#..
####.##.##
#.##.##.##
#.#####.##
..#.#.....
##########
#.######.#
#.#####.##

#.LL.LL.L#
#LLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLL#
#.LLLLLL.L
#.LLLLL.L#

#.L#.##.L#
#L#####.LL
L.#.#..#..
##L#.##.##
#.##.#L.##
#.#####.#L
..#.#.....
LLL####LL#
#.L#####.L
#.L####.L#

#.L#.L#.L#
#LLLLLL.LL
L.L.L..#..
##LL.LL.L#
L.LL.LL.L#
#.LLLLL.LL
..L.L.....
LLLLLLLLL#
#.LLLLL#.L
#.L#LL#.L#

#.L#.L#.L#
#LLLLLL.LL
L.L.L..#..
##L#.#L.L#
L.L#.#L.L#
#.L####.LL
..#.#.....
LLL###LLL#
#.LLLLL#.L
#.L#LL#.L#

#.L#.L#.L#
#LLLLLL.LL
L.L.L..#..
##L#.#L.L#
L.L#.LL.L#
#.LLLL#.LL
..#.L.....
LLL###LLL#
#.LLLLL#.L
#.L#LL#.L#

Again, at this point, people stop shifting around and the seating area reaches equilibrium.
Once this occurs, you count 26 occupied seats.

Given the new visibility method and the rule change for occupied seats becoming empty, once
equilibrium is reached, how many seats end up occupied?


 */