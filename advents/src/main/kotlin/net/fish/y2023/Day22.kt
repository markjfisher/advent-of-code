package net.fish.y2023

import net.fish.Day
import net.fish.geometry.Area
import net.fish.geometry.Point3D
import net.fish.geometry.overlaps
import net.fish.resourceLines

object Day22 : Day {
    private val blockExtractor by lazy { Regex("""(\d+),(\d+),(\d+)~(\d+),(\d+),(\d+)""") }
    private val data by lazy { resourceLines(2023, 22) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val sandGrid = generateSandGrid(data)
        return sandGrid.redundantBricks.count()
    }
    fun doPart2(data: List<String>): Int {
        val sandGrid = generateSandGrid(data)
        return 0
    }

    data class SandBlock(val id: Int, val start: Point3D, val end: Point3D) {
        init {
            require(start.z <= end.z) { "Start and end given out of order." }
            require(start.z >= 1) { "Sand block collides with ground." }
            val isHorizontal = start.z == end.z && (start.x == end.x || start.y == end.y)
            val isVertical = start.x == end.x && start.y == end.y
            require(isHorizontal || isVertical) { "The sand block must be a straight line." }
        }

        val fallingArea: Area = Area(
            xRange = minOf(start.x, end.x)..maxOf(start.x, end.x),
            yRange = minOf(start.y, end.y)..maxOf(start.y, end.y),
        )

        fun fallTo(restHeight: Int): SandBlock = SandBlock(
            id = id,
            start = start.copy(z = restHeight),
            end = end.copy(z = end.z - start.z + restHeight),
        )

        fun fallingAreaOverlaps(other: SandBlock): Boolean = fallingArea overlaps other.fallingArea
    }

    data class SandGrid(var blocks: List<SandBlock>) {
        val settledBlocks = blocks
            .toMutableList()
            .apply {
                sortBy { it.start.z }
                forEachIndexed { index, block ->
                    this[index] = slice((0 until index))
                        .filter { block.fallingAreaOverlaps(it) }
                        .maxOfOrNull { it.end.z + 1 }
                        .let { restHeight -> block.fallTo(restHeight ?: 1)}
                }
            }
            .sortedBy { it.start.z }

        val supportedBy: Map<SandBlock, Set<SandBlock>>

        private val SandBlock.supportedBricks: Set<SandBlock> get() = supportedBy.getValue(this)

        val supporting: Map<SandBlock, Set<SandBlock>>

        private val SandBlock.standingOn: Set<SandBlock> get() = supporting.getValue(this)

        init {
            val supportedBy: Map<SandBlock, MutableSet<SandBlock>> = settledBlocks.associateWith { mutableSetOf() }
            val supporting: Map<SandBlock, MutableSet<SandBlock>> = settledBlocks.associateWith { mutableSetOf() }

            settledBlocks.forEachIndexed { index, above ->
                settledBlocks.slice((0 until index)).forEach { below ->
                    if (below.fallingAreaOverlaps(above) && above.start.z == below.end.z + 1) {
                        supportedBy.getValue(below).add(above)
                        supporting.getValue(above).add(below)
                    }
                }
            }

            this.supportedBy = supportedBy
            this.supporting = supporting
        }

        val redundantBricks: Set<SandBlock> =
            settledBlocks
                .filter { it.supportedBricks.all { supported -> supported.standingOn.count() >= 2 } }
                .toSet()


    }

    fun generateSandGrid(data: List<String>): SandGrid {
        var id = 0
        val blocks = data.map { line ->
            blockExtractor.find(line)?.destructured!!.let { (ixs, iys, izs, exs, eys, ezs) ->
                val ix = ixs.toInt()
                val iy = iys.toInt()
                val iz = izs.toInt()
                val ex = exs.toInt()
                val ey = eys.toInt()
                val ez = ezs.toInt()

                SandBlock(id++, Point3D(ix, iy, iz), Point3D(ex, ey, ez))
            }
        }
        return SandGrid(blocks)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}