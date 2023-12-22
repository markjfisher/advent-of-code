package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines
import net.fish.resourcePath
import org.joml.Vector3i

object Day22 : Day {
    private val blockExtractor by lazy { Regex("""(\d+),(\d+),(\d+)~(\d+),(\d+),(\d+)""") }
    private val data by lazy { resourceLines(2023, 22) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val sandGrid = generateSandGrid(data)
        sandGrid.drop()
        return sandGrid.removableBlocksCount()
    }
    fun doPart2(data: List<String>): Int = data.size

    data class SandBlock(val id: Int, val locations: List<Vector3i>) {
        fun lowestZ(): Int = locations.minOf { it.z }
        fun lowerBlock() = locations.forEach { it.z -= 1 }
    }

    data class SandGrid(var blocks: List<SandBlock>) {
        // move blocks down a unit if possible, if it was able to move, return true
        fun fall(): Boolean {
            // order blocks, then for each block, check that all the points below its lowest z point(s) have free space below them
            var blockFell = false
            blocks = blocks.sortedBy { it.lowestZ() }
            for (block in blocks) {
                if (block.lowestZ() > 1) {
                    val blockBelowExists = blocks.any { other ->
                        other != block && other.locations.any { otherLocation ->
                            block.locations.any { blockLocation ->
                                blockLocation.z - 1 == otherLocation.z &&
                                        blockLocation.x == otherLocation.x &&
                                        blockLocation.y == otherLocation.y
                            }
                        }
                    }

                    if (!blockBelowExists) {
                        block.lowerBlock()
                        blockFell = true
                    }
                }
            }
            return blockFell
        }

        fun drop() {
            var iterations = 0
            do {
                val didFall = fall()
                if (didFall) {
                    iterations++
                    println("falling .. $iterations")
                }
            } while (didFall)
            // println(iterations)
        }

        fun removableBlocksCount(): Int {
            var count = 0
            val originalBlocks = blocks.toList()
            for(block in originalBlocks) {
                println("checking ${block.id} in ${blocks.size} for removable")
                blocks = originalBlocks.filter { it != block }
                if(!fall()) {
                    count++
                    println("count -> $count")
                }
                blocks = originalBlocks
            }
            return count
        }
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
                val locations = when {
                    ix != ex -> (ix .. ex).map { Vector3i(it, iy, iz) }
                    iy != ey -> (iy .. ey).map { Vector3i(ix, it, iz) }
                    iz != ez -> (iz .. ez).map { Vector3i(ix, iy, it) }
                    else -> listOf(Vector3i(ix, iy, iz))
                }
                SandBlock(id++, locations)
            }
        }
        return SandGrid(blocks)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // 515 too high
        println(part1())
        println(part2())
    }

}