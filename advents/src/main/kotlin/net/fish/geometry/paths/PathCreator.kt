package net.fish.geometry.paths

interface PathCreator {
    fun createPath(segments: Int): List<PathData>
}

class NullPathCreator: PathCreator {
    override fun createPath(segments: Int): List<PathData> = emptyList()
}

enum class PathType(val requiredFields: List<String>) {
    StaticPoint(emptyList()),
    TorusKnot(listOf("p", "q", "a", "b")),
    DecoratedTorusKnot(listOf("pattern")),
    SimpleTorus(listOf("majorRadius")),
    Epitrochoid(listOf("a", "b", "c")),
    ThreeFactorParametric(listOf("a", "b", "c")),
    Trefoil(emptyList())
}