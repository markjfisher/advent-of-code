package net.fish.geometry.paths

import org.joml.Vector3f

// For any given parametric equation, this represents points on the path, and their Tangents and normals.
data class PathData(
    val point: Vector3f,
    val tangent: Vector3f,
    val normal: Vector3f
)