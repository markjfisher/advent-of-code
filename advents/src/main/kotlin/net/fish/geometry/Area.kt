package net.fish.geometry

data class Area(val xRange: IntRange, val yRange: IntRange): Iterable<Point> {
    constructor(width: Int, height: Int) : this((0 until width), (0 until height))
    constructor(bottomLeft: Point, topRight: Point) : this(bottomLeft.x..topRight.x, bottomLeft.y..topRight.y)

    // similar to boundary
    constructor(points: Iterable<Point>) : this(
        xRange = points.minOf { it.x }..points.maxOf { it.x },
        yRange = points.minOf { it.y }..points.maxOf { it.y },
    )

    val width: Int get() = if (xRange.isEmpty()) 0 else xRange.last - xRange.first + 1
    val height: Int get() = if (xRange.isEmpty()) 0 else yRange.last - yRange.first + 1

    operator fun contains(point: Point) = point.x in xRange && point.y in yRange

    override fun iterator(): Iterator<Point> = iterator {
        for (x in xRange) {
            for (y in yRange) {
                yield(Point(x, y))
            }
        }
    }
}

infix fun Area.overlaps(other: Area): Boolean {
    val horizontalOverlap = maxOf(xRange.first, other.xRange.first) <= minOf(xRange.last, other.xRange.last)
    val verticalOverlap = maxOf(yRange.first, other.yRange.first) <= minOf(yRange.last, other.yRange.last)
    return horizontalOverlap && verticalOverlap
}