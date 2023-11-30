package net.fish.y2018

import net.fish.Day
import net.fish.resourceString

object Day08 : Day {
    private val data by lazy { toLicenceTree(resourceString(2018, 8).split(" ").map { it.toInt() }) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(tree: LNode): Int = tree.sumAllMetaData()
    fun doPart2(tree: LNode): Int {
        return tree.metaDataPart2()
    }

    // Responsible for handing data from the list of ints out
    data class TreeData(val data: List<Int>) {
        private var currentIndex = 0
        fun getHeader(): Pair<Int, Int> {
            val header = Pair(data[currentIndex], data[currentIndex+1])
            currentIndex += 2
            return header
        }

        fun getMetaData(count: Int): List<Int> {
            if (count == 0) return emptyList()
            val subList = data.subList(currentIndex, currentIndex + count)
            currentIndex += count
            return subList
        }
    }

    data class LNode(val parent: LNode?, val childNodes: MutableList<LNode>, val metaData: MutableList<Int>) {
        // stops the recursion issue in debugging
        override fun toString(): String {
            return "LNode[childNodes: ${childNodes.size}, metaData: $metaData]"
        }

        fun sumAllMetaData(): Int {
            return metaData.sum() + childNodes.sumOf { it.sumAllMetaData() }
        }

        fun metaDataPart2(): Int {
            if (childNodes.isEmpty()) {
                return metaData.sum()
            }
            // look at each metadata entry as indexes into child nodes
            return metaData.sumOf { md ->
                // CAREFUL! the indexes are 1 based. If there's no child referenced by index, return 0, otherwise recurse into the child's value
                if (md <= childNodes.size) childNodes[md - 1].metaDataPart2() else 0
            }
        }
    }

    fun toLicenceTree(data: List<Int>): LNode {
        val td = TreeData(data)
        fun buildTree(currentNode: LNode, numChildren: Int, numMeta: Int) {
            repeat((0 until numChildren).count()) {
                val newNode = LNode(currentNode, mutableListOf(), mutableListOf())
                val (nC, nM) = td.getHeader()
                buildTree(newNode, nC, nM)
                currentNode.childNodes.add(newNode)
            }
            currentNode.metaData.addAll(td.getMetaData(numMeta))
        }

        // kick off the process with the root node, which has no parent.
        val root = LNode(null, mutableListOf(), mutableListOf())
        val (numChildren, numMeta) = td.getHeader()
        buildTree(root, numChildren, numMeta)

        return root
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}