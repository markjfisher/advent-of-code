package net.fish.y2021

import net.fish.Day
import net.fish.maths.product
import net.fish.maths.productIndexed
import net.fish.maths.withCounts
import net.fish.resourceLines
import net.fish.y2021.Day19.rot24
import org.joml.AxisAngle4f
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.RoundingMode
import org.joml.Vector4f
import org.joml.Vector4i
import kotlin.math.PI
import kotlin.math.abs

object Day19 : Day {
    private val scanners by lazy { parseScanners(resourceLines(2021, 19)) }

    val mRot90x = AxisAngle4f(PI.toFloat() / 2.0f, 1f, 0f, 0f).get(Matrix4f())!!
    val mRot90y = AxisAngle4f(PI.toFloat() / 2.0f, 0f, 1f, 0f).get(Matrix4f())!!
    val mRot90z = AxisAngle4f(PI.toFloat() / 2.0f, 0f, 0f, 1f).get(Matrix4f())!!

    // calculate the 24 rotations
    val rot24 by lazy {
        (0..3).flatMap { xs -> (0..3).flatMap { ys -> (0..3).map { zs -> List(xs) { mRot90x } + List(ys) { mRot90y } + List(zs) { mRot90z } } } }
            .filter { it.isNotEmpty() }
            .map {
                it.reduce { acc, matrix -> Matrix4f(acc).mul(matrix) }
            }
            .toSet()
    }

    fun parseScanners(input: List<String>): List<Scanner> {
        val scanners = mutableListOf<Scanner>()
        var beacons = mutableListOf<Vector4f>()
        var currentIndex = 0

        input.forEach { line ->
            when {
                line.contains("---") -> beacons = mutableListOf()
                line.isNotEmpty() -> {
                    val (x, y, z) = line.split(",").map { it.toFloat() }
                    beacons.add(Vector4f(x, y, z, 1f))
                }
                line.isBlank() -> scanners.add(Scanner(index = currentIndex++, beacons = beacons))
            }
        }
        // last entry
        scanners += Scanner(index = currentIndex, beacons = beacons)
        return scanners
    }

    override fun part1() = doPart1(scanners)
    override fun part2() = doPart2(scanners)

    fun doPart1(scanners: List<Scanner>): Pair<Int, Int> {
        val solution = solve(scanners)
        val nonUnique = solution.flatMap { (scanner, _) ->
            // convert to closest integer points to remove rotation rounding
            scanner.beacons.map { Vector4i(it, RoundingMode.HALF_DOWN) }
        }
        val count = nonUnique.toSet().count()
        val maxDist = solution.map { it.second }.product().maxOfOrNull { (p1, p2) ->
            abs(p1.x - p2.x) + abs(p1.y - p2.y) + abs(p1.z - p2.z)
        } ?: throw Exception("No max distance")

        return Pair(count, maxDist)
    }

    fun doPart2(scanners: List<Scanner>): Int {
        // doing it in part1 as there solution is complete there
        return 0
    }

    fun solve(scanners: List<Scanner>): List<Pair<Scanner, Vector4i>> {
        // Start with first scanner, make everything relative to it
        val translated = mutableListOf(scanners.first() to Vector4i(0, 0, 0, 1))
        val scannersToTranslate = scanners.drop(1).toMutableList()

        while (scannersToTranslate.isNotEmpty()) {
            val startSize = scannersToTranslate.size

            val scannerIterator = scannersToTranslate.listIterator()
            while (scannerIterator.hasNext()) {
                val scanner = scannerIterator.next()

                val matches = translated.asSequence().mapNotNull { (reference, _) ->
                    val orientatedScanner = scanner.orientateTo(reference)
                    orientatedScanner
                }.firstOrNull()
                if (matches != null) {
                    translated.add(matches)
                    scannerIterator.remove()
                }
            }
            if (scannersToTranslate.size == startSize) throw Exception("Failed to translate a scanner")
        }

        return translated
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

class Scanner(
    val index: Int,
    val beacons: List<Vector4f>,
    distances: Map<Int, Pair<Int, Int>>? = null
) {

    // find the distances between every pair of beacons
    val distances = distances ?: beacons.productIndexed { b1i, b1, b2i, b2 -> b1.distanceSquared(b2).toInt() to (b1i to b2i) }
        .groupBy { it.first }
        .mapValues { (_, v) -> v.also { require(it.size == 1) { "Clashing beacon distances from scanner" } }.first().second }

    override fun toString() = "Scanner[$index, beacons: ${beacons.size}, 1st: ${beacons[0]}"

    fun orientateTo(reference: Scanner): Pair<Scanner, Vector4i>? {
        val commonDistances = distances.keys.intersect(reference.distances.keys)
        if (commonDistances.size < 12) return null

        // try the 24 rotations and max out the overlapping beacons
        val matPair = commonDistances.flatMap { dist ->
            val (b1, b2) = distances[dist]!!
            val (rb1, rb2) = reference.distances[dist]!!
            rot24.mapNotNull { rotation ->
                val rotatedB1 = Vector4f(beacons[b1]).mul(rotation)
                val rotatedB2 = Vector4f(beacons[b2]).mul(rotation)
                val d1 = Vector4f(rotatedB1).sub(reference.beacons[rb1])
                val d2 = Vector4f(rotatedB2).sub(reference.beacons[rb2])
                val d3 = Vector4f(rotatedB2).sub(reference.beacons[rb1])
                val d4 = Vector4f(rotatedB1).sub(reference.beacons[rb2])
                if (d1 == d2) {
                    val tr = Matrix4f().translationRotate(-d1.x, -d1.y, -d1.z, rotation.getNormalizedRotation(Quaternionf())).m33(1f)
                    tr
                } else if (d3 == d4) {
                    val tr = Matrix4f().translationRotate(-d3.x, -d3.y, -d3.z, rotation.getNormalizedRotation(Quaternionf())).m33(1f)
                    tr
                } else null
            }
        }.withCounts().maxByOrNull { it.value }

        if (matPair == null || matPair.value < 12) return null

        val translatedBeacons = beacons.map { beacon: Vector4f ->
            beacon.mul(matPair.key, Vector4f())
        }
        val translatedOrigin = Vector4f().mul(matPair.key)
        return Scanner(index, translatedBeacons, distances) to Vector4i(translatedOrigin.x.toInt(), translatedOrigin.y.toInt(), translatedOrigin.z.toInt(), 1)

    }

}
