package net.fish.geometry.square

import net.fish.geometry.paths.PathCreator
import net.fish.geometry.paths.PathData
import net.fish.geometry.projection.PathingSurfaceMapper
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class PathingSquareSurfaceMapper(
    grid: WrappingSquareGrid,
    override var pathCreator: PathCreator,
    private val sweepRadius: Float
) : SquareSurfaceMapper(grid), PathingSurfaceMapper {

    private var centres: Map<Square, Vector3f> = emptyMap()

    override fun coordinates(item: Square): List<Vector3f> {
        if (centres.isEmpty()) centres = calculateSquareCentres()
        val neighboursToAverage = listOf(Triple(0, 6, 7), Triple(2, 0, 1), Triple(4, 2, 3), Triple(6, 4, 5))
        val corners = neighboursToAverage.map { neighbours ->
            averageCentres(item, item.neighbour(neighbours.first)!!, item.neighbour(neighbours.second)!!, item.neighbour(neighbours.third)!!)
        }
        return corners + centres[item]!!
    }

    private fun averageCentres(s0: Square, s1: Square, s2: Square, s3: Square): Vector3f {
        val sc0 = centres.getOrDefault(s0, Vector3f())
        val sc1 = centres.getOrDefault(s1, Vector3f())
        val sc2 = centres.getOrDefault(s2, Vector3f())
        val sc3 = centres.getOrDefault(s3, Vector3f())
        return sc0.add(sc1, Vector3f()).add(sc2).add(sc3).div(4f)
    }

    private fun calculateSquareCentres(): Map<Square, Vector3f> {
        val centres = mutableMapOf<Square, Vector3f>()
        val pathCoordinates = pathCreator.createPath(grid.width)
        pathCoordinates.forEachIndexed { segment, pathData ->
            val squares = mutableListOf<Square>()
            var squareToAdd = grid.square(segment, 0)!!
            squares.add(squareToAdd)
            (0 until (grid.height - 1)).forEach { _ ->
                squareToAdd = squareToAdd.neighbour(2)!! // "North" of this square
                squares.add(squareToAdd)
            }
            squares.forEachIndexed { index, square ->
                val theta = 2.0 * PI * (1.0 / grid.height * index)
                centres[square] = calculateSquareCentre(pathData, theta)
            }
        }
        return centres
    }

    private fun calculateSquareCentre(pathData: PathData, theta: Double): Vector3f {
        // This is different to hex version, but tests show it's correct, need to change hex one
        val b = pathData.tangent.cross(pathData.normal, Vector3f())
        val xd = b.mul((sweepRadius * sin(theta) * -1.0).toFloat(), Vector3f())
        val yd = pathData.normal.mul((sweepRadius * cos(theta) * -1.0).toFloat(), Vector3f())
        return xd.add(pathData.point).add(yd)
    }

    override fun toString(): String {
        return String.format("PathingSquareSurfaceMapper[grid: %s]", grid)
    }
}