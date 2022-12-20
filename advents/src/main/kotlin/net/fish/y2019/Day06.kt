package net.fish.y2019

import net.fish.Day
import net.fish.resourceLines

object Day06: Day {
    private val orbitData by lazy { resourceLines(2019, 6) }
    private val graph by lazy { createGraph(orbitData) }

    override fun part1() = graph.orbitCounts()
    override fun part2() = graph.traverseCount("YOU", "SAN")

    fun createGraph(orbitData: List<String>): Graph {
        // every line represents an object and what it is orbiting, e.g. A)B means B orbits A
        val uniqueObjects = orbitData.map { it.split(")") }.flatten().toHashSet().map { Body(name = it) }
        val orbits = orbitData.map { line ->
            val parts = line.split(")")
            val parent = uniqueObjects.find { it.name == parts[0] }!!
            val body = uniqueObjects.find { it.name == parts[1] }!!
            Orbit(parent = parent, body = body)
        }
        return Graph(objects = uniqueObjects, orbits = orbits)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

data class Graph (
    val objects: List<Body>,
    val orbits: List<Orbit>
) {
    fun orbitCounts(): Int {
        return objects.fold(0) { total, o ->
            val count = parentCount(o)
            total + count
        }
    }

    fun parentCount(o: Body): Int {
        var count = 0
        var parent = orbits.find { it.body == o }?.parent
        while (parent != null) {
            count++
            parent = orbits.find { it.body == parent }?.parent
        }
        return count
    }

    fun commonParent(name1: String, name2: String): Body? {
        val obj1 = objects.find { it.name == name1 }
        val obj2 = objects.find { it.name == name2 }
        if (obj1 == null || obj2 == null) return null

        val ancestors1 = ancestorsOf(obj1)
        val ancestors2 = ancestorsOf(obj2)

        if (ancestors1.isEmpty() || ancestors2.isEmpty()) return null

        var foundCommon = false
        var ancestors1Index = 0
        var potentialCommon = ancestors1[ancestors1Index]
        while (!foundCommon) {
            if (ancestors2.contains(potentialCommon)) {
                foundCommon = true
            } else {
                potentialCommon = ancestors1[++ancestors1Index]
            }
        }
        return if (foundCommon) potentialCommon else null
    }

    private fun ancestorsOf(obj: Body): List<Body> {
        val ancestors = mutableListOf<Body>()
        var parent = orbits.find { it.body == obj }?.parent
        while (parent != null) {
            ancestors.add(parent)
            parent = orbits.find { it.body == parent }?.parent
        }
        return ancestors.toList()
    }

    fun countNodesBetween(name1: String, name2: String): Int {
        val obj1 = objects.find { it.name == name1 }
        val obj2 = objects.find { it.name == name2 }
        if (obj1 == null || obj2 == null) return 0

        val ancestors = ancestorsOf(obj1)
        return ancestors.indexOfFirst { it.name == name2 } + 1
    }

    fun traverseCount(name1: String, name2: String): Int {
        val common = commonParent(name1, name2) ?: return 0

        val fromName1ToCommon: Int = countNodesBetween(name1, common.name)
        val fromCommonToName2: Int = countNodesBetween(name2, common.name)

        return fromName1ToCommon + fromCommonToName2 - 2

    }
}

// Nodes
data class Body(
    val name: String
)

// Edges
data class Orbit(
    val body: Body,
    val parent: Body
)

/*
You've landed at the Universal Orbit Map facility on Mercury. Because navigation in space often involves transferring
between orbits, the orbit maps here are useful for finding efficient routes between, for example, you and Santa.
You download a map of the local orbits (your puzzle input).

Except for the universal Center of Mass (COM), every object in space is in orbit around exactly one other object.
An orbit looks roughly like this:

                  \
                   \
                    |
                    |
AAA--> o            o <--BBB
                    |
                    |
                   /
                  /
In this diagram, the object BBB is in orbit around AAA.
The path that BBB takes around AAA (drawn with lines) is only partly shown.
In the map data, this orbital relationship is written AAA)BBB, which means "BBB is in orbit around AAA".

Before you use your map data to plot a course, you need to make sure it wasn't corrupted during the download.
To verify maps, the Universal Orbit Map facility uses orbit count checksums - the total number of direct orbits
(like the one shown above) and indirect orbits.

Whenever A orbits B and B orbits C, then A indirectly orbits C. This chain can be any number of objects long:
if A orbits B, B orbits C, and C orbits D, then A indirectly orbits D.

For example, suppose you have the following map:

COM)B
B)C
C)D
D)E
E)F
B)G
G)H
D)I
E)J
J)K
K)L
Visually, the above map of orbits looks like this:

        G - H       J - K - L
       /           /
COM - B - C - D - E - F
               \
                I

In this visual representation, when two objects are connected by a line, the one on the right directly orbits the one
on the left.

Here, we can count the total number of orbits as follows:

D directly orbits C and indirectly orbits B and COM, a total of 3 orbits.
L directly orbits K and indirectly orbits J, E, D, C, B, and COM, a total of 7 orbits.
COM orbits nothing.
The total number of direct and indirect orbits in this example is 42.

What is the total number of direct and indirect orbits in your map data?
*/


/*
Now, you just need to figure out how many orbital transfers you (YOU) need to take to get to Santa (SAN).

You start at the object YOU are orbiting; your destination is the object SAN is orbiting. An orbital transfer
lets you move from any object to an object orbiting or orbited by that object.

For example, suppose you have the following map:

COM)B
B)C
C)D
D)E
E)F
B)G
G)H
D)I
E)J
J)K
K)L
K)YOU
I)SAN
Visually, the above map of orbits looks like this:

                          YOU
                         /
        G - H       J - K - L
       /           /
COM - B - C - D - E - F
               \
                I - SAN
In this example, YOU are in orbit around K, and SAN is in orbit around I. To move from K to I, a minimum of 4 orbital
transfers are required:

K to J
J to E
E to D
D to I
Afterward, the map of orbits looks like this:

        G - H       J - K - L
       /           /
COM - B - C - D - E - F
               \
                I - SAN
                 \
                  YOU
What is the minimum number of orbital transfers required to move from the object YOU are orbiting to the object SAN
is orbiting? (Between the objects they are orbiting - not between YOU and SAN.)

*/

