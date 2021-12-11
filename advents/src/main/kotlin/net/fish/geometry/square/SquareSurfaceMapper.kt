package net.fish.geometry.square

import net.fish.geometry.grid.GridItem
import net.fish.geometry.grid.GridItemAxis
import net.fish.geometry.paths.PathCreator
import net.fish.geometry.paths.PathData
import net.fish.geometry.projection.SurfaceMapper
import net.fish.geometry.grid.GridType
import net.fish.maths.normalFromPoints
import org.joml.Matrix3f
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SquareSurfaceMapper(
    private var grid: WrappingSquareGrid,
    override var pathCreator: PathCreator,
    private val sweepRadius: Float
) : SurfaceMapper {
    private var centres: Map<Square, Vector3f> = emptyMap()

    override fun grid() = grid
    override fun mappingType() = GridType.SQUARE

    override fun itemToObj(item: GridItem): List<String> {
        val square = item as Square
        val lines = mutableListOf<String>()
        // Generate faces as follows
        // 0/1/4, 1/2/4, 2/3/4, 3/0/4
        // This uses my origin which is SE, NE, NW, SW

        val coordinates = coordinates(square)

        // VERTICES
        for (i in (0..4)) {
            val c = coordinates[i]
            lines += String.format("v %7f %7f %7f", c.x, c.y, c.z)
        }

        // TEXTURES
        lines += "vt 1.0000 1.0000" // point 0
        lines += "vt 1.0000 0.0000" // point 1
        lines += "vt 0.0000 0.0000" // point 2
        lines += "vt 0.0000 1.0000" // point 3
        lines += "vt 0.5000 0.5000" // point 4

        // NORMALS
        for (i in (0..3)) {
            val p1 = coordinates[i]
            val p2 = coordinates[(i + 1) % 4]
            val p3 = coordinates[4]
            val normal = normalFromPoints(p1, p2, p3)
            lines += String.format("vn %5f %5f %5f", normal.x, normal.y, normal.z)
        }

        // FACES are +1 compared to point number. make texture and point same.
        // These are: Vertex Index / Texture Index / Normal Index (all 1 based)
        lines += "f 1/1/1 2/2/1 5/5/1"
        lines += "f 2/2/2 3/3/2 5/5/2"
        lines += "f 3/3/3 4/4/3 5/5/3"
        lines += "f 4/4/4 1/1/4 5/5/4"

        return lines.toList()
    }

    override fun itemAxis(item: GridItem): GridItemAxis {
        val square = item as Square
        val corners = coordinates(square)
        val mp01 = corners[0].add(corners[1], Vector3f()).mul(0.5f)
        val mp12 = corners[1].add(corners[2], Vector3f()).mul(0.5f)
        val mp23 = corners[2].add(corners[3], Vector3f()).mul(0.5f)
        val mp34 = corners[3].add(corners[4], Vector3f()).mul(0.5f)
        val xDir = mp01.sub(mp23).normalize()
        val yDir = mp34.sub(mp12).normalize()
        if (xDir.x.isNaN() || yDir.x.isNaN()) {
            println("ERROR IN NORMALS: square: $square, xDir: $xDir, yDir: $yDir")
        }
        val zDir = xDir.cross(yDir, Vector3f())
        return GridItemAxis(
            location = corners[4],
            axes = Matrix3f(xDir, yDir, zDir)
        )
    }

    private fun coordinates(item: Square): List<Vector3f> {
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

    fun calculateSquareCentres(): Map<Square, Vector3f> {
        val centres = mutableMapOf<Square, Vector3f>()
        val pathCoordinates = pathCreator.createPath(grid.width)
        pathCoordinates.forEachIndexed { segment, pathData ->
            val squares = mutableListOf<Square>()
            var squareToAdd = grid.square(segment, 0)
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
        return String.format("SquareSurfaceMapper[grid: %s]", grid)
    }
}