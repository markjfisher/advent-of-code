package net.fish.y2020

import net.fish.Day
import net.fish.resourceLines
import java.math.BigInteger

object Day13 : Day {
    private val data = resourceLines(2020, 13)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data[1])

    fun doPart1(data: List<String>): Int {
        val targetTime = data[0].toInt()
        val frequencies = data[1].split(",").filterNot { it == "x" }.map { it.toInt() }

        val lowest = frequencies.map { lowestMultipleAfter(targetTime, it) }.minByOrNull { it.second }!!
        return (lowest.second - targetTime) * lowest.first
    }

    fun lowestMultipleAfter(after: Int, frequency: Int): Pair<Int, Int> {
        val lowest = generateSequence(1) { it + 1 }.map { it * frequency }.dropWhile { it < after }.first()
        return Pair(frequency, lowest)
    }

    fun doPart2(input: String): BigInteger {
        val idWithIndex = input.split(",").mapIndexed{ index, s ->
            Pair(index, s)
        }.filterNot { it.second == "x" }.map{ Pair(it.first, it.second.toInt()) }

        var currentStep = BigInteger.ONE
        var accum = BigInteger.ONE
        idWithIndex.forEach { (index, id) ->
            accum = nextMultipleWithOffsetSteppingBy(id, accum, index, currentStep)
            currentStep *= id.toBigInteger()
        }

        return accum
    }

    fun nextMultipleWithOffsetSteppingBy(frequency: Int, startingAt: BigInteger, offset: Int, step: BigInteger): BigInteger {
        return generateSequence(startingAt) { it + step }
            .dropWhile { (it + offset.toBigInteger()) % frequency.toBigInteger() != BigInteger.ZERO }
            .first()
    }

    fun generateInput(entries: Int, numPrimes: Int): String {
        val primeSubset = primes.shuffled().take(numPrimes)
        val positions = (1 until (entries - 1)).toList().shuffled().take(numPrimes - 2)
        val inputs = generateSequence { "x" }.take(entries).toList().toMutableList()
        inputs[0] = primeSubset[0].toString()
        inputs[entries - 1] = primeSubset[numPrimes - 1].toString()
        positions.forEachIndexed { index, pos ->
            inputs[pos] = primeSubset[index+1].toString()
        }

        return inputs.joinToString(",")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

    val primes = listOf(
        2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,
        109,113,127,131,137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,
        229,233,239,241,251,257,263,269,271,277,281,283,293,307,311,313,317,331,337,347,349,
        353,359,367,373,379,383,389,397,401,409,419,421,431,433,439,443,449,457,461,463,467,
        479,487,491,499,503,509,521,523,541,547,557,563,569,571,577,587,593,599,601,607,613,
        617,619,631,641,643,647,653,659,661,673,677,683,691,701,709,719,727,733,739,743,751,
        757,761,769,773,787,797,809,811,821,823,827,829,839,853,857,859,863,877,881,883,887,
        907,911,919,929,937,941,947,953,967,971,977,983,991,997
    )

}

/*
--- Day 13: Shuttle Search ---
Your ferry can make it safely to a nearby port, but it won't get much further.
When you call to book another ship, you discover that no ships embark from that
port to your vacation island. You'll need to get from the port to the nearest airport.

Fortunately, a shuttle bus service is available to bring you from the sea port
to the airport! Each bus has an ID number that also indicates how often the bus
leaves for the airport.

Bus schedules are defined based on a timestamp that measures the number of minutes
since some fixed reference point in the past. At timestamp 0, every bus simultaneously
departed from the sea port. After that, each bus travels to the airport, then various
other locations, and finally returns to the sea port to repeat its journey forever.

The time this loop takes a particular bus is also its ID number: the bus with ID 5 departs
from the sea port at timestamps 0, 5, 10, 15, and so on. The bus with ID 11 departs
at 0, 11, 22, 33, and so on. If you are there when the bus departs, you can ride that
bus to the airport!

Your notes (your puzzle input) consist of two lines. The first line is your estimate
of the earliest timestamp you could depart on a bus. The second line lists the bus IDs
that are in service according to the shuttle company; entries that show x must be out
of service, so you decide to ignore them.

To save time once you arrive, your goal is to figure out the earliest bus you can take
to the airport. (There will be exactly one such bus.)

For example, suppose you have the following notes:

939
7,13,x,x,59,x,31,19

Here, the earliest timestamp you could depart is 939, and the bus IDs in service are
7, 13, 59, 31, and 19. Near timestamp 939, these bus IDs depart at the times marked D:

time   bus 7   bus 13  bus 59  bus 31  bus 19
929      .       .       .       .       .
930      .       .       .       D       .
931      D       .       .       .       D
932      .       .       .       .       .
933      .       .       .       .       .
934      .       .       .       .       .
935      .       .       .       .       .
936      .       D       .       .       .
937      .       .       .       .       .
938      D       .       .       .       .
939      .       .       .       .       .
940      .       .       .       .       .
941      .       .       .       .       .
942      .       .       .       .       .
943      .       .       .       .       .
944      .       .       D       .       .
945      D       .       .       .       .
946      .       .       .       .       .
947      .       .       .       .       .
948      .       .       .       .       .
949      .       D       .       .       .

The earliest bus you could take is bus ID 59. It doesn't depart until timestamp 944,
so you would need to wait 944 - 939 = 5 minutes before it departs. Multiplying the
bus ID by the number of minutes you'd need to wait gives 295.

What is the ID of the earliest bus you can take to the airport multiplied by the
number of minutes you'll need to wait for that bus?
*/

/*
The shuttle company is running a contest: one gold coin for anyone that can find the
earliest timestamp such that the first bus ID departs at that time and each subsequent
listed bus ID departs at that subsequent minute. (The first line in your input is no
longer relevant.)

For example, suppose you have the same list of bus IDs as above:

7,13,x,x,59,x,31,19

An x in the schedule means there are no constraints on what bus IDs must depart at that time.

This means you are looking for the earliest timestamp (called t) such that:

Bus ID 7 departs at timestamp t.
Bus ID 13 departs one minute after timestamp t.
There are no requirements or restrictions on departures at two or three minutes after timestamp t.
Bus ID 59 departs four minutes after timestamp t.
There are no requirements or restrictions on departures at five minutes after timestamp t.
Bus ID 31 departs six minutes after timestamp t.
Bus ID 19 departs seven minutes after timestamp t.

The only bus departures that matter are the listed bus IDs at their specific offsets from t.
Those bus IDs can depart at other times, and other bus IDs can depart at those times.
For example, in the list above, because bus ID 19 must depart seven minutes after the timestamp
at which bus ID 7 departs, bus ID 7 will always also be departing with bus ID 19 at seven minutes
after timestamp t.

In this example, the earliest timestamp at which this occurs is 1068781:

time     bus 7   bus 13  bus 59  bus 31  bus 19
1068773    .       .       .       .       .
1068774    D       .       .       .       .
1068775    .       .       .       .       .
1068776    .       .       .       .       .
1068777    .       .       .       .       .
1068778    .       .       .       .       .
1068779    .       .       .       .       .
1068780    .       .       .       .       .
1068781    D       .       .       .       .
1068782    .       D       .       .       .
1068783    .       .       .       .       .
1068784    .       .       .       .       .
1068785    .       .       D       .       .
1068786    .       .       .       .       .
1068787    .       .       .       D       .
1068788    D       .       .       .       D
1068789    .       .       .       .       .
1068790    .       .       .       .       .
1068791    .       .       .       .       .
1068792    .       .       .       .       .
1068793    .       .       .       .       .
1068794    .       .       .       .       .
1068795    D       D       .       .       .
1068796    .       .       .       .       .
1068797    .       .       .       .       .

In the above example, bus ID 7 departs at timestamp 1068788 (seven minutes after t).
This is fine; the only requirement on that minute is that bus ID 19 departs then, and it does.

Here are some other examples:

The earliest timestamp that matches the list 17,x,13,19 is 3417.

67,7,59,61 first occurs at timestamp 754018.
67,x,7,59,61 first occurs at timestamp 779210.
67,7,x,59,61 first occurs at timestamp 1261476.
1789,37,47,1889 first occurs at timestamp 1202161486.

However, with so many bus IDs in your list, surely the actual earliest timestamp will be
larger than 100000000000000!

What is the earliest timestamp such that all of the listed bus IDs depart at offsets
matching their positions in the list?

 */

/*
PJ's breakdown that explains the solution.

We're looking to match an offset from multiples of the previous iteration.
Each time we get to next iteration, the sequencies repeat every multiple of the previous frequencies (like the planets puzzle).
hence the "Stride" value is product of the bus ids

Bus: 7 Bus index: 0 RunTime: 7 Stride: 1
========== Increment time by time stride: '1' where strided time + bus index: '0' is a multiple of bus id: '7'
-------- RunTime: 7 RunTime+index: 7

Bus: 13 Bus index: 1 RunTime: 7 Stride: 7
========== Increment time by time stride: '7' where strided time + bus index: '1' is a multiple of bus id: '13'
-------- RunTime: 7 RunTime+index: 8
-------- RunTime: 14 RunTime+index: 15
-------- RunTime: 21 RunTime+index: 22
-------- RunTime: 28 RunTime+index: 29
-------- RunTime: 35 RunTime+index: 36
-------- RunTime: 42 RunTime+index: 43
-------- RunTime: 49 RunTime+index: 50
-------- RunTime: 56 RunTime+index: 57
-------- RunTime: 63 RunTime+index: 64
-------- RunTime: 70 RunTime+index: 71
-------- RunTime: 77 RunTime+index: 78

Bus: 59 Bus index: 4 RunTime: 77 Stride: 91
========== Increment time by time stride: '91' where strided time + bus index: '4' is a multiple of bus id: '59'
-------- RunTime: 77 RunTime+index: 81
-------- RunTime: 168 RunTime+index: 172
-------- RunTime: 259 RunTime+index: 263
-------- RunTime: 350 RunTime+index: 354

Bus: 31 Bus index: 6 RunTime: 350 Stride: 5369
========== Increment time by time stride: '5369' where strided time + bus index: '6' is a multiple of bus id: '31'
-------- RunTime: 350 RunTime+index: 356
-------- RunTime: 5719 RunTime+index: 5725
-------- RunTime: 11088 RunTime+index: 11094
-------- RunTime: 16457 RunTime+index: 16463
-------- RunTime: 21826 RunTime+index: 21832
-------- RunTime: 27195 RunTime+index: 27201
-------- RunTime: 32564 RunTime+index: 32570
-------- RunTime: 37933 RunTime+index: 37939
-------- RunTime: 43302 RunTime+index: 43308
-------- RunTime: 48671 RunTime+index: 48677
-------- RunTime: 54040 RunTime+index: 54046
-------- RunTime: 59409 RunTime+index: 59415
-------- RunTime: 64778 RunTime+index: 64784
-------- RunTime: 70147 RunTime+index: 70153

Bus: 19 Bus index: 7 RunTime: 70147 Stride: 166439
========== Increment time by time stride: '166439' where strided time + bus index: '7' is a multiple of bus id: '19'
-------- RunTime: 70147 RunTime+index: 70154
-------- RunTime: 236586 RunTime+index: 236593
-------- RunTime: 403025 RunTime+index: 403032
-------- RunTime: 569464 RunTime+index: 569471
-------- RunTime: 735903 RunTime+index: 735910
-------- RunTime: 902342 RunTime+index: 902349
-------- RunTime: 1068781 RunTime+index: 1068788

ANSWER: 1068781

 */