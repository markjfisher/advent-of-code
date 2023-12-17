package net.fish.network

import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.geometry.points
import net.fish.y2021.GridDataUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class AStarSearchKtTest {
    @Test
    fun `can search simple graph`() {
        val data = """
            11122
            22133
            33111
        """.trimIndent().split("\n")
        val grid = GridDataUtils.mapIntPointsFromLines(data)
        val searchResult = findShortestPath(
            Point(0,0),                              // start
            Point(4, 2),                             // end
            { it.neighbours().filter { p -> p in grid } },  // valid neighbours of current point, anything in a cardinal direction and if it's in the grid
            { pointFrom, pointTo ->                         // prev, next points. return a cost to move. here, just the location cost
                grid.getOrDefault(pointTo, Int.MAX_VALUE)   // always in the map in this case, as we filter neighbours
            }
        )
        val path = searchResult.getPath()
        assertThat(path).containsExactly(
            Point(0, 0),
            Point(1, 0),
            Point(2, 0),
            Point(2, 1),
            Point(2, 2),
            Point(3, 2),
            Point(4, 2),
        )
        // initial point doesn't add to the cost, as we only cost moving to next position
        assertThat(searchResult.getScore()).isEqualTo(6)
        // we were able to visit everything
        assertThat(searchResult.seen()).containsExactlyInAnyOrder(
            Point(x=0, y=0),
            Point(x=0, y=1),
            Point(x=0, y=2),
            Point(x=1, y=0),
            Point(x=1, y=1),
            Point(x=1, y=2),
            Point(x=2, y=0),
            Point(x=2, y=1),
            Point(x=2, y=2),
            Point(x=3, y=0),
            Point(x=3, y=1),
            Point(x=3, y=2),
            Point(x=4, y=0),
            Point(x=4, y=1),
            Point(x=4, y=2),
        )

        val allScores = grid.keys.bounds().points().map { p ->
            Pair(p, searchResult.getScore(p))
        }.toList()
        assertThat(allScores).containsExactlyInAnyOrder(
            Pair(Point(x=0, y=0), 0),
            Pair(Point(x=1, y=0), 1),
            Pair(Point(x=2, y=0), 2),
            Pair(Point(x=3, y=0), 4),
            Pair(Point(x=4, y=0), 6),
            Pair(Point(x=0, y=1), 2),
            Pair(Point(x=1, y=1), 3),
            Pair(Point(x=2, y=1), 3),
            Pair(Point(x=3, y=1), 6),
            Pair(Point(x=4, y=1), 9),
            Pair(Point(x=0, y=2), 5),
            Pair(Point(x=1, y=2), 6),
            Pair(Point(x=2, y=2), 4),
            Pair(Point(x=3, y=2), 5),
            Pair(Point(x=4, y=2), 6)
        )
    }

    @Test
    fun `can search blocked graph`() {
        val data = """
            111X2
            22X33
            33111
        """.trimIndent().split("\n")
        val grid = GridDataUtils.mapCharPointsFromLines(data)
        val searchResult = findShortestPath(
            Point(0,0),
            Point(4, 2),
            { it.neighbours().filter { p -> p in grid && grid[p] != 'X' } }, // allow it if it's not blocked
            { pointFrom, pointTo ->
                val c = grid.getOrDefault(pointTo, 'X')
                when {
                    c == 'X' -> throw Exception("shouldn't be allowed onto X")
                    else -> c.digitToInt()
                }
            }
        )
        val path = searchResult.getPath()
        assertThat(path).containsExactly(
            Point(x=0, y=0),
            Point(x=1, y=0),
            Point(x=1, y=1),
            Point(x=1, y=2),
            Point(x=2, y=2),
            Point(x=3, y=2),
            Point(x=4, y=2),
        )
        // initial point doesn't add to the cost, as we only cost moving to next position
        assertThat(searchResult.getScore()).isEqualTo(9)
    }
}