package net.fish.geometry.projection

enum class DecoratedKnotType {
    // Valid patterns: 4b, 7a, 7b, 10b, 11c
    Type4b, Type7a, Type7b, Type10b, Type11c;
    companion object {
        fun from(p: String): DecoratedKnotType? {
            return values().firstOrNull { it.name.equals(p, ignoreCase = true) }
        }
    }
}