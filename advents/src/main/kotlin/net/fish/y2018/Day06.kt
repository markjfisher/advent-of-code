package net.fish.y2018

import net.fish.Day
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.geometry.edgePoints
import net.fish.geometry.points
import net.fish.resourceLines

object Day06 : Day {
    private val data by lazy { toLocations(resourceLines(2018, 6)) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(locations: List<Location>): Int {
        // locations.forEach { println(it) }
        val allLocationsOwners: MutableMap<Point, List<Location>> = mutableMapOf()
        val boundLocations = locations.filter { it.isBounded }
        val boundLocationOwnedPoints: MutableMap<Location, Int> = mutableMapOf()
        // We only need to consider points in the bounds of edge locations
        val bounds = locations.map { it.position }.bounds()
        for (y in bounds.first.y..bounds.second.y) {
            for (x in bounds.first.x..bounds.second.x) {
                val p = Point(x, y)
                val b2 = belongsTo(p, locations)
                allLocationsOwners[p] = b2
                if (b2.size == 1 && boundLocations.contains(b2.first())) {
                    val l = b2.first()
                    val count = boundLocationOwnedPoints.getOrPut(l) { 0 }
                    boundLocationOwnedPoints[l] = count + 1
                }
            }
        }
        return boundLocationOwnedPoints.maxBy { it.value }.value
    }

    fun doPart2(locations: List<Location>): Int {
        // very easy. just find all locations where the sum of MD's to every point is under 10000
        return safeLocations(locations, 10000)
    }

    fun safeLocations(locations: List<Location>, limit: Int) =
        locations.map { it.position }.bounds().points()
            .map { p -> locations.sumOf { p.manhattenDistance(it.position) } }.filter { it < limit }.count()

    data class Location(val position: Point, var isBounded: Boolean)

    private fun belongsTo(p: Point, locations: List<Location>): List<Location> {
        // for this point, find the locations with the lowest manhatten distance
        // gets a map entry of format {2, [Location1, Location2]} for this point
        val lowestMDtoLocations = locations.groupBy {  location ->
            location.position.manhattenDistance(p)
        }.minBy { it.key }
        return lowestMDtoLocations.value
   }

    fun toLocations(data: List<String>): List<Location> {
        val locations = data.map { line ->
            val p = line.split(",", limit = 2).map { it.trim().toInt() }.let { Point(it[0], it[1]) }
            Location(position = p, isBounded = true)
        }

        // go around the bounding edge, and find the closest location to us. that will be an infinite location and thus unbounded
        // Only consider those points not shared, as this could just be the edge of the data
        // My original implementation of considering points that had nothing Up/Down/Left/Right of them didn't work on real data, but did on test :(
        val bounds = locations.map { it.position }.bounds()
        bounds.edgePoints().forEach { edge ->
            val b2 = belongsTo(edge, locations)
            if (b2.size == 1) b2[0].isBounded = false
        }

        return locations
    }

    fun ownershipString(locations: List<Location>): String {
        var output = ""
        val namedLocations = locations.mapIndexed { index, location ->
            location to ('a' + index)
        }.toMap()
        val bounds = locations.map { it.position }.bounds()
        for(y in bounds.first.y - 1 .. bounds.second.y + 1) {
            for (x in bounds.first.x - 1 .. bounds.second.x + 1) {
                val p = Point(x, y)
                val b2 = belongsTo(p, locations)
                output += if (b2.size > 1) "." else {
                    val n = namedLocations[b2.first()]!!
                    if (locations.any { it.position == p }) n.uppercase() else n
                }
            }
            output += "\n"
        }
        return output
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}