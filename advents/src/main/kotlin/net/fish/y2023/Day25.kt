package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines
import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph

object Day25 : Day {
    private val data by lazy { resourceLines(2023, 25) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val graph = SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
        data.forEach { line ->
            val (name, others) = line.split(": ")
            graph.addVertex(name)
            others.split(" ").forEach { other ->
                graph.addVertex(other)
                graph.addEdge(name, other)
            }
        }

        val oneSide = StoerWagnerMinimumCut(graph).minCut()
        return (graph.vertexSet().size - oneSide.size) * oneSide.size
    }

    fun doPart2(data: List<String>): Int = data.size

    // clearly 3 lines separate the 2 blobs. printing as SVG was possible to find the nodes
    fun toGraphViz(data: List<String>): String {
        val builder = StringBuilder()
        builder.append("digraph Day25 {\n")
        for (line in data) {
            val parts = line.split(":")
            val node = parts[0].trim()
            val edges = parts[1].split(" ")
            for (edge in edges) {
                if (edge.trim() != "") {
                    builder.append("  $node -> ${edge.trim()};\n")
                }
            }
        }
        builder.append("}\n")
        return builder.toString()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        // println(part2())
    }

}