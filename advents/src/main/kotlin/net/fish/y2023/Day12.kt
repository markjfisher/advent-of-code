package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines

object Day12 : Day {
    private val data by lazy { resourceLines(2023, 12) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Long {
        return parsePuzzle(data).sumOf { count(it.first, it.second) }
    }

    fun doPart2(data: List<String>): Long {
        return parsePuzzle(data).sumOf {
            val newCharSeq = "${it.first}?".repeat(5).dropLast(1)
            val newNumSeq = "${it.second.joinToString(",")},".repeat(5).dropLast(1).split(",").map(String::toInt)
            count(newCharSeq, newNumSeq)
        }
    }

    fun parsePuzzle(data: List<String>): List<Pair<String, List<Int>>> = data.map { line ->
        val parts = line.split(" ", limit = 2)
        Pair(parts[0], parts[1].split(",").map { it.trim().toInt() })
    }

    private val cache = mutableMapOf<Pair<String, List<Int>>, Long>()
    fun count(config: String, seq: List<Int>): Long {
        // Base cases
        if (seq.isEmpty()) {
            // "#" can't match an empty sequence, but a "." or "?" or empty can match it
            return if ("#" in config) 0 else 1
        }
        if (config.isEmpty()) {
            // no config, but we have a sequence, so return 0 count
            return 0
        }

//        if (cache.contains(config to seq)) {
//            println ("FETCHING: [$config, $seq] = ${cache[config to seq]}")
//        }
        // recurse into more complex cases, and store their values as we find them
        return cache.getOrPut(config to seq) {
            var result = 0L

            // "." case, reduce the config and try again
            if (config.first() in ".?") {
//                println("rec #1, $config -> ${config.drop(1)}")
                result += count(config.drop(1), seq)
            }

            // "#" case, is the first sequence <= length of config (no "."), and don't have a # at config[first in seq] (bounding the sequence to correct length)
            if (config.first() in "#?" &&                                       // we have a # (or ?) in first char
                seq.first() <= config.length &&                                 // first sequence is <= current length of config
                "." !in config.take(seq.first()) &&                             // there's no "." in the next sequence characters
                (seq.first() == config.length || config[seq.first()] != '#')    // bounding the sequence so there's no extra # extending it
            ) {
//                println("rec #2, $config -> ${config.drop(seq.first() + 1)}, $seq -> ${seq.drop(1)}")
                val reducedConfig = config.drop(seq.first() + 1)                // yes! drop the sequence length + 1 (the extra "." we must have)
                val reducedSequence = seq.drop(1)                                    // and the next sequence, and find result of the next piece along
                result += count(reducedConfig, reducedSequence)
            }
//            println ("STORING [$config, $seq] -> $result")
            // finally have a count for this config and sequence
            result
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}