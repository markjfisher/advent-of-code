package net.fish.geometry.hex.projection

import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.DecoratedTorusKnotPathCreator
import net.fish.geometry.paths.TorusKnotPathCreator
import net.fish.geometry.paths.TrefoilPathCreator
import org.junit.jupiter.api.Test
import java.io.File

internal class SurfaceMapperTest {

    private val decoratedSurface = PathMappedWrappingHexGrid(
        hexGrid = WrappingHexGrid(900, 16, Layout(POINTY)),
        // Valid patterns: 4b, 7a, 7b, 10b, 11c
        pathCreator = DecoratedTorusKnotPathCreator(pattern = "11c", scale = 5.0),
        r = 0.25
    )

    private val trefoilSurface = PathMappedWrappingHexGrid(
        hexGrid = WrappingHexGrid(600, 26, Layout(POINTY)),
        pathCreator = TrefoilPathCreator(scale = 3.0),
        r = 0.6
    )

    private val torusKnotSurface = PathMappedWrappingHexGrid(
        hexGrid = WrappingHexGrid(900, 16, Layout(POINTY)),
        pathCreator = TorusKnotPathCreator(p = 3, q = 7, scale = 5.0),
        r = 0.25
    )

    // @Disabled("just for creating files")
    @Test
    fun `can output projection as obj file`() {
        val surface = decoratedSurface
        surface.gridToObj(File("/home/markf/Documents/blender/surface.obj").outputStream())
    }

    @Test
    fun `can export obj files`() {

    }
}