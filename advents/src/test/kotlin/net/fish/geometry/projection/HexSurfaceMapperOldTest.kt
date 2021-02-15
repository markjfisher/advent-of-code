package net.fish.geometry.projection

import net.fish.geometry.hex.HexSurfaceMapperOld
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import org.junit.jupiter.api.Test
import java.io.File

internal class HexSurfaceMapperOldTest {

    private val decoratedSurface = DecoratedKnotSurfaceOld(900, 16, POINTY, DecoratedKnotType.Type10b, 0.25f, 5.0f)
    private val trefoilSurface = TrefoilSurface(600, 26, POINTY, 0.6f, 3.0f)
    private val torusKnotSurface = TorusKnotSurface(900, 16, POINTY, 3, 7, 1.0f, 0.2f, 0.2f, 5.0f)
    private val torusKnotSurface2 = TorusKnotSurface(1300, 12, POINTY, 11, 17, 1.0f, 0.723f, 0.2f, 5.0f)

    // @Disabled("just for creating files")
    @Test
    fun `can output projection as obj file`() {
        val surface = trefoilSurface
        surface.createMapper()
        val mapper = HexSurfaceMapperOld(surface)
        mapper.gridToObj(File("/home/markf/Documents/blender/surface.obj").outputStream())
    }

    @Test
    fun `can export obj files`() {

    }
}