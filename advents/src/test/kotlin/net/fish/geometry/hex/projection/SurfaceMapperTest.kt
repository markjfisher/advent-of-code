package net.fish.geometry.hex.projection

import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.TorusKnotPathCreator
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

internal class SurfaceMapperTest {
    @Disabled("just for creating files")
    @Test
    fun `can output projection as obj file`() {
        val gridLayout = Layout(Orientation.ORIENTATION.POINTY)
        val hexGrid = WrappingHexGrid(800, 16, gridLayout)
        val knot = PathMappedWrappingHexGrid(hexGrid = hexGrid, TorusKnotPathCreator(p = 3, q = 7, scale = 5.0, segments = hexGrid.m * 2), r = 0.25)

        knot.gridToObj(File("/tmp/knot.obj").outputStream())
    }

    @Test
    fun `can export obj files`() {

    }
}