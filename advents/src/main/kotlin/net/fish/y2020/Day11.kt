package net.fish.y2020

import net.fish.Day
import net.fish.resourceLines
import net.fish.y2020.Day11.Location.EMPTY
import net.fish.y2020.Day11.Location.FLOOR
import net.fish.y2020.Day11.Location.OCCUPIED
import net.fish.y2020.Day11.Location.OUTSIDE
import java.lang.Exception
import java.lang.Integer.max

typealias SeatPlan<T> = Array<Array<T>>

object Day11 : Day {
    private val seatPlan = toSeatPlan(resourceLines(2020, 11))

    override fun part1() = simulatePeopleP1(seatPlan).countOccupied()
    override fun part2() = simulatePeopleP2(seatPlan).countOccupied()

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

    fun simulatePeopleP1(seatPlan: SeatPlan<Location>): SeatPlan<Location> {
        var newPlan: SeatPlan<Location> = seatPlan.copyPlan()
        do {
            val oldPlan = newPlan.copyPlan()
            newPlan = processPlanP1(newPlan)
        } while (!newPlan.identical(oldPlan))
        return newPlan
    }

    fun processPlanP1(seatPlan: SeatPlan<Location>): SeatPlan<Location> {
        val newPlan = createEmptyPlanFrom(seatPlan)
        lr@ for(row in 0 until seatPlan.rows()) {
            lc@ for (column in 0 until seatPlan.columns()) {
                val currentLocation = locationAt(column, row, seatPlan)
                if (currentLocation == FLOOR) continue@lc
                val around = around(column, row, seatPlan)
                newPlan[row][column] = when {
                    (currentLocation == EMPTY) && (around.count { it == OCCUPIED } == 0) -> OCCUPIED
                    (currentLocation == OCCUPIED) && (around.count { it == OCCUPIED } >= 4) -> EMPTY
                    else -> currentLocation
                }
            }
        }
        return newPlan
    }

    fun simulatePeopleP2(seatPlan: SeatPlan<Location>): SeatPlan<Location> {
        var newPlan: SeatPlan<Location> = seatPlan.copyPlan()
        do {
            val oldPlan = newPlan.copyPlan()
            newPlan = processPlanP2(newPlan)
        } while (!newPlan.identical(oldPlan))
        return newPlan
    }

    fun processPlanP2(seatPlan: SeatPlan<Location>): SeatPlan<Location> {
        val newPlan = createEmptyPlanFrom(seatPlan)
        lr@ for(row in 0 until seatPlan.rows()) {
            lc@ for (column in 0 until seatPlan.columns()) {
                val currentLocation = locationAt(column, row, seatPlan)
                if (currentLocation == FLOOR) continue@lc
                val around = canSee(column, row, seatPlan)
                newPlan[row][column] = when {
                    (currentLocation == EMPTY) && (around.count { it == OCCUPIED } == 0) -> OCCUPIED
                    (currentLocation == OCCUPIED) && (around.count { it == OCCUPIED } >= 5) -> EMPTY
                    else -> currentLocation
                }
            }
        }
        return newPlan
    }

    fun SeatPlan<Location>.rows() = this.size
    fun SeatPlan<Location>.columns() = this[0].size
    fun SeatPlan<Location>.toGrid() {
        for(row in 0 until this.rows()) {
            for(column in 0 until this.columns()) {
                print(locationAt(column, row, this).value)
            }
            println()
        }
        println()
    }

    fun SeatPlan<Location>.copyPlan(): SeatPlan<Location> {
        val newPlan = createEmptyPlanFrom(this)
        for(row in 0 until this.rows()) {
            for(column in 0 until this.columns()) {
                newPlan[row][column] = locationAt(column, row, this)
            }
        }
        return newPlan
    }

    fun SeatPlan<Location>.identical(other: SeatPlan<Location>): Boolean {
        for(row in 0 until this.rows()) {
            for(column in 0 until this.columns()) {
                if (locationAt(column, row, this) != locationAt(column, row, other)) {
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
                if (locationAt(column, row, this) == OCCUPIED) count++
            }
        }
        return count
    }

    fun createEmptyPlanFrom(seatPlan: SeatPlan<Location>): SeatPlan<Location> {
        return Pair(seatPlan.rows(), seatPlan.columns()).createArray(FLOOR)
    }

    fun locationAt(x: Int, y: Int, plan: SeatPlan<Location>): Location {
        return when {
            x >= plan[0].size || x < 0 -> OUTSIDE
            y >= plan.size || y < 0 -> OUTSIDE
            else -> plan[y][x]
        }
    }

    fun around(x: Int, y: Int, seatPlan: SeatPlan<Location>): List<Location> {
        return listOf(
            locationAt(x-1, y-1, seatPlan),
            locationAt(x  , y-1, seatPlan),
            locationAt(x+1, y-1, seatPlan),
            locationAt(x-1, y  , seatPlan),
            // locationAt(x-0, y  , seatPlan),
            locationAt(x+1, y  , seatPlan),
            locationAt(x-1, y+1, seatPlan),
            locationAt(x  , y+1, seatPlan),
            locationAt(x+1, y+1, seatPlan),
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
        for (delta in 1 until max(seatPlan.columns(), seatPlan.rows())) {
            val inD0 = locationAt(x      , y-delta, seatPlan)
            val inD1 = locationAt(x+delta, y-delta, seatPlan)
            val inD2 = locationAt(x+delta, y      , seatPlan)
            val inD3 = locationAt(x+delta, y+delta, seatPlan)
            val inD4 = locationAt(x      , y+delta, seatPlan)
            val inD5 = locationAt(x-delta, y+delta, seatPlan)
            val inD6 = locationAt(x-delta, y      , seatPlan)
            val inD7 = locationAt(x-delta, y-delta, seatPlan)
            if (!hasSeenD0 && inD0 != FLOOR && inD0 != OUTSIDE) { locations.add(inD0); hasSeenD0 = true }
            if (!hasSeenD1 && inD1 != FLOOR && inD1 != OUTSIDE) { locations.add(inD1); hasSeenD1 = true }
            if (!hasSeenD2 && inD2 != FLOOR && inD2 != OUTSIDE) { locations.add(inD2); hasSeenD2 = true }
            if (!hasSeenD3 && inD3 != FLOOR && inD3 != OUTSIDE) { locations.add(inD3); hasSeenD3 = true }
            if (!hasSeenD4 && inD4 != FLOOR && inD4 != OUTSIDE) { locations.add(inD4); hasSeenD4 = true }
            if (!hasSeenD5 && inD5 != FLOOR && inD5 != OUTSIDE) { locations.add(inD5); hasSeenD5 = true }
            if (!hasSeenD6 && inD6 != FLOOR && inD6 != OUTSIDE) { locations.add(inD6); hasSeenD6 = true }
            if (!hasSeenD7 && inD7 != FLOOR && inD7 != OUTSIDE) { locations.add(inD7); hasSeenD7 = true }
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

inline fun<reified T> Pair<Int,Int>.createArray(initialValue:T) = Array(this.first){ Array(this.second){initialValue}}

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