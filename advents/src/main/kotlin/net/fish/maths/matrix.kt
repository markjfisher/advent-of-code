package net.fish.maths

import org.joml.Vector3f

inline fun <reified R> rotateMatrix(mat: Array<Array<R>>): Array<Array<R>> {
    // only allow nxn matrix
    check(mat.size == mat[0].size)

    var matCopy = copyMatrix(mat)

    val size = mat.size

//    println("BEFORE ROTATION -------------------------------")
//    for (i in 0 until size) {
//        for (j in 0 until size) print(matCopy[i][j].toString() + " ")
//        print("\n")
//    }
//    println("-------------------------------")

    for (layer in (0 until (size / 2))) {
        val last = size - layer - 1
        for (i in (layer until last)) {
            val offset = i - layer
            val lastMinusOffset = last - offset
            val top = matCopy[layer][i]
            matCopy[layer][i] = matCopy[lastMinusOffset][layer]
            matCopy[lastMinusOffset][layer] = matCopy[last][lastMinusOffset]
            matCopy[last][lastMinusOffset] = matCopy[i][last]
            matCopy[i][last] = top
        }
    }

    // Print rotated matrix
//    println("AFTER ROTATION -------------------------------")
    // val size = mat.size
//    for (i in 0 until size) {
//        for (j in 0 until size) print(matCopy[i][j].toString() + " ")
//        print("\n")
//    }
//    println("-------------------------------")
    return matCopy
}

inline fun <reified R> copyMatrix(matrix: Array<Array<R>>): Array<Array<R>> {
    var copy = arrayOf<Array<R>>()
    for (j in matrix.indices) {
        var row = arrayOf<R>()
        for (i in matrix.indices) {
            row += matrix[j][i]
        }
        copy += row
    }
    return copy
}

inline fun <reified R> flipMatrixByVertical(mat: Array<Array<R>>): Array<Array<R>> {
    // only allow nxn matrix
    check(mat.size == mat[0].size)

    val matCopy = copyMatrix(mat)
    for (r in matCopy.indices) {
        matCopy[r].reverse()
    }

    return matCopy
}

inline fun <reified T> Pair<Int, Int>.createArray(initialValue: T) = Array(this.first) { Array(this.second) { initialValue } }

fun normalFromPoints(p1: Vector3f, p2: Vector3f, p3: Vector3f): Vector3f {
    // Blender does normal = (p1-p2)x(p2-p3)
    val cross = p1.sub(p2, Vector3f()).cross(p2.sub(p3, Vector3f()))
    if (cross.lengthSquared() > 0.001f) cross.normalize() else {
        println("cross is zero for $p1, $p2, $p3")
    }
    return cross
}
