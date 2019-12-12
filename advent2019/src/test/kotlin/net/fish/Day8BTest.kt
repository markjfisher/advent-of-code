package net.fish

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day8BTest {
    @Test
    fun `decode image`() {
        val pixelData = listOf(0,2,2,2,1,1,2,2,2,2,1,2,0,0,0,0)
        val image = Image.readPixelData(pixelData, 2, 2)
        assertThat(image.mergedLayer.pixels).containsExactly(0, 1, 1, 0)
    }
}