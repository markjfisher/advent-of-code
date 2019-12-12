package net.fish

open class Day6Base {
    fun createGraph(spaceData: List<String>): Graph {
        // every line represents an object and what it is orbiting, e.g. A)B means B orbits A
        val uniqueObjects = spaceData.map { it.split(")") }.flatten().toHashSet().map { Day6Object(name = it) }
        val orbits = spaceData.map {  line ->
            val parts = line.split(")")
            val parent = uniqueObjects.find { it.name == parts[0] }!!
            val body = uniqueObjects.find { it.name == parts[1] }!!
            Day6Orbit(parent = parent, body = body)
        }
        return Graph(objects = uniqueObjects, orbits = orbits)
    }
}

data class Graph (
    val objects: List<Day6Object>,
    val orbits: List<Day6Orbit>
) {
    fun orbitCounts(): Int {
        return objects.fold(0) { total, o ->
            val count = parentCount(o)
            total + count
        }
    }

    fun parentCount(o: Day6Object): Int {
        var count = 0
        var parent = orbits.find { it.body == o }?.parent
        while (parent != null) {
            count++
            parent = orbits.find { it.body == parent }?.parent
        }
        return count
    }

    fun commonParent(name1: String, name2: String): Day6Object? {
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

    private fun ancestorsOf(obj: Day6Object): List<Day6Object> {
        val ancestors = mutableListOf<Day6Object>()
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
data class Day6Object(
    val name: String
)

// Edges
data class Day6Orbit(
    val body: Day6Object,
    val parent: Day6Object
)
