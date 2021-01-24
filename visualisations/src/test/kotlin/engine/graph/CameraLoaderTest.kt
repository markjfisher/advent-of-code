package engine.graph

import org.assertj.core.api.Assertions.assertThat
import org.joml.Quaternionf
import org.joml.Vector3f
import org.junit.jupiter.api.Test

class CameraLoaderTest {
    @Test
    fun `rotations from file are mapped to correct quaternions`() {
        val q = Quaternionf(-2.755E-4f, 0f, 0f, 1f)
        println("q: ${q.getEulerAnglesXYZ(Vector3f())}")

        val cameraData = CameraLoader.loadCamera("/camera-location-data.txt")
        assertThat(cameraData[0].location).isEqualTo(Vector3f(0.00000f, -8.05700f, 0f))
        assertThat(cameraData[0].rotation.dot(Quaternionf(0f,  0f,  0f,  1f)) - 1f).isLessThan(0.0001f)
    }
}