package net.fish.y2019

import net.fish.*
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.bounds

object Day11: Day {
    private val program by lazy { resourceString(2019, 11).split(",").map { it.toLong() } }

    override fun part1(): Any {
        return run(0).grid.keys.size
    }

    override fun part2(): Any {
        val grid = run(1)
        val bounds = grid.grid.keys.bounds()
        var output = "\n"
        for(y in bounds.first.y .. bounds.second.y) {
            for (x in bounds.first.x .. bounds.second.x) {
                val t = if (grid.get(Point(x, y)) == 1) "█" else " "
                output += t
            }
            output += "\n"
        }
        return output
    }

    private fun run(initialColour: Long): Grid {
        val grid = Grid(mutableMapOf())
        val bot = Bot(Point(0, 0), Direction.NORTH)

        val computer = AdventComputer(program, listOf(initialColour))
        while(computer.running) {
            computer.run()
            grid.set(bot.position, computer.out().toInt())
            bot.move(computer.out().toInt())
            computer.addInput(grid.get(bot.position).toLong())
        }

        return grid
    }

    data class Grid(val grid: MutableMap<Point, Int>) {
        fun get(pos: Point) = grid.computeIfAbsent(pos) { 0 }
        fun set(pos: Point, v: Int) {
            grid[pos] = v
        }
    }

    data class Bot(
        var position: Point,
        var direction: Direction
    ) {
        fun move(d: Int) {
            direction = if (d == 0) direction.ccw() else direction.cw()
            position += direction
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

/*
On the way to Jupiter, you're pulled over by the Space Police.

"Attention, unmarked spacecraft! You are in violation of Space Law! All spacecraft must have a clearly visible registration identifier!
You have 24 hours to comply or be sent to Space Jail!"

Not wanting to be sent to Space Jail, you radio back to the Elves on Earth for help. Although it takes almost three hours for their
reply signal to reach you, they send instructions for how to power up the emergency hull painting robot and even provide a small
Intcode program (your puzzle input) that will cause it to paint your ship appropriately.

There's just one problem: you don't have an emergency hull painting robot.

You'll need to build a new emergency hull painting robot. The robot needs to be able to move around on the grid of square panels
on the side of your ship, detect the color of its current panel, and paint its current panel black or white. (All of the panels are
currently black.)

The Intcode program will serve as the brain of the robot. The program uses input instructions to access the robot's camera:
provide 0 if the robot is over a black panel or 1 if the robot is over a white panel. Then, the program will output two values:

First, it will output a value indicating the color to paint the panel the robot is over: 0 means to paint the panel black,
and 1 means to paint the panel white.

Second, it will output a value indicating the direction the robot should turn: 0 means it should turn left 90 degrees,
and 1 means it should turn right 90 degrees.

After the robot turns, it should always move forward exactly one panel. The robot starts facing up.

The robot will continue running for a while like this and halt when it is finished drawing. Do not restart the Intcode
computer inside the robot during this process.

For example, suppose the robot is about to start running. Drawing black panels as ., white panels as #, and the robot
pointing the direction it is facing (< ^ > v), the initial state and region near the robot looks like this:

.....
.....
..^..
.....
.....

The panel under the robot (not visible here because a ^ is shown instead) is also black, and so any input instructions at
this point should be provided 0. Suppose the robot eventually outputs 1 (paint white) and then 0 (turn left).

After taking these actions and moving forward one panel, the region now looks like this:

.....
.....
.<#..
.....
.....
Input instructions should still be provided 0. Next, the robot might output 0 (paint black) and then 0 (turn left):

.....
.....
..#..
.v...
.....

After more outputs (1,0, 1,0):

.....
.....
..^..
.##..
.....
The robot is now back where it started, but because it is now on a white panel, input instructions should be provided 1.
After several more outputs (0,1, 1,0, 1,0), the area looks like this:

.....
..<#.
...#.
.##..
.....
Before you deploy the robot, you should probably have an estimate of the area it will cover: specifically, you need to know
the number of panels it paints at least once, regardless of color. In the example above, the robot painted 6 panels at least once.
(It painted its starting panel twice, but that panel is still only counted once; it also never painted the panel it ended on.)

Build a new emergency hull painting robot and run the Intcode program on it. How many panels does it paint at least once?
 */

/*

You're not sure what it's trying to paint, but it's definitely not a registration identifier. The Space Police are getting impatient.

Checking your external ship cameras again, you notice a white panel marked "emergency hull painting robot starting panel". The rest
of the panels are still black, but it looks like the robot was expecting to start on a white panel, not a black one.

Based on the Space Law Space Brochure that the Space Police attached to one of your windows, a valid registration identifier
is always eight capital letters. After starting the robot on a single white panel instead, what registration identifier does
it paint on your hull?

 */