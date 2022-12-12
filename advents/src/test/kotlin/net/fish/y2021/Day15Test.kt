package net.fish.y2021

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day15Test {
    private val testData = resourcePath("/2021/day15-test.txt")

    @Test
    fun `can read test data and create part 1 path with a star search`() {
        val chitonGrid = Day15.createChitonGraph(testData, 1)

        val start = chitonGrid.grid.square(0, 0)!!
        val end = chitonGrid.grid.square(9, 9)!!
        val result = chitonGrid.aStarSearch(start, end, true)
        val path = result.first
        assertThat(path.map { Point(it.x, it.y) }).containsExactly(
            Point(x=0, y=0),
            Point(x=0, y=1),
            Point(x=0, y=2),
            Point(x=1, y=2),
            Point(x=2, y=2),
            Point(x=3, y=2),
            Point(x=4, y=2),
            Point(x=5, y=2),
            Point(x=6, y=2),
            Point(x=6, y=3),
            Point(x=7, y=3),
            Point(x=7, y=4),
            Point(x=8, y=4),
            Point(x=8, y=5),
            Point(x=8, y=6),
            Point(x=8, y=7),
            Point(x=8, y=8),
            Point(x=9, y=8),
            Point(x=9, y=9)
        )
        val cost = result.second
        assertThat(cost).isEqualTo(40)
    }

    @Test
    fun `can read test data and create part 1 path with dijkstra search`() {
        val chitonGrid = Day15.createChitonGraph(testData, 1)

        val start = chitonGrid.grid.square(0, 0)!!
        val end = chitonGrid.grid.square(9, 9)!!
        val result = chitonGrid.dijkstraSearch(start, end, true)
        val path = result.first
        assertThat(path.map { Point(it.x, it.y) }).containsExactly(
            Point(x=0, y=0),
            Point(x=0, y=1),
            Point(x=0, y=2),
            Point(x=1, y=2),
            Point(x=2, y=2),
            Point(x=3, y=2),
            Point(x=4, y=2),
            Point(x=5, y=2),
            Point(x=6, y=2),
            Point(x=6, y=3),
            Point(x=7, y=3),
            Point(x=7, y=4),
            Point(x=7, y=5), // corrected after fixing error in costBasedSquareComparator
            Point(x=8, y=5),
            Point(x=8, y=6),
            Point(x=8, y=7),
            Point(x=8, y=8),
            Point(x=9, y=8),
            Point(x=9, y=9)
        )
        val cost = result.second
        assertThat(cost).isEqualTo(40)
    }

    @Test
    fun `can expand test data`() {
        val expansions = 5
        val chitonGrid = Day15.createChitonGraph(testData, 5)
        val gridAsStrings = (0 until 10 * expansions).map { y ->
            (0 until 10 * expansions).fold("") { string, x ->
                "$string${chitonGrid.storage.getData(chitonGrid.grid.square(x, y)!!)!!.cost}"
            }
        }
        assertThat(gridAsStrings).containsExactly(
            "01637517422274862853338597396444961841755517295286",
            "13813736722492484783351359589446246169155735727126",
            "21365113283247622439435873354154698446526571955763",
            "36949315694715142671582625378269373648937148475914",
            "74634171118574528222968563933317967414442817852555",
            "13191281372421239248353234135946434524615754563572",
            "13599124212461123532357223464346833457545794456865",
            "31254216394236532741534764385264587549637569865174",
            "12931385212314249632342535174345364628545647573965",
            "23119445813422155692453326671356443778246755488935",
            "22748628533385973964449618417555172952866628316397",
            "24924847833513595894462461691557357271266846838237",
            "32476224394358733541546984465265719557637682166874",
            "47151426715826253782693736489371484759148259586125",
            "85745282229685639333179674144428178525553928963666",
            "24212392483532341359464345246157545635726865674683",
            "24611235323572234643468334575457944568656815567976",
            "42365327415347643852645875496375698651748671976285",
            "23142496323425351743453646285456475739656758684176",
            "34221556924533266713564437782467554889357866599146",
            "33859739644496184175551729528666283163977739427418",
            "35135958944624616915573572712668468382377957949348",
            "43587335415469844652657195576376821668748793277985",
            "58262537826937364893714847591482595861259361697236",
            "96856393331796741444281785255539289636664139174777",
            "35323413594643452461575456357268656746837976785794",
            "35722346434683345754579445686568155679767926678187",
            "53476438526458754963756986517486719762859782187396",
            "34253517434536462854564757396567586841767869795287",
            "45332667135644377824675548893578665991468977611257",
            "44961841755517295286662831639777394274188841538529",
            "46246169155735727126684683823779579493488168151459",
            "54698446526571955763768216687487932779859814388196",
            "69373648937148475914825958612593616972361472718347",
            "17967414442817852555392896366641391747775241285888",
            "46434524615754563572686567468379767857948187896815",
            "46833457545794456865681556797679266781878137789298",
            "64587549637569865174867197628597821873961893298417",
            "45364628545647573965675868417678697952878971816398",
            "56443778246755488935786659914689776112579188722368",
            "55172952866628316397773942741888415385299952649631",
            "57357271266846838237795794934881681514599279262561",
            "65719557637682166874879327798598143881961925499217",
            "71484759148259586125936169723614727183472583829458",
            "28178525553928963666413917477752412858886352396999",
            "57545635726865674683797678579481878968159298917926",
            "57944568656815567976792667818781377892989248891319",
            "75698651748671976285978218739618932984172914319528",
            "56475739656758684176786979528789718163989182927419",
            "67554889357866599146897761125791887223681299833479",
        )
    }

    @Test
    fun `can do part 1`() {
        assertThat(Day15.solve(testData, 1)).isEqualTo(40)
    }

    @Test
    fun `can do part 2`() {
        assertThat(Day15.solve(testData, 5)).isEqualTo(315)
    }

}