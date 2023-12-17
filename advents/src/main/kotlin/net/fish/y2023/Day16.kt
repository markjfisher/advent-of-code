package net.fish.y2023

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Direction.EAST
import net.fish.geometry.Direction.NORTH
import net.fish.geometry.Direction.SOUTH
import net.fish.geometry.Direction.WEST
import net.fish.geometry.Point
import net.fish.geometry.edgePoints
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day16 : Day {
    private val data by lazy { resourceLines(2023, 16) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val maze = toBeamMaze(data)
        val beams = maze.traceBeam(Beam(Point(0,0), EAST))
        return maze.countEnergized(beams)
    }
    fun doPart2(data: List<String>): Int {
        val maze = toBeamMaze(data)

        // all points on maze boundary
        return maze.bounds.edgePoints().fold(mutableSetOf<Beam>()) { ac, p ->
            if (p.x == 0) ac += Beam(p, EAST)
            if (p.y == 0) ac += Beam(p, SOUTH)
            if (p.x == maze.width - 1) ac += Beam (p, WEST)
            if (p.y == maze.height - 1) ac += Beam(p, NORTH)
            ac
        }.maxOf { start ->
            val beams = maze.traceBeam(start)
            val max = maze.countEnergized(beams)
            // println ("start: $start -> $max")
            max
        }
    }

    data class Beam(val position: Point, val direction: Direction)

    data class BeamMaze(val mirrors: Map<Point, Char>, val width: Int, val height: Int) {
        val bounds = Pair(Point(0, 0), Point(width - 1, height - 1))

        fun validSplits(beam: Beam): Set<Beam> {
            return setOf(beam.direction.cw(), beam.direction.ccw()).fold(setOf()) { ac, d ->
                val splitPoint = beam.position + d
                if (splitPoint.within(bounds)) ac + Beam(splitPoint, d) else ac
            }
        }

        fun turn(beam: Beam, mirror: Char): Beam? {
            val newDirection = when {
                mirror == '\\' && beam.direction in setOf(NORTH, SOUTH) ->  beam.direction.ccw()
                mirror == '\\' && beam.direction in setOf(EAST, WEST) ->  beam.direction.cw()
                mirror == '/' && beam.direction in setOf(EAST, WEST) ->  beam.direction.ccw()
                mirror == '/' && beam.direction in setOf(NORTH, SOUTH) ->  beam.direction.cw()
                else -> throw Exception("turning a beam with non turning mirror: $mirror at $beam")
            }
            val newPosition = beam.position + newDirection
            return if (newPosition.within(bounds)) Beam(newPosition, newDirection) else null
        }

        // move the starting beam around until we have the full set of Beams it expands into
        // thus we will detect and handle crossing beams, or loops easily
        fun traceBeam(start: Beam): Set<Beam> {
            tailrec fun trace(beam: Beam, untracedBeams: Set<Beam>, visited: MutableSet<Beam>): Set<Beam> {
                // exit if beam is in visited and untraced is empty

                // move a beam once and recurse until:
                //  - outside the bounds
                //  - hits something
                //  - repeats

                // if it splits, follow 1 and add 1 to the untraced beams (if valid) and recurse

                // exit condition
                if (visited.contains(beam)) {
                    return if (untracedBeams.isEmpty()) {
                        visited
                    } else {
                        // or loop more with untraced that were generated from a split
                        val next = untracedBeams.first()
                        trace(next, untracedBeams - next, visited)
                    }
                }

                // this is a new beam location, add it to visited
                visited += beam

                // is it being split?
                val newMazePosition = mirrors[beam.position] ?: '.'
                when {
                    // E/W beam hitting "|"
                    newMazePosition == '|' && beam.direction in setOf(EAST, WEST) -> {
                        val splits = validSplits(beam)
                        // must always be at least 1 good split on a maze
                        val follow = splits.first()
                        // If there were multiple valid splits, add the one we're not tracing to untraced set
                        val newUntraced = untracedBeams + (splits - follow)
                        return trace(follow, newUntraced, visited)
                    }
                    // N/S beam hitting "-"
                    newMazePosition == '-' && beam.direction in setOf(NORTH, SOUTH) -> {
                        val splits = validSplits(beam)
                        // must always be at least 1 good split on a maze
                        val follow = splits.first()
                        // If there were multiple valid splits, add the one we're not tracing to untraced set
                        val newUntraced = untracedBeams + (splits - follow)
                        return trace(follow, newUntraced, visited)
                    }
                    // beam hits turning mirror
                    newMazePosition in setOf('/', '\\') -> {
                        val newBeam = turn(beam, newMazePosition)
                        return if (newBeam == null) {
                            // recurse with another beam from untraced, or exit with visited
                            if (untracedBeams.isEmpty()) {
                                visited
                            } else {
                                val next = untracedBeams.first()
                                trace(next, untracedBeams - next, visited)
                            }
                        } else {
                            trace(newBeam, untracedBeams, visited)
                        }
                    }
                    // either pass through, or empty, so beam just continues on its path
                    else -> {
                        val newPosition = beam.position + beam.direction
                        return if (newPosition.within(bounds)) {
                            trace(Beam(newPosition, beam.direction), untracedBeams, visited)
                        } else {
                            // recurse with another beam from untraced, or exit with visited
                            if (untracedBeams.isEmpty()) {
                                visited
                            } else {
                                val next = untracedBeams.first()
                                trace(next, untracedBeams - next, visited)
                            }
                        }
                    }
                }
            }

            return trace(start, setOf(), mutableSetOf())
        }

        fun countEnergized(beams: Set<Beam>): Int {
            return beamsGrid(beams).count{ it == '#'}
        }

        fun beamsGrid(beams: Set<Beam>): String {
            val beamLocations = beams.map { it.position }.toSet()
            var s = ""
            for (y in 0 until height) {
                for (x in 0 until width) {
                    s += if (beamLocations.contains(Point(x, y))) "#" else "."
                }
                s += "\n"
            }
            // remove final \n
            return s.dropLast(1)
        }

        override fun toString(): String {
            var s = ""
            for (y in 0 until height) {
                for (x in 0 until width) {
                    s += mirrors.getOrDefault(Point(x, y), '.')
                }
                s += "\n"
            }
            // remove final \n
            return s.dropLast(1)
        }
    }

    fun toBeamMaze(data: List<String>): BeamMaze {
        val fullMap = GridDataUtils.mapCharPointsFromLines(data)
        return BeamMaze(fullMap.filterNot { it.value == '.' }, data[0].length, data.size)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // 5356 too low
        println(part1())
        println(part2())
    }

}