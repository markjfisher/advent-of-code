package net.fish.geometry.grid

enum class GridType {
    HEX, SQUARE;
    companion object {
        fun from(name: String): GridType? {
            return values().firstOrNull { it.name.equals(name, ignoreCase = true) }
        }
    }
}