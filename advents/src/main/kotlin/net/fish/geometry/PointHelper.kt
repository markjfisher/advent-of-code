package net.fish.geometry

private fun isPointInLoop(loop: List<Point>, point: Point): Boolean {
    var intersections = 0
    val bounds = loop.bounds()
    val outsidePoint = Point(bounds.first.x - 1, bounds.first.y - 1)

    for (i in loop.indices) {
        val start = loop[i]
        val end = if (i + 1 == loop.size) loop[0] else loop[i + 1]

        if (doLinesIntersect(start, end, point, outsidePoint)) {
            intersections++
        }
    }
    val isInLoop = intersections %2 == 1
    // println("checking $point, r: $isInLoop, intersections = $intersections")

    return isInLoop
}

private fun doLinesIntersect(start: Point, end: Point, point: Point, outsidePoint: Point): Boolean {
    if (start.y <= point.y) {
        if (end.y > point.y && isPointOnRight(start, end, point, outsidePoint)) {
            return true
        }
    } else if (end.y <= point.y) {
        if (isPointOnRight(start, end, point, outsidePoint)) {
            return true
        }
    }

    return false
}

fun isPointOnRight(start: Point, end: Point, point: Point, outsidePoint: Point): Boolean {
    val crossProduct1 = ((end.y - start.y) * (point.x - start.x)) - ((end.x - start.x) * (point.y - start.y))
    val crossProduct2 = ((end.y - start.y) * (outsidePoint.x - start.x)) - ((end.x - start.x) * (outsidePoint.y - start.y))
    return crossProduct1 * crossProduct2 < 0
}