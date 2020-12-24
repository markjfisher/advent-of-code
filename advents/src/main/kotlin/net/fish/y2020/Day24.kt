package net.fish.y2020

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import net.fish.Day
import net.fish.move
import net.fish.resourceLines
import net.fish.y2020.HEX_DIRECTION.E
import net.fish.y2020.HEX_DIRECTION.NE
import net.fish.y2020.HEX_DIRECTION.NW
import net.fish.y2020.HEX_DIRECTION.SE
import net.fish.y2020.HEX_DIRECTION.SW
import net.fish.y2020.HEX_DIRECTION.W

object Day24 : Day {
    override val warmUps = 1
    private val data = resourceLines(2020, 24)
    private val logger = KotlinLogging.logger { }

    var blackTiles: MutableSet<Pair<Int, Int>> = mutableSetOf()

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val ends = endPointsOfWalk(toHexWalks(data))
        blackTiles = ends.fold(mutableSetOf()) { acc, end ->
            if (acc.contains(end)) acc.remove(end) else acc.add(end)
            acc
        }
        return blackTiles.count()
    }

    fun doPart2(data: List<String>): Int {
        if (blackTiles.isEmpty()) doPart1(data)
        (0 until 100).forEach { conwayHexStep() }
        return blackTiles.count()
    }

    fun conwayHexStep() {
        val allTouchingPositions = mutableSetOf<Pair<Int, Int>>()
        blackTiles.chunked(5).forEach { chunk ->
            allTouchingPositions.addAll(chunk.flatMap { neighbourPositions(it) })
        }

        val tileSplitSize = allTouchingPositions.size / 12
        val locs = allTouchingPositions.chunked(tileSplitSize)
        runBlocking {
            val deferreds = locs.map { loc ->
                async(Dispatchers.Default) { calculateAsync(loc) }
            }
            blackTiles = mutableSetOf<Pair<Int, Int>>().also { set ->
                deferreds.awaitAll().map { locs ->
                    set.addAll(locs)
                }
            }
        }

    }

    suspend fun calculateAsync(locs: List<Pair<Int, Int>>): Set<Pair<Int, Int>> = withContext(Dispatchers.Default) {
        calculate(locs)
    }

    fun calculate(locs: List<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        return locs.fold(mutableSetOf()) { newFlipped, location ->
            val neighbourCount = neighbourCount(location)
            val isBlack = blackTiles.contains(location)

            when {
                isBlack && (neighbourCount == 0 || neighbourCount > 2) -> newFlipped.remove(location)
                !isBlack && neighbourCount == 2 -> newFlipped.add(location)
                isBlack -> newFlipped.add(location)
            }

            newFlipped
        }
    }

    fun neighbourPositions(point: Pair<Int, Int>): Set<Pair<Int, Int>> {
        return setOf(
            Pair(point.first - 1, point.second - 1),
            Pair(point.first - 1, point.second + 1),
            Pair(point.first - 2, point.second),
            Pair(point.first + 2, point.second),
            Pair(point.first + 1, point.second - 1),
            Pair(point.first + 1, point.second + 1),
        )
    }

    fun neighbourCount(point: Pair<Int, Int>): Int {
        return blackTiles.intersect(neighbourPositions(point)).count()
    }

    fun endPointsOfWalk(paths: List<List<Pair<Int, Int>>>): List<Pair<Int, Int>> {
        return paths.map { it.last() }
    }

    fun toHexWalks(data: List<String>): List<List<Pair<Int, Int>>> {
        // each line represents a walk without delimiters, so break the instruction up
        return data.map { line ->
            val path = mutableListOf<Pair<Int, Int>>()
            val hexDirections: List<HEX_DIRECTION> = toHexDirections(line, emptyList())
            var currentPosition = Pair(0, 0)
            path.add(currentPosition)
            hexDirections.forEach { direction ->
                currentPosition = when (direction) {
                    NW -> move(currentPosition, 1, Pair(-1, 1), path)
                    NE -> move(currentPosition, 1, Pair(1, 1), path)
                    E -> move(currentPosition, 1, Pair(2, 0), path)
                    SE -> move(currentPosition, 1, Pair(1, -1), path)
                    SW -> move(currentPosition, 1, Pair(-1, -1), path)
                    W -> move(currentPosition, 1, Pair(-2, 0), path)
                }
            }
            path.toList()
        }
    }

    tailrec fun toHexDirections(line: String, builtDirections: List<HEX_DIRECTION> = emptyList()): List<HEX_DIRECTION> {
        if (line.isEmpty()) return builtDirections
        // line like neew = ne e w, must do North/South first
        val direction = when {
            line.startsWith("nw") -> NW
            line.startsWith("ne") -> NE
            line.startsWith("sw") -> SW
            line.startsWith("se") -> SE
            line.startsWith("e") -> E
            line.startsWith("w") -> W
            else -> throw Exception("next direction unknown in $line")
        }
        return toHexDirections(line.substring(direction.value.length), builtDirections + direction)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

enum class HEX_DIRECTION(val value: String) {
    E("e"), SE("se"), SW("sw"), W("w"), NW("nw"), NE("ne")
}

/*
--- Day 24: Lobby Layout ---
Your raft makes it to the tropical island; it turns out that the small crab was an
excellent navigator. You make your way to the resort.

As you enter the lobby, you discover a small problem: the floor is being renovated.
You can't even reach the check-in desk until they've finished installing the new tile floor.

The tiles are all hexagonal; they need to be arranged in a hex grid with a very
specific color pattern. Not in the mood to wait, you offer to help figure out the pattern.

The tiles are all white on one side and black on the other. They start with the
white side facing up. The lobby is large enough to fit whatever pattern might need
to appear there.

A member of the renovation crew gives you a list of the tiles that need to be flipped
over (your puzzle input). Each line in the list identifies a single tile that needs
to be flipped by giving a series of steps starting from a reference tile in the very
center of the room. (Every line starts from the same reference tile.)

Because the tiles are hexagonal, every tile has six neighbors:
east, southeast, southwest, west, northwest, and northeast.

These directions are given in your list, respectively, as e, se, sw, w, nw, and ne.

A tile is identified by a series of these directions with no delimiters; for example,
esenee identifies the tile you land on if you start at the reference tile and then
move one tile east, one tile southeast, one tile northeast, and one tile east.

Each time a tile is identified, it flips from white to black or from black to white.
Tiles might be flipped more than once. For example, a line like esew flips a tile
immediately adjacent to the reference tile, and a line like nwwswee flips the
reference tile itself.

Here is a larger example:

sesenwnenenewseeswwswswwnenewsewsw
neeenesenwnwwswnenewnwwsewnenwseswesw
seswneswswsenwwnwse
nwnwneseeswswnenewneswwnewseswneseene
swweswneswnenwsewnwneneseenw
eesenwseswswnenwswnwnwsewwnwsene
sewnenenenesenwsewnenwwwse
wenwwweseeeweswwwnwwe
wsweesenenewnwwnwsenewsenwwsesesenwne
neeswseenwwswnwswswnw
nenwswwsewswnenenewsenwsenwnesesenew
enewnwewneswsewnwswenweswnenwsenwsw
sweneswneswneneenwnewenewwneswswnese
swwesenesewenwneswnwwneseswwne
enesenwswwswneneswsenwnewswseenwsese
wnwnesenesenenwwnenwsewesewsesesew
nenewswnwewswnenesenwnesewesw
eneswnwswnwsenenwnwnwwseeswneewsenese
neswnwewnwnwseenwseesewsenwsweewe
wseweeenwnesenwwwswnew

In the above example, 10 tiles are flipped once (to black), and 5 more are flipped twice
(to black, then back to white). After all of these instructions have been followed, a total
of 10 tiles are black.

Go through the renovation crew's list and determine which tiles they need to flip.
After all of the instructions have been followed, how many tiles are left with the black side up?


 */

/*
--- Part Two ---
The tile floor in the lobby is meant to be a living art exhibit. Every day, the tiles are all
flipped according to the following rules:

Any black tile with zero or more than 2 black tiles immediately adjacent to it is flipped to white.
Any white tile with exactly 2 black tiles immediately adjacent to it is flipped to black.
Here, tiles immediately adjacent means the six tiles directly touching the tile in question.

The rules are applied simultaneously to every tile; put another way, it is first determined which
tiles need to be flipped, then they are all flipped at the same time.

In the above example, the number of black tiles that are facing up after the given number of days
has passed is as follows:

Day 1: 15
Day 2: 12
Day 3: 25
Day 4: 14
Day 5: 23
Day 6: 28
Day 7: 41
Day 8: 37
Day 9: 49
Day 10: 37

Day 20: 132
Day 30: 259
Day 40: 406
Day 50: 566
Day 60: 788
Day 70: 1106
Day 80: 1373
Day 90: 1844
Day 100: 2208

After executing this process a total of 100 times, there would be 2208 black tiles facing up.

How many tiles will be black after 100 days?
 */