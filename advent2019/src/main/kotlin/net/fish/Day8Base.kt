package net.fish

open class Day8Base {

}

data class Image(
    val layers: List<Layer>,
    val width: Int,
    val height: Int
) {
    var mergedLayer: Layer

    companion object {
        fun readPixelData(pixelData: List<Int>, width: Int, height: Int): Image {
            return Image(layers = pixelData.chunked(width * height).map { Layer(pixels = it) }, width = width, height = height)
        }
    }

    fun pixelColour(pixelColours: List<Int>): Int {
        val colour = pixelColours.dropWhile { it == 2 }
        return if (colour.isEmpty()) 2 else colour.first()
    }

    init {
        val mergedPixels = (0 until (width*height)).map { pixelNum ->
            val pixels = layers.map { it.pixels[pixelNum] }
            pixelColour(pixels)
        }
        mergedLayer = Layer(pixels = mergedPixels)
    }

    fun printImage() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                val colour = mergedLayer.pixels[y * width + x]
                val char = if(colour == 0) " " else "#"
                print(char)
            }
            println("")
        }
    }
}

data class Layer(
    val pixels: List<Int>
) {
    fun count(i: Int) = pixels.filter { it == i }.count()
}