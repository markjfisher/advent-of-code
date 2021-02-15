package net.fish.geometry.hex

import net.fish.geometry.grid.GridItem
import net.fish.geometry.grid.GridItemAxis
import net.fish.geometry.paths.PathCreator
import net.fish.geometry.paths.PathData
import net.fish.geometry.projection.SurfaceMapper
import net.fish.geometry.projection.SurfaceType
import net.fish.maths.normalFromPoints
import org.joml.Matrix3f
import org.joml.Vector3f
import java.io.OutputStream
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class HexSurfaceMapper(
    private var grid: WrappingHexGrid,
    override var pathCreator: PathCreator,
    private val sweepRadius: Float
): SurfaceMapper {
    private var hexCentres: Map<Hex, Vector3f> = emptyMap()

    override fun grid() = grid
    override fun mappingType() = SurfaceType.HEX
    override fun init() {
        hexCentres = emptyMap()
    }

    override fun toString(): String {
        return String.format("HexSurfaceMapper[grid: %s]", grid)
    }

    override fun itemToObj(item: GridItem): List<String> {
        val hex = item as Hex
        val lines = mutableListOf<String>()
        // Generate faces as follows to be more rounded than the 5 version by blender which doesn't bend well
        // 0/1/6, 1/2/6, 2/3/6, 3/4/6, 4/5/6, 5/0/6
        // This uses my origin which is SE for pointy, E for flat

        val coordinates = coordinates(hex)

        // vetices (last one is the centre)
        for(i in (0..6)) {
            val c = coordinates[i]
            lines += String.format("v %7f %7f %7f", c.x, c.y, c.z)
        }

        // textures
        when (grid.layout.orientation) {
            Orientation.ORIENTATION.POINTY -> {
                lines += "vt 0.8660 0.7500" // point 0
                lines += "vt 0.8660 0.2500" // point 1
                lines += "vt 0.4330 0.0000" // point 2
                lines += "vt 0.0000 0.2500" // point 3
                lines += "vt 0.0000 0.7500" // point 4
                lines += "vt 0.4330 1.0000" // point 5
                lines += "vt 0.4330 0.5000" // point 6
            }
            Orientation.ORIENTATION.FLAT -> {
                // The y coordinate is shifted down by (1-sqrt(3)/2) as the 0,0 isn't top left it seems.
                lines += "vt 1.0000 0.5670" // point 0
                lines += "vt 0.7500 0.1340" // point 1
                lines += "vt 0.2500 0.1340" // point 2
                lines += "vt 0.0000 0.5670" // point 3
                lines += "vt 0.2500 1.0000" // point 4
                lines += "vt 0.7500 1.0000" // point 5
                lines += "vt 0.5000 0.5670" // point 6
            }
        }

        // normals to each face
        for (i in (0..5)) {
            val p1 = coordinates[i]
            val p2 = coordinates[(i + 1) % 6]
            val p3 = coordinates[6]
            val normal = normalFromPoints(p1, p2, p3)
            lines += String.format("vn %5f %5f %5f", normal.x, normal.y, normal.z)
        }

        // faces are +1 compared to point number. make texture and point same.
        // These are: Vertex Index / Texture Index / Normal Index (all 1 based)
        lines += "f 1/1/1 2/2/1 7/7/1"
        lines += "f 2/2/2 3/3/2 7/7/2"
        lines += "f 3/3/3 4/4/3 7/7/3"
        lines += "f 4/4/4 5/5/4 7/7/4"
        lines += "f 5/5/5 6/6/5 7/7/5"
        lines += "f 6/6/6 1/1/6 7/7/6"

        return lines.toList()
    }


    override fun itemAxis(item: GridItem): GridItemAxis {
        val hex = item as Hex
        val cornersOnSurface = coordinates(hex)
        val centre = cornersOnSurface[6]
        val (xP, yP) = when (grid.layout.orientation) {
            Orientation.ORIENTATION.POINTY -> {
                val midpoint01 = cornersOnSurface[0].add(cornersOnSurface[1], Vector3f()).mul(0.5f)
                val midpoint34 = cornersOnSurface[3].add(cornersOnSurface[4], Vector3f()).mul(0.5f)
                val xDir = midpoint01.sub(midpoint34)
                val yDir = cornersOnSurface[2].sub(cornersOnSurface[5])
                Pair(xDir, yDir)
            }
            Orientation.ORIENTATION.FLAT -> {
                val xDir = cornersOnSurface[0].sub(cornersOnSurface[3], Vector3f())
                val midpoint45 = cornersOnSurface[4].add(cornersOnSurface[5], Vector3f()).mul(0.5f)
                val midpoint21 = cornersOnSurface[2].add(cornersOnSurface[1], Vector3f()).mul(0.5f)
                val yDir = midpoint21.sub(midpoint45)
                Pair(xDir, yDir)
            }
        }
        val unitX = xP.normalize()
        val unitY = yP.normalize()
        if (unitX.x.isNaN() || unitY.x.isNaN()) {
            println("ERROR IN NORMALS: hex = $hex, xP = $xP, yP = $yP")
        }
        val unitZ = unitX.cross(unitY, Vector3f())
        return GridItemAxis(
            location = centre,
            axes = Matrix3f(unitX, unitY, unitZ)
        )
    }

    fun coordinates(hex: Hex): List<Vector3f> {
        if (hexCentres.isEmpty()) {
            hexCentres = calculateHexCentres()
        }

        val pairs = when (grid.layout.orientation) {
            Orientation.ORIENTATION.POINTY -> listOf(Pair(5, 0), Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4), Pair(4, 5))
            Orientation.ORIENTATION.FLAT -> listOf(Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4), Pair(4, 5), Pair(5, 0))
        }
        // for a given hex, its corners are calculated relative to the hexes around it for simplicity
        val corners = pairs.map { neighbourPair ->
            averageCentres(hex, hex.neighbour(neighbourPair.first), hex.neighbour(neighbourPair.second))
        }

        return corners + hexCentres[hex]!!
    }

    private fun averageCentres(h0: Hex, h1: Hex, h2: Hex): Vector3f {
        val hc0 = hexCentres.getOrDefault(h0, Vector3f())
        val hc1 = hexCentres.getOrDefault(h1, Vector3f())
        val hc2 = hexCentres.getOrDefault(h2, Vector3f())
        return hc0.add(hc1, Vector3f()).add(hc2).div(3f)
    }

    private fun calculateHexCentres(): Map<Hex, Vector3f> {
        return when (grid.layout.orientation) {
            Orientation.ORIENTATION.POINTY -> calcuatePointyHexCentres()
            Orientation.ORIENTATION.FLAT -> calculateFlatHexCentres()
        }

    }

    private fun calculateFlatHexCentres(): Map<Hex, Vector3f> {
        val centres = mutableMapOf<Hex, Vector3f>()
        val pathCoordinates = pathCreator.createPath(grid.m)
        pathCoordinates.forEachIndexed { segment, pathData ->
            val hexList = mutableListOf<Hex>()
            val hq = segment
            val hs = -segment / 2
            val hr = -hq - hs
            var hexToAdd = grid.hex(hq, hr, hs)
            hexList.add(hexToAdd)
            (0 until grid.height).forEach { _ ->
                hexToAdd = hexToAdd.neighbour(2)
                hexList.add(hexToAdd)
            }
            hexList.forEachIndexed { index, hex ->
                val adjustOddHexes = if (segment % 2 == 0) 0.0 else (0.5 / grid.height)
                val theta = 2.0 * PI * (1.0 / grid.height * index + adjustOddHexes)
                centres[hex] = calculateHexCentre(pathData, theta)
            }

        }
        return centres
    }

    private fun calcuatePointyHexCentres(): Map<Hex, Vector3f> {
        val centres = mutableMapOf<Hex, Vector3f>()
        val pathCoordinates = pathCreator.createPath(grid.width * 2)
        pathCoordinates.forEachIndexed { segment, pathData ->
            val hexList = mutableListOf<Hex>()
            var hexToAdd = when {
                segment % 2 == 0 -> {
                    val hq = segment / 2
                    val hr = 0
                    val hs = -hq
                    grid.hex(hq, hr, hs)
                }
                else -> {
                    val hq = (segment + 1) / 2
                    val hr = -1
                    val hs = -hq - hr
                    grid.hex(hq, hr, hs)
                }
            }
            hexList.add(hexToAdd)
            (0 until (grid.height / 2 - 1)).forEach { _ ->
                hexToAdd = hexToAdd.diagonalNeighbour(1) // add the "north" diagonal
                hexList.add(hexToAdd)
            }
            hexList.forEachIndexed { index, hex ->
                // pencil and paper!
                val adjustOddHexes = if (segment % 2 == 0) 0.0 else (1.0 / grid.height)
                val theta = 2.0 * PI * (2.0 / grid.height * index + adjustOddHexes)
                centres[hex] = calculateHexCentre(pathData, theta)
            }
        }
        return centres
    }

    private fun calculateHexCentre(pathData: PathData, theta: Double): Vector3f {
        val b = pathData.tangent.cross(pathData.normal, Vector3f())
        val xd = b.mul((sweepRadius * cos(theta)).toFloat(), Vector3f())
        val yd = pathData.normal.mul((sweepRadius * sin(theta)).toFloat(), Vector3f())
        return xd.add(pathData.point).add(yd)
    }

    // create a full obj file for our entire grid for the given coordinate provider
    // This is used to export the object for (e.g.) blender.
    fun gridToObj(os: OutputStream) {
        val writer = os.bufferedWriter()
        val allPointsMap = mutableMapOf<Int, Vector3f>()
        var pointIndex = 1
        val faces = mutableListOf<Face>()
        val normals = mutableListOf<Vector3f>()
        var faceIndex = 1
        grid.items().forEach { hex ->
            // accumulate 7 unique points of the hex into a list, with their indexes in the global points map
            val hexPointIndices = mutableListOf<Int>()
            coordinates(hex).forEach { hexPoint ->
                // we don't deal with duplicates here, blender can remove them in seconds. This was adding minutes to the output
                val theIndex = pointIndex++
                allPointsMap[theIndex] = hexPoint

                hexPointIndices += theIndex
            }
            // we now have 7 points and their indexes into the global points map
            // we can create 6 new faces for the hex
            val newFaces = (0..5).map { i ->
                createFace(hexPointIndices, i, (i + 1) % 6, 6, faceIndex)
            }
            faces.addAll(newFaces)
            faceIndex++

            // Create the normals from the points
            val newNormals = (0..5).map { i ->
                val p1 = allPointsMap[hexPointIndices[i]]!!
                val p2 = allPointsMap[hexPointIndices[(i + 1) % 6]]!!
                val p3 = allPointsMap[hexPointIndices[6]]!!
                normalFromPoints(p1, p2, p3)
            }
            normals.addAll(newNormals)
        }

        val points = allPointsMap.toSortedMap().values.toList()

        // Points
        points.forEach { point ->
            writer.write(String.format("v %7f %7f %7f", point.x, point.y, point.z))
            writer.newLine()
        }

        // Textures are not used, but need to have same number as points
        points.forEach { _ ->
            writer.write("vt 0.0 0.0 0.0")
            writer.newLine()
        }

        // Normals
        normals.forEach { normal ->
            writer.write(String.format("vn %5f %5f %5f", normal.x, normal.y, normal.z))
            writer.newLine()
        }

        // Faces
        faces.forEach { face ->
            val line = String.format("f %d/%d/%d %d/%d/%d %d/%d/%d",
                face.p1.textureIndex, face.p1.textureIndex, face.p1.normalIndex,
                face.p2.textureIndex, face.p2.textureIndex, face.p2.normalIndex,
                face.p3.textureIndex, face.p3.textureIndex, face.p3.normalIndex
            )
            writer.write(line)
            writer.newLine()
        }
        writer.close()
    }

    private fun createFace(hexPointIndices: MutableList<Int>, i1: Int, i2: Int, i3: Int, fidx: Int): Face {
        return Face(
            p1 = FacePoint(hexPointIndices[i1], hexPointIndices[i1], fidx),
            p2 = FacePoint(hexPointIndices[i2], hexPointIndices[i2], fidx),
            p3 = FacePoint(hexPointIndices[i3], hexPointIndices[i3], fidx)
        )
    }

    data class Face(
        val p1: FacePoint,
        val p2: FacePoint,
        val p3: FacePoint
    )

    data class FacePoint(
        val vertexIndex: Int,
        val textureIndex: Int,
        val normalIndex: Int
    )

}