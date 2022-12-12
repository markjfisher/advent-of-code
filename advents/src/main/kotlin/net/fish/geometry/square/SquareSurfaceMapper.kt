package net.fish.geometry.square

import net.fish.geometry.grid.GridItem
import net.fish.geometry.grid.GridItemAxis
import net.fish.geometry.grid.GridType
import net.fish.geometry.projection.SurfaceMapper
import net.fish.maths.normalFromPoints
import org.joml.Matrix3f
import org.joml.Vector3f

abstract class SquareSurfaceMapper(
    var grid: SquareGridInterface
): SurfaceMapper {
    // abstract fun calculateSquareCentres(): Map<Square, Vector3f>
    abstract fun coordinates(item: Square): List<Vector3f>

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
        val mp30 = corners[3].add(corners[0], Vector3f()).mul(0.5f)
        val xDir = mp01.sub(mp23).normalize()
        val yDir = mp30.sub(mp12).normalize()
        if (xDir.x.isNaN() || yDir.x.isNaN()) {
            println("ERROR IN NORMALS: square: $square, xDir: $xDir, yDir: $yDir")
        }
        val zDir = xDir.cross(yDir, Vector3f())
        return GridItemAxis(
            location = corners[4],
            axes = Matrix3f(xDir, yDir, zDir)
        )
    }

}