package net.fish.y2022

import net.fish.resourceStrings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day13Test {
    @Test
    fun `can do part 1`() {
        assertThat(Day13.doPart1(resourceStrings("/2022/day13-test.txt"))).isEqualTo(13)
    }

    @Test
    fun `can do part 2`() {
        assertThat(Day13.doPart2(resourceStrings("/2022/day13-test.txt"))).isEqualTo(140)
    }

    @Test
    fun `can parse input data`() {
        assertThat(DistressSignalProcessor.parseData(listOf(listOf("[1,1,3,1,1]", "[1,1,5,1,1]")))).containsExactly(DSPPair(
            left = DSPList(mutableListOf(DSPValue(1), DSPValue(1), DSPValue(3), DSPValue(1), DSPValue(1))),
            right = DSPList(mutableListOf(DSPValue(1), DSPValue(1), DSPValue(5), DSPValue(1), DSPValue(1)))
        ))

        val dsp2 = DistressSignalProcessor.parseData(listOf(listOf("[[1],[2,3,4]]", "[[1],4]")))
        assertThat(dsp2).containsExactly(DSPPair(
            left = DSPList(mutableListOf(
                DSPList(mutableListOf(DSPValue(1))),
                DSPList(mutableListOf(DSPValue(2), DSPValue(3), DSPValue(4)))
            )),
            right = DSPList(mutableListOf(
                DSPList(mutableListOf(DSPValue(1))),
                DSPValue(4)
            )),
        ))
    }

    @Test
    fun `can compare simple DSPLists`() {
        val dspList1 = DSPList(mutableListOf(DSPValue(1)))
        val dspList2 = DSPList(mutableListOf(DSPValue(2)))

        assertThat(dspList1 > dspList1).isFalse
        assertThat(dspList1 < dspList2).isTrue
        assertThat(dspList1 <= dspList2).isTrue
        assertThat(dspList1 > dspList2).isFalse
        assertThat(dspList1 >= dspList2).isFalse
    }

    @Test
    fun `can compare complex DSPLists`() {
        val dspEq = DistressSignalProcessor.parseData(listOf(listOf("[[1],2,[3,4]]", "[[1],2,[3,4]]"))).first()
        assertThat(dspEq.left < dspEq.right).isFalse
        assertThat(dspEq.left <= dspEq.right).isTrue
        assertThat(dspEq.left != dspEq.right).isFalse
        assertThat(dspEq.left == dspEq.right).isTrue
        assertThat(dspEq.left > dspEq.right).isFalse
        assertThat(dspEq.left >= dspEq.right).isTrue

        val dsp0 = DistressSignalProcessor.parseData(listOf(listOf("[1,1,3,1,1]", "[1,1,5,1,1]"))).first()
        assertThat(dsp0.left < dsp0.right).isTrue
        assertThat(dsp0.left <= dsp0.right).isTrue
        assertThat(dsp0.left != dsp0.right).isTrue
        assertThat(dsp0.left == dsp0.right).isFalse
        assertThat(dsp0.left > dsp0.right).isFalse
        assertThat(dsp0.left >= dsp0.right).isFalse

        val dsp1 = DistressSignalProcessor.parseData(listOf(listOf("[[1],[2,3,4]]", "[[1],4]"))).first()
        assertThat(dsp1.left < dsp1.right).isTrue
        assertThat(dsp1.left <= dsp1.right).isTrue
        assertThat(dsp1.left != dsp1.right).isTrue
        assertThat(dsp1.left == dsp1.right).isFalse
        assertThat(dsp1.left > dsp1.right).isFalse
        assertThat(dsp1.left >= dsp1.right).isFalse

        val dsp2 = DistressSignalProcessor.parseData(listOf(listOf("[9]", "[[8,7,6]]"))).first()
        assertThat(dsp2.left < dsp2.right).isFalse
        assertThat(dsp2.left <= dsp2.right).isFalse
        assertThat(dsp2.left != dsp2.right).isTrue
        assertThat(dsp2.left == dsp2.right).isFalse
        assertThat(dsp2.left > dsp2.right).isTrue
        assertThat(dsp2.left >= dsp2.right).isTrue

        val dsp3 = DistressSignalProcessor.parseData(listOf(listOf("[[4,4],4,4]", "[[4,4],4,4,4]"))).first()
        assertThat(dsp3.left < dsp3.right).isTrue
        assertThat(dsp3.left <= dsp3.right).isTrue
        assertThat(dsp3.left != dsp3.right).isTrue
        assertThat(dsp3.left == dsp3.right).isFalse
        assertThat(dsp3.left > dsp3.right).isFalse
        assertThat(dsp3.left >= dsp3.right).isFalse

        val dsp4 = DistressSignalProcessor.parseData(listOf(listOf("[7,7,7,7]", "[7,7,7]"))).first()
        assertThat(dsp4.left < dsp4.right).isFalse
        assertThat(dsp4.left <= dsp4.right).isFalse
        assertThat(dsp4.left != dsp4.right).isTrue
        assertThat(dsp4.left == dsp4.right).isFalse
        assertThat(dsp4.left > dsp4.right).isTrue
        assertThat(dsp4.left >= dsp4.right).isTrue

        val dsp5 = DistressSignalProcessor.parseData(listOf(listOf("[]", "[3]"))).first()
        assertThat(dsp5.left < dsp5.right).isTrue
        assertThat(dsp5.left <= dsp5.right).isTrue
        assertThat(dsp5.left != dsp5.right).isTrue
        assertThat(dsp5.left == dsp5.right).isFalse
        assertThat(dsp5.left > dsp5.right).isFalse
        assertThat(dsp5.left >= dsp5.right).isFalse

        val dsp6 = DistressSignalProcessor.parseData(listOf(listOf("[[[]]]", "[[]]"))).first()
        assertThat(dsp6.left < dsp6.right).isFalse
        assertThat(dsp6.left <= dsp6.right).isFalse
        assertThat(dsp6.left != dsp6.right).isTrue
        assertThat(dsp6.left == dsp6.right).isFalse
        assertThat(dsp6.left > dsp6.right).isTrue
        assertThat(dsp6.left >= dsp6.right).isTrue

        val dsp7 = DistressSignalProcessor.parseData(listOf(listOf("[1,[2,[3,[4,[5,6,7]]]],8,9]", "[1,[2,[3,[4,[5,6,0]]]],8,9]"))).first()
        assertThat(dsp7.left < dsp7.right).isFalse
        assertThat(dsp7.left <= dsp7.right).isFalse
        assertThat(dsp7.left != dsp7.right).isTrue
        assertThat(dsp7.left == dsp7.right).isFalse
        assertThat(dsp7.left > dsp7.right).isTrue
        assertThat(dsp7.left >= dsp7.right).isTrue

    }


}