package net.fish.geometry.paths

interface PathCreator {
    fun createPath(segments: Int): List<PathData>
}
