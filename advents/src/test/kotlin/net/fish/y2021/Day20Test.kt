package net.fish.y2021

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day20Test {
    private val testData = resourcePath("/2021/day20-test.txt")
    private val testData2 = resourcePath("/2021/day20-2-test.txt")

    @Test
    fun `can do part 1 and 2 on test data`() {
        assertThat(Day20.solve(testData, 2)).isEqualTo(35)
//        println("starting 10")
//        Day20.solve(testData, 10)
//        println("starting 20")
//        Day20.solve(testData, 20)
//        println("starting 30")
//        Day20.solve(testData, 30)
//        println("doing 50")
        Day20.solve(testData, 50)
    }

    @Test
    fun `can evolve when bit 0 is set in algorithm`() {
        val trench = Day20.TrenchMap.parseInput(testData2)
        val e1 = trench.evolve(1)
        println(e1.stringGrid())
    }

    @Test
    fun `can parse input data`() {
        val trench = Day20.TrenchMap.parseInput(testData)
        assertThat(trench.algorithm).hasSize(238)
        assertThat(trench.algorithm).containsExactly(
            2,
            4,
            7,
            8,
            9,
            10,
            11,
            13,
            15,
            17,
            19,
            20,
            21,
            23,
            24,
            30,
            31,
            32,
            34,
            35,
            37,
            40,
            41,
            42,
            44,
            45,
            46,
            47,
            50,
            51,
            52,
            53,
            54,
            57,
            62,
            65,
            68,
            69,
            72,
            73,
            74,
            77,
            78,
            79,
            80,
            81,
            82,
            84,
            85,
            86,
            90,
            91,
            92,
            93,
            96,
            99,
            100,
            101,
            102,
            103,
            106,
            107,
            110,
            112,
            113,
            114,
            115,
            116,
            120,
            121,
            123,
            125,
            128,
            130,
            131,
            134,
            136,
            143,
            145,
            146,
            147,
            149,
            150,
            151,
            152,
            153,
            154,
            156,
            157,
            158,
            160,
            161,
            162,
            163,
            167,
            169,
            170,
            172,
            173,
            176,
            179,
            182,
            183,
            184,
            185,
            186,
            192,
            194,
            199,
            200,
            201,
            204,
            206,
            207,
            214,
            220,
            223,
            226,
            229,
            230,
            233,
            237,
            238,
            240,
            241,
            242,
            243,
            244,
            245,
            247,
            248,
            249,
            250,
            252,
            253,
            254,
            255,
            257,
            259,
            263,
            271,
            274,
            276,
            278,
            282,
            283,
            284,
            285,
            287,
            288,
            290,
            297,
            300,
            304,
            305,
            307,
            309,
            310,
            313,
            317,
            318,
            320,
            322,
            323,
            326,
            327,
            328,
            330,
            337,
            339,
            347,
            349,
            351,
            353,
            354,
            355,
            356,
            358,
            359,
            360,
            362,
            363,
            367,
            373,
            374,
            375,
            376,
            378,
            381,
            384,
            386,
            387,
            389,
            394,
            395,
            398,
            400,
            401,
            402,
            403,
            408,
            409,
            413,
            414,
            417,
            421,
            428,
            430,
            438,
            446,
            447,
            450,
            451,
            452,
            453,
            456,
            460,
            462,
            464,
            468,
            469,
            472,
            474,
            477,
            478,
            479,
            482,
            483,
            484,
            485,
            486,
            495,
            498,
            499,
            500,
            501,
            508,
            511
        )
        assertThat(trench.imageMap).containsExactly(
            Point(0, 0), Point(3, 0),
            Point(0, 1),
            Point(0, 2), Point(1, 2), Point(4, 2),
            Point(2, 3),
            Point(2, 4), Point(3, 4), Point(4, 4)
        )
    }

    @Test
    fun `can get point values in image`() {
        val trench = Day20.TrenchMap.parseInput(testData)
        assertThat(Day20.TrenchMap.pointValue(Point(2, 2), trench.algorithm, trench.imageMap, trench.shouldConsiderInfinite)).isEqualTo(34)
    }

    @Test
    fun `can print the grid for lols`() {
        val trench = Day20.TrenchMap.parseInput(testData)
        val grid = trench.stringGrid()
        println(grid)
        assertThat(grid).isEqualTo(
            """
            .......
            .#..#..
            .#.....
            .##..#.
            ...#...
            ...###.
            .......
        
        """.trimIndent()
        )
    }

    @Test
    fun `can evolve grid`() {
        val trench = Day20.TrenchMap.parseInput(testData)
        val e1 = trench.evolve(1)
        assertThat(e1.stringGrid()).isEqualTo(
            """
            .........
            ..##.##..
            .#..#.#..
            .##.#..#.
            .####..#.
            ..#..##..
            ...##..#.
            ....#.#..
            .........
            
            """.trimIndent()
        )

        val e2 = trench.evolve(2)
        assertThat(e2.stringGrid()).isEqualTo(
            """
            ...........
            ........#..
            ..#..#.#...
            .#.#...###.
            .#...##.#..
            .#.....#.#.
            ..#.#####..
            ...#.#####.
            ....##.##..
            .....###...
            ...........
            
            """.trimIndent()
        )
    }
}