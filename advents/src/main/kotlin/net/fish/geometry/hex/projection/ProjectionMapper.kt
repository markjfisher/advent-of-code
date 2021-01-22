package net.fish.geometry.hex.projection

import net.fish.geometry.hex.Hex
import net.fish.geometry.hex.HexAxis
import net.fish.geometry.hex.Orientation
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.maths.normalFromPoints
import org.joml.Matrix3f
import org.joml.Vector3f
import java.io.OutputStream

abstract class ProjectionMapper(open val hexGrid: WrappingHexGrid) {
    // return the coordinates of the hex corners on the torus described by the layout
    abstract fun coordinates(hex: Hex): List<Vector3f>

    fun hexAxes(): Map<Hex, HexAxis> {
        return hexGrid.hexes().map { hex ->
            val cornersOnTorus = coordinates(hex)
            val centre = cornersOnTorus[6]
            val (xP, yP) = when(hexGrid.layout.orientation) {
                Orientation.ORIENTATION.POINTY -> {
                    val midpoint01 = cornersOnTorus[0].add(cornersOnTorus[1], Vector3f()).mul(0.5f)
                    val midpoint34 = cornersOnTorus[3].add(cornersOnTorus[4], Vector3f()).mul(0.5f)
                    val xDir = midpoint01.sub(midpoint34)
                    val yDir = cornersOnTorus[2].sub(cornersOnTorus[5])
                    Pair(xDir, yDir)
                }
                Orientation.ORIENTATION.FLAT -> {
                    val xDir = cornersOnTorus[0].sub(cornersOnTorus[3], Vector3f())
                    val midpoint45 = cornersOnTorus[4].add(cornersOnTorus[5], Vector3f()).mul(0.5f)
                    val midpoint21 = cornersOnTorus[2].add(cornersOnTorus[1], Vector3f()).mul(0.5f)
                    val yDir = midpoint21.sub(midpoint45)
                    Pair(xDir, yDir)
                }
            }
            val unitX = xP.normalize()
            val unitY = yP.normalize()
            val unitZ = unitX.cross(unitY, Vector3f())
            hex to HexAxis(
                location = centre,
                axes = Matrix3f(unitX, unitY, unitZ)
            )
        }.toMap()
    }

    // Create OBJ compatible output for the given hex using 6 faces, with texture coordinates
    // mapped onto a torus of the given dimensions
    // The face triangles all share the centre as a common point, and fill the hexagon in a natural manner.
    fun hexToObj(hex: Hex): List<String> {
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
        when (hexGrid.layout.orientation) {
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

    // create a full obj file for our entire grid for the given coordinate provider
    fun gridToObj(os: OutputStream) {
        val writer = os.bufferedWriter()
        val allPointsMap = mutableMapOf<Int, Vector3f>()
        var pointIndex = 1
        val faces = mutableListOf<Face>()
        val normals = mutableListOf<Vector3f>()
        var faceIndex = 1
        hexGrid.hexes().forEach { hex ->
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