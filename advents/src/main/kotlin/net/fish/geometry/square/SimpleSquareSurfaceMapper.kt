package net.fish.geometry.square

import org.joml.Vector3f

class SimpleSquareSurfaceMapper(
    grid: NonWrappingSquareGrid,
    private val scale: Float = 2.0f
) : SquareSurfaceMapper(grid) {

    override fun coordinates(item: Square): List<Vector3f> {
        // return the 5 points of the square: SE, NE, NW, SW, centre,
        val minX = scale / 2.0f * (item.x * 2.0f - grid.width)
        val minY = scale / 2.0f * (item.y * 2.0f - grid.height)
        val maxX = minX + scale
        val maxY = minY + scale
        return listOf(
            Vector3f(maxX, minY, 0f),
            Vector3f(maxX, maxY, 0f),
            Vector3f(minX, maxY, 0f),
            Vector3f(minX, minY, 0f),
            Vector3f((maxX + minX) / 2.0f, (maxY + minY) / 2.0f, 0f)
        )
    }

}