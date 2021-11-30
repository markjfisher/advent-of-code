package net.fish.geometry.paths

interface PathCreator {
    fun createPath(segments: Int): List<PathData>
}

enum class PathType(val requiredFields: List<String>) {
    TorusKnot(listOf("p", "q", "a", "b")),
    DecoratedTorusKnot(listOf("pattern")),
    SimpleTorus(listOf("majorRadius")),
    Epitrochoid(listOf("a", "b", "c")),
    ThreeFactorParametric(listOf("a", "b", "c")),
    Trefoil(emptyList())
}