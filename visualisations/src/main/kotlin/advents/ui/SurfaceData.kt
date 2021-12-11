package advents.ui

import net.fish.geometry.grid.GridType

data class SurfaceData(
    val width: Int,
    val height: Int,
    val gridType: GridType,
    val size: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SurfaceData

        if (width != other.width) return false
        if (height != other.height) return false
        if (gridType != other.gridType) return false
        if (!size.contentEquals(other.size)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + gridType.hashCode()
        result = 31 * result + size.contentHashCode()
        return result
    }
}