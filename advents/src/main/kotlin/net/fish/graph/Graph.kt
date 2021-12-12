package net.fish.graph

import java.util.ArrayDeque
import java.util.Deque

class Graph<T> {
    private val adjacencyMap: HashMap<T, HashSet<T>> = HashMap()

    fun addEdge(sourceVertex: T, destinationVertex: T) {
        // Add edge to source vertex / node.
        adjacencyMap
            .computeIfAbsent(sourceVertex) { HashSet() }
            .add(destinationVertex)
        // Add edge to destination vertex / node.
        adjacencyMap
            .computeIfAbsent(destinationVertex) { HashSet() }
            .add(sourceVertex)
    }

    override fun toString(): String = StringBuffer().apply {
        for (key in adjacencyMap.keys) {
            append("$key -> ")
            append(adjacencyMap[key]?.joinToString(", ", "[", "]\n"))
        }
    }.toString()

    fun depthFirstTraversal(startNode: T): String {
        // Mark all the vertices / nodes as not visited.
        val visitedMap = mutableMapOf<T, Boolean>().apply {
            adjacencyMap.keys.forEach { node -> put(node, false) }
        }

        // Create a stack for DFS. Both ArrayDeque and LinkedList implement Deque.
        val stack: Deque<T> = ArrayDeque()

        // Initial step -> add the startNode to the stack.
        stack.push(startNode)

        // Store the sequence in which nodes are visited, for return value.
        val traversalList = mutableListOf<T>()

        // Traverse the graph.
        while (stack.isNotEmpty()) {
            // Pop the node off the top of the stack.
            val currentNode = stack.pop()

            if (!visitedMap[currentNode]!!) {

                // Store this for the result.
                traversalList.add(currentNode)

                // Mark the current node visited and add to the traversal list.
                visitedMap[currentNode] = true

                // Add nodes in the adjacency map.
                adjacencyMap[currentNode]?.forEach { node ->
                    stack.push(node)
                }

            }

        }

        return traversalList.joinToString()
    }
}

