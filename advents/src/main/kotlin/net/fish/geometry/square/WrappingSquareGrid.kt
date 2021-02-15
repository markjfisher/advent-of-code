package net.fish.geometry.square

import net.fish.geometry.grid.Grid

class WrappingSquareGrid(
    override var width: Int,
    override var height: Int
): SquareConstrainer, Grid {
    init {
        require(width > 2 && height > 2) {
            "Invalid dimensions for square grid. Width and height should be > 2. Given width: $width, height: $height"
        }
    }
    override fun items(): Iterable<Square> {
        return sequence {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    yield(Square(x, y, this@WrappingSquareGrid))
                }
            }
        }.asIterable()
    }

    fun square(x: Int, y: Int): Square = constrain(Square(x, y, this))

    override fun constrain(item: Square): Square {
        var x = item.x % width
        var y = item.y % height
        if (x < 0) x += width
        if (y < 0) y += height
        return Square(x, y, this)
    }

    override fun toString(): String {
        return String.format("WSG[$width, $height]")
    }
}