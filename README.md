# Advent of code

My kotlin solutions to [Advent of Code](https://adventofcode.com/) puzzles.

To run all days, adjust year, but run with:

    ./gradlew advent2022

## windows

To run tests, the files must be in unix format when checked out of git, so use

```shell
git config --global core.eol lf
git config --global core.autocrlf input

# if you had checked it out without setting above first, you need to fix files with CRLF into LF:
git rm -rf --cached .
git reset --hard HEAD
```

## coding notes

### Reading resources

1. column of data, separator is EOL

Example 2022 02

```kotlin
// simply split into List<String>
resourceLines(2022, 2)

// then can deal with each line
data.fold(0) { acc, l ->
    val (a,b) = l.split(" ", limit = 2)
    // ... etc
```

1. column data with blank line separator

Example, 2022 01

```text
5538
6760

5212
2842
```

```kotlin
// first a list of strings for each block (which still have \n in them)
private val data by lazy { totals(resourceStrings(2022, 1)) }

// Then split by \n
data.map { block ->
    block.split("\n").sumOf { f -> f.toInt() }
}
```

### Parsing data with regex

Example where each line contains information we want to extract parts out of
```text
12-80,12-81
13-94,14-93
...
```
Use `resourceLines` to pull each line, and then map that through a regex "Extractor"
```kotlin
private val assignmentExtractor by lazy { Regex("""(\d+)-(\d+),(\d+)-(\d+)""") }
private val data by lazy { toAssignments(resourceLines(2022, 4)) }

fun toAssignments(data: List<String>): List<Assignments> = data.map { line ->
    assignmentExtractor.find(line)?.destructured!!.let { (a, b, c, d) ->
        Assignments(IntRange(a.toInt(), b.toInt()), IntRange(c.toInt(), d.toInt()))
    }
}
```
Example 2022 04

### Grid data

See GridDataUtils for parsing grid information from lines where each point represents 1 datum, either as numbers:

```text
  1234
  2256
  1978
```

or as characters:

```text
  .x.
  yz.
```

#### Grid viewing

There are some good examples of looking from a point to a boundary in 2022 Day 08, using `generateSequence` on points in a direction until they hit the boundary.

```kotlin
private fun pointsToBoundaryInDir(p: Point, dir: Direction): Sequence<Point> {
    return generateSequence(p + dir) { it + dir }.takeWhileInclusive { !onBoundary(it) }
}
```
