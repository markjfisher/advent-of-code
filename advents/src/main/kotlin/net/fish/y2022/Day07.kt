package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines

object Day07 : Day {
    private val root by lazy { readDisk(resourceLines(2022, 7)) }

    override fun part1() = doPart1(root)
    override fun part2() = doPart2(root)

    fun doPart1(root: AOCDir): Long {
        return dfsDirs(root).filter { it.size() <= 100000L }.sumOf { it.size() }
    }

    fun doPart2(root: AOCDir): Long {
        val requiredToFree = root.size() - 40_000_000L // 30_000_000L - (70_000_000L - root.size())
        return dfsDirs(root).sortedBy { it.size() }.first { it.size() > requiredToFree }.size()
    }

    data class AOCFile(val name: String, val size: Long, val parent: AOCDir) {
        override fun toString(): String = "File[$name]"  // To stop recursive problems in debugger
    }

    data class AOCDir(val name: String, val dirs: MutableList<AOCDir>, val files: MutableList<AOCFile>, val parent: AOCDir? = null) {
        fun size(): Long = files.sumOf { it.size } + dirs.sumOf { it.size() }

        override fun toString(): String = "Dir[$name]" // To stop recursive problems in debugger
    }

    private fun dfsDirs(root: AOCDir): List<AOCDir> {
        fun doDFS(dir: AOCDir, builtList: MutableList<AOCDir>) {
            builtList.add(dir)
            dir.dirs.forEach { doDFS(it, builtList) }
        }
        val builtList = mutableListOf<AOCDir>()
        doDFS(root, builtList)
        return builtList
    }

    fun readDisk(data: List<String>): AOCDir {
        val root = AOCDir("/", mutableListOf(), mutableListOf(), null)
        var currentDir = root
        val fileRE = Regex("^\\d+ ")
        val dirRE = Regex("^dir ")
        val cdRE = Regex("^\\$ cd ")
        data.forEachIndexed { i, line ->
            when {
                line == "\$ cd /" -> currentDir = root
                line == "\$ cd .." -> currentDir = currentDir.parent ?: throw Exception("Cannot traverse above root at line $i")
                fileRE.containsMatchIn(line) -> addFile(currentDir, line)
                dirRE.containsMatchIn(line) -> addDir(currentDir, line)
                cdRE.containsMatchIn(line) -> currentDir = cdDir(currentDir, line)
            }
        }
        return root
    }

    private fun addFile(dir: AOCDir, line: String) {
        val (ss, name) = line.split(" ", limit = 2)
        dir.files += AOCFile(name = name, size = ss.toLong(), parent = dir)
    }

    private fun addDir(dir: AOCDir, line: String) {
        val dirName = line.split(" ", limit = 2)[1]
        dir.dirs += AOCDir(dirName, mutableListOf(), mutableListOf(), dir)
    }

    private fun cdDir(dir: AOCDir, line: String): AOCDir {
        val dirName = line.split(" ", limit = 3)[2]
        return dir.dirs.find { d -> d.name == dirName } ?: throw Exception("$dirName could not be found in $dir")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}