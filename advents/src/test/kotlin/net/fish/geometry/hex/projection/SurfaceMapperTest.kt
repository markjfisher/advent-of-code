package net.fish.geometry.hex.projection

import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import org.junit.jupiter.api.Test
import java.io.File

internal class SurfaceMapperTest {

    private val decoratedSurface = DecoratedKnotSurface(900, 16, POINTY, DecoratedKnotType.Type10b, 0.25f, 5.0f)
    private val trefoilSurface = TrefoilSurface(600, 26, POINTY, 0.6f, 3.0f)
    private val torusKnotSurface = TorusKnotSurface(900, 16, POINTY, 3, 7, 1.0f, 0.2f, 0.2f, 5.0f)

    // @Disabled("just for creating files")
    @Test
    fun `can output projection as obj file`() {
        val surface = decoratedSurface
        surface.createMapper()
        val mapper = SurfaceMapper(surface)
        mapper.gridToObj(File("/home/markf/Documents/blender/surface.obj").outputStream())
    }

    @Test
    fun `can export obj files`() {

    }
}