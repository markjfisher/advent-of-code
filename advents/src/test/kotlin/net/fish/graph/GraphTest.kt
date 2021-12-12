package net.fish.graph

import org.junit.jupiter.api.Test

class GraphTest {
    @Test
    fun `can add nodes to graph`() {
        val g = Graph<String>()
        g.addEdge("A", "B")
        g.addEdge("B", "C")
        g.addEdge("B", "D")
        g.addEdge("C", "END")
        g.addEdge("D", "END")

        println(g)

        val dfs = g.depthFirstTraversal("A")
        println(dfs)
    }
}