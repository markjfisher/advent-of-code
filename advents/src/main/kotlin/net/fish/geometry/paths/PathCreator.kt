package net.fish.geometry.paths

interface PathCreator {
    fun createPath(segments: Int): List<PathData>
}

object NoPathCreator: PathCreator {
    override fun createPath(segments: Int): List<PathData> {
        return emptyList()
    }

}