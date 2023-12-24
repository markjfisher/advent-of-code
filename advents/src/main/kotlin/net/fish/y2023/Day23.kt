package net.fish.y2023

import java.util.Stack
import net.fish.Day
import net.fish.geometry.Direction.EAST
import net.fish.geometry.Direction.NORTH
import net.fish.geometry.Direction.SOUTH
import net.fish.geometry.Direction.WEST
import net.fish.geometry.Point
import net.fish.geometry.get
import net.fish.geometry.gridString
import net.fish.geometry.containsPoint
import net.fish.network.longestPathDfs
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day23 : Day {
    private val data by lazy { resourceLines(2023, 23) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val iceGrid = toIceGrid(data)
        val paths = iceGrid.paths()
        return paths.maxOf { it.size } - 1
    }

    fun doPart2(data: List<String>): Int {
        // takes forever
//        val iceGrid = toIceGrid(data)
//        val graph = iceGrid.toGraph()
//        val paths = graph.paths(iceGrid.start, iceGrid.end)
//        val x = paths.maxOf { it.sumOf { e -> e.length } }
//        return x

        val iceGrid = toIceGrid(data)
        val graph = iceGrid.buildGraph(data, iceGrid.start, iceGrid.end, false)
        return longestPathDfs(graph, iceGrid.start, iceGrid.end)
    }

    data class IceGrid(val locations: Map<Point, Char>, val width: Int, val height: Int) {
        val start = Point(1,0)
        val end = Point(width - 2, height - 1)
        override fun toString() = locations.gridString()

        fun paths(useForced: Boolean = true): List<List<Point>> {
            val visited = mutableSetOf<Point>()
            val paths = mutableListOf<List<Point>>()
            val currentPath = mutableListOf<Point>()

            // this doesn't scale for P2 real data, only test data
            fun dfs(point: Point) {
                if (point == end) {
                    paths.add(ArrayList(currentPath))
                    return
                }

                val neighbours = point.neighbours()
                val forcedDirection = if (useForced) when (locations[point]) {
                    '>' -> point + EAST
                    'v' -> point + SOUTH
                    '<' -> point + WEST
                    '^' -> point + NORTH
                    else -> null
                } else null

                val pointsToVisit = forcedDirection?.let { listOf(it) } ?: neighbours
                for (nextPoint in pointsToVisit) {
                    if (nextPoint in locations.keys && locations[nextPoint] != '#' && nextPoint !in visited) {
                        visited.add(nextPoint)
                        currentPath.add(nextPoint)
                        dfs(nextPoint)
                        visited.remove(nextPoint)
                        currentPath.removeAt(currentPath.size - 1)
                    }
                }
            }

            visited.add(start)
            currentPath.add(start)
            dfs(start)

            return paths
        }

        // TODO: why is this taking so long on large inputs? Need to fix it.
        fun toGraph(): IceGraph {
            val nodes = mutableMapOf<Point, Node>()
            val edges = mutableListOf<Edge>()

            data class WalkState(val point: Point, val fromNode: Node?, val fromPoint: Point?, val visited: MutableSet<Point>, val length: Int)

            val stack = Stack<WalkState>()
            stack.push(WalkState(Point(1, 0), null, null, mutableSetOf(), 0))

            while (stack.isNotEmpty()) {
                val (point, fromNode, fromPoint, visited, length) = stack.pop()

                if (point in visited && point != start && point != end) {
                    // This point has been visited before, skip it to avoid circular paths
                    continue
                }
                visited.add(point)

                val neighbours = point.neighbours().filter { it in locations.keys && locations[it] != '#' }
                if (neighbours.size != 2 || point == start || point == end) {
                    val node = nodes.getOrPut(point) { Node(point) }
                    if (fromNode != null) {
                        val newEdge = Edge(fromNode, node, length)
                        val oldEdge = edges.find { it == newEdge }
                        if (oldEdge == null || newEdge > oldEdge) {
                            // remove the old edge if it exists
                            oldEdge?.let {
                                edges.remove(it)
                                fromNode.edges.remove(it)
                                node.edges.remove(it)
                            }
                            // add the new edge
                            edges.add(newEdge)
                            fromNode.edges.add(newEdge)
                            node.edges.add(newEdge)
                        }
                    }
                    for (nextPoint in neighbours) {
                        if (nextPoint != fromPoint) {
                            stack.push(WalkState(nextPoint, node, point, visited.toMutableSet(), 1))
                        }
                    }
                } else {
                    // This is not a junction, continue walking
                    val nextPoint = neighbours.first { it != fromPoint }
                    stack.push(WalkState(nextPoint, fromNode, point, visited, length + 1))
                }
            }

            return IceGraph(nodes.values.toList(), edges)
        }

        fun buildGraph(
            map: List<String>,
            start: Point,
            end: Point,
            part1: Boolean
        ): Map<Point, Map<Point, Int>> {
            val graph = mutableMapOf<Point, MutableMap<Point, Int>>()
            populateIntersection(map, start, end, graph, part1)
            return graph
        }

        fun populateIntersection(
            map: List<String>,
            current: Point,
            mapEnd: Point,
            graph: MutableMap<Point, MutableMap<Point, Int>>,
            part1: Boolean
        ) {
            val currentNeighbours = nextPointsA(current, map)
            val paths = currentNeighbours.mapNotNull {
                searchUntilIntersection(it, current, map, mapEnd)
            }
            graph[current] = paths.toMap(mutableMapOf())

            paths.forEach { (point, _) ->
                if (point !in graph) {
                    populateIntersection(map, point, mapEnd, graph, part1)
                }
            }

            if (!part1) {
                paths.forEach { (end, cost) ->
                    graph[end]?.put(current, cost)
                }
            }
        }

        fun searchUntilIntersection(
            first: Point,
            previous: Point,
            map: List<String>,
            mapEnd: Point
        ): Pair<Point, Int>? {
            val currentPath = mutableSetOf(previous)
            var current = first
            var neighbours = nextPointsA(current, map).filter { it !in currentPath }
            while (neighbours.size == 1) {
                currentPath.add(current)
                current = neighbours.first()
                neighbours = nextPointsA(current, map).filter { it !in currentPath }
            }

            return if (neighbours.isNotEmpty() || current == mapEnd) {
                current to currentPath.size
            } else {
                null
            }
        }

        fun nextPointsA(point: Point, map: List<String>): List<Point> {
            return if (point.y == -1) {
                listOf(point + Point(0, 1))
            } else when (map[point]) {
                '.' -> point.neighbours()
                '<' -> listOf(Point(point.x - 1, point.y))
                '>' -> listOf(Point(point.x + 1, point.y))
                '^' -> listOf(Point(point.x, point.y - 1))
                'v' -> listOf(Point(point.x, point.y + 1))
                else -> {
                    println("Unexpected path char ${map[point]}")
                    emptyList()
                }
            }.filter { map.containsPoint(it) && map[it] != '#' }
        }
    }

    data class Node(val point: Point) {
        val edges = mutableListOf<Edge>()
    }

    data class Edge(val node1: Node, val node2: Node, val length: Int) : Comparable<Edge> {
        override fun compareTo(other: Edge): Int {
            return length.compareTo(other.length)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Edge

            return (node1 == other.node1 && node2 == other.node2) || (node1 == other.node2 && node2 == other.node1)
        }

        override fun hashCode(): Int {
            return node1.hashCode() + node2.hashCode()
        }
    }

    data class IceGraph(val nodes: List<Node>, val edges: List<Edge>) {
        fun paths(start: Point, end: Point): List<List<Edge>> {
            val startNode = nodes.find { it.point == start }
            val endNode = nodes.find { it.point == end }
            if (startNode == null || endNode == null) return emptyList()

            val visitedEdges = mutableSetOf<Edge>()
            val currentPath = mutableListOf<Edge>()
            val allPaths = mutableListOf<List<Edge>>()

            dfs(startNode, endNode, visitedEdges, currentPath, allPaths)

            return allPaths
        }

        fun dfs(current: Node, end: Node, visitedEdges: MutableSet<Edge>, currentPath: MutableList<Edge>, allPaths: MutableList<List<Edge>>) {
            if (current == end) {
                allPaths.add(ArrayList(currentPath))
                return
            }

            for (edge in current.edges) {
                if (visitedEdges.contains(edge)) continue

                visitedEdges.add(edge)
                currentPath.add(edge)

                val nextNode = if (edge.node1 == current) edge.node2 else edge.node1
                dfs(nextNode, end, visitedEdges, currentPath, allPaths)

                visitedEdges.remove(edge)
                currentPath.removeAt(currentPath.size - 1)
            }
        }
    }

    fun toIceGrid(data: List<String>): IceGrid {
        return IceGrid(GridDataUtils.mapCharPointsFromLines(data), data[0].length, data.size)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}