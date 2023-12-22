package net.fish.geometry

data class Point3D(val x: Int, val y: Int, val z: Int): Comparable<Point3D> {
    override fun compareTo(other: Point3D): Int {
        return when {
            this.z != other.z -> this.z - other.z
            this.y != other.y -> this.y - other.y
            else -> this.x - other.x
        }
    }

    operator fun plus(other: Point3D) = Point3D(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Point3D) = Point3D(x - other.x, y - other.y, z - other.z)
}