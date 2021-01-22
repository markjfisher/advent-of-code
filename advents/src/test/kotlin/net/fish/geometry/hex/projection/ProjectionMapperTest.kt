package net.fish.geometry.hex.projection

import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation
import net.fish.geometry.hex.WrappingHexGrid
import org.junit.jupiter.api.Test
import java.io.File

internal class ProjectionMapperTest {

    @Test
    fun `can output projection as obj file`() {
        val gridLayout = Layout(Orientation.ORIENTATION.POINTY)
        val hexGrid = WrappingHexGrid(800, 16, gridLayout)
        val knot = TorusKnotMappedWrappingHexGrid(hexGrid = hexGrid, p = 3, q = 7, r = 0.25, scale = 5.0)

        knot.gridToObj(File("/tmp/knot.obj").outputStream())
    }
}