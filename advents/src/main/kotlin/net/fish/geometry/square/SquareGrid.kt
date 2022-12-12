package net.fish.geometry.square

class SquareGrid(
    override var width: Int,
    override var height: Int
): SquareConstrainer, SquareGridInterface {
    init {
        require(width > 2 && height > 2) {
            "Invalid dimensions for square grid. Width and height should be > 2. Given width: $width, height: $height"
        }
    }

    override fun items(): Iterable<Square> {
        return sequence {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    yield(Square(x, y, this@SquareGrid))
                }
            }
        }.asIterable()
    }

    override fun square(x: Int, y: Int): Square? = constrain(Square(x, y, this))

    override fun constrain(item: Square?): Square? {
        if (item == null) throw Exception("Cannot constrain null square")
        if (item.x < 0 || item.y < 0 || item.x >= this.width || item.y >= this.height) return null
        return item
    }

    override fun toString(): String {
        return String.format("NWSG[$width, $height]")
    }
}