package net.fish.y2021

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SnailFishProcessorTest {
    @Test
    fun `can parse input strings to snail fish`() {
        assertThat(SnailFishProcessor.convertToSnailFish("[1,2]")).isEqualTo(
            SnailFishPair(left = SnailFishValue(1), right = SnailFishValue(2), depth = 0)
        )

        assertThat(SnailFishProcessor.convertToSnailFish("[[1,2],3]")).isEqualTo(
            SnailFishPair(left = SnailFishPair(left = SnailFishValue(1), right = SnailFishValue(2), depth = 1), right = SnailFishValue(3), depth = 0)
        )

        assertThat(SnailFishProcessor.convertToSnailFish("[9,[8,7]]")).isEqualTo(
            SnailFishPair(left = SnailFishValue(9), right = SnailFishPair(left = SnailFishValue(8), right = SnailFishValue(7), depth = 1), depth = 0)
        )

        assertThat(SnailFishProcessor.convertToSnailFish("[[1,9],[8,5]]")).isEqualTo(
            SnailFishPair(left = SnailFishPair(left = SnailFishValue(1), right = SnailFishValue(9), depth = 1), right = SnailFishPair(left = SnailFishValue(8), right = SnailFishValue(5), depth = 1), depth = 0)
        )

        assertThat(SnailFishProcessor.convertToSnailFish("[[[[1,2],[3,4]],[[5,6],[7,8]]],9]")).isEqualTo(
            SnailFishPair(
                left = SnailFishPair(
                    left = SnailFishPair(
                        left = SnailFishPair(left = SnailFishValue(1), right = SnailFishValue(2), depth = 3),
                        right = SnailFishPair(left = SnailFishValue(3), right = SnailFishValue(4), depth = 3),
                        depth = 2
                    ),
                    right = SnailFishPair(
                        left = SnailFishPair(left = SnailFishValue(5), right = SnailFishValue(6), depth = 3),
                        right = SnailFishPair(left = SnailFishValue(7), right = SnailFishValue(8), depth = 3),
                        depth = 2
                    ),
                    depth = 1
                ),
                right = SnailFishValue(9),
                depth = 0
            )
        )
    }

    @Test
    fun `can find magnitude`() {
        assertThat(SnailFishProcessor.convertToSnailFish("[9,1]").magnitude()).isEqualTo(29)
        assertThat(SnailFishProcessor.convertToSnailFish("[1,9]").magnitude()).isEqualTo(21)
        assertThat(SnailFishProcessor.convertToSnailFish("[[9,1],[1,9]]").magnitude()).isEqualTo(129)
        assertThat(SnailFishProcessor.convertToSnailFish("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]").magnitude()).isEqualTo(3488)
    }

    @Test
    fun `can increment depth`() {
        val pair = SnailFishProcessor.convertToSnailFish("[[1,9],[8,5]]")
        pair.incrementDepth()
        assertThat(pair).isEqualTo(
            SnailFishPair(left = SnailFishPair(left = SnailFishValue(1), right = SnailFishValue(9), depth = 2), right = SnailFishPair(left = SnailFishValue(8), right = SnailFishValue(5), depth = 2), depth = 1)
        )
    }

    @Test
    fun `can add pairs`() {
        val p1 = SnailFishProcessor.convertToSnailFish("[1,2]")
        val p2 = SnailFishProcessor.convertToSnailFish("[[3,4],5]")
        assertThat(SnailFishProcessor.add(p1, p2)).isEqualTo(SnailFishProcessor.convertToSnailFish("[[1,2],[[3,4],5]]"))
    }

    @Test
    fun `parents are set`() {
        val pair = SnailFishProcessor.convertToSnailFish("[[3,4],5]")
        val left = pair.left
        val right = pair.right
        assertThat(pair.parent).isNull()

        assertThat(left.parent).isEqualTo(pair)
        assertThat(right.parent).isEqualTo(pair)

        val three = (left as SnailFishPair).left
        val four = left.right
        assertThat(three.parent).isEqualTo(left)
        assertThat(four.parent).isEqualTo(left)
    }

    @Test
    fun `can find depth 4 pair`() {
        val t1pair = SnailFishProcessor.convertToSnailFish("[[[[[9,8],1],2],3],4]")
        val t1pair98 = SnailFishProcessor.convertToSnailFish("[9,8]")
        assertThat(SnailFishProcessor.findPairToExplode(t1pair)).isEqualTo(t1pair98)

        val t2pair = SnailFishProcessor.convertToSnailFish("[7,[6,[5,[4,[3,2]]]]]")
        val t2pair32 = SnailFishProcessor.convertToSnailFish("[3,2]")
        assertThat(SnailFishProcessor.findPairToExplode(t2pair)).isEqualTo(t2pair32)

        val t3pair = SnailFishProcessor.convertToSnailFish("[[6,[5,[4,[3,2]]]],1]")
        val t3pair32 = SnailFishProcessor.convertToSnailFish("[3,2]")
        assertThat(SnailFishProcessor.findPairToExplode(t3pair)).isEqualTo(t3pair32)

        val t4pair = SnailFishProcessor.convertToSnailFish("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]")
        val t4pair73 = SnailFishProcessor.convertToSnailFish("[7,3]")
        assertThat(SnailFishProcessor.findPairToExplode(t4pair)).isEqualTo(t4pair73)
    }

    @Test
    fun `can explode`() {
        val t1pair = SnailFishProcessor.convertToSnailFish("[[[[[9,8],1],2],3],4]")
        SnailFishProcessor.explode(t1pair)
        assertThat(t1pair).isEqualTo(SnailFishProcessor.convertToSnailFish("[[[[0,9],2],3],4]"))

        val t2pair = SnailFishProcessor.convertToSnailFish("[7,[6,[5,[4,[3,2]]]]]")
        SnailFishProcessor.explode(t2pair)
        assertThat(t2pair).isEqualTo(SnailFishProcessor.convertToSnailFish("[7,[6,[5,[7,0]]]]"))

        val t3pair = SnailFishProcessor.convertToSnailFish("[[6,[5,[4,[3,2]]]],1]")
        SnailFishProcessor.explode(t3pair)
        assertThat(t3pair).isEqualTo(SnailFishProcessor.convertToSnailFish("[[6,[5,[7,0]]],3]"))

        val t4pair = SnailFishProcessor.convertToSnailFish("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]")
        SnailFishProcessor.explode(t4pair)
        assertThat(t4pair).isEqualTo(SnailFishProcessor.convertToSnailFish("[[3,[2,[8,0]]],[9,[5,[7,0]]]]"))

    }

    @Test
    fun `can order nodes by dfs`() {
        val t1pair = SnailFishProcessor.convertToSnailFish("[[1,9],[8,5]]")
        val t1A = SnailFishProcessor.convertToSnailFish("[1,9]")
        val t1B = SnailFishProcessor.convertToSnailFish("[8,5]")
        val t1v1 = SnailFishValue(1)
        val t1v9 = SnailFishValue(9)
        val t1v8 = SnailFishValue(8)
        val t1v5 = SnailFishValue(5)
        val t1dfs = SnailFishProcessor.dfsList(t1pair)
        assertThat(t1dfs).containsExactly(t1pair, t1A, t1v1, t1v9, t1B, t1v8, t1v5)

        // and they all point back up the way
        assertThat(t1dfs[0].parent).isEqualTo(null)   // root

        assertThat(t1dfs[1].parent).isEqualTo(t1pair)   // [1,9]
        assertThat(t1dfs[2].parent).isEqualTo(t1dfs[1]) // 1
        assertThat(t1dfs[3].parent).isEqualTo(t1dfs[1]) // 9

        assertThat(t1dfs[4].parent).isEqualTo(t1pair)   // [8,5]
        assertThat(t1dfs[5].parent).isEqualTo(t1dfs[4])   // 8
        assertThat(t1dfs[6].parent).isEqualTo(t1dfs[4])   // 5
    }

    @Test
    fun `can get ordered list of values case 1`() {
        val t1pair = SnailFishProcessor.convertToSnailFish("[[1,9],[8,5]]")
        val t1v1 = SnailFishValue(1)
        val t1v9 = SnailFishValue(9)
        val t1v8 = SnailFishValue(8)
        val t1v5 = SnailFishValue(5)
        val t1dfs = SnailFishProcessor.dfsValues(t1pair)
        assertThat(t1dfs).containsExactly(t1v1, t1v9, t1v8, t1v5)
        // and they all come back to root node
        assertThat(t1dfs[0].parent!!.parent).isEqualTo(t1pair)
        assertThat(t1dfs[1].parent!!.parent).isEqualTo(t1pair)
        assertThat(t1dfs[2].parent!!.parent).isEqualTo(t1pair)
        assertThat(t1dfs[3].parent!!.parent).isEqualTo(t1pair)
    }

    @Test
    fun `can get ordered list of values case 2`() {
        val t1pair = SnailFishProcessor.convertToSnailFish("[[1,[[2,3],4]],[5,[6,7]]]")
        val t1v1 = SnailFishValue(1)
        val t1v2 = SnailFishValue(2)
        val t1v3 = SnailFishValue(3)
        val t1v4 = SnailFishValue(4)
        val t1v5 = SnailFishValue(5)
        val t1v6 = SnailFishValue(6)
        val t1v7 = SnailFishValue(7)
        val t1dfs = SnailFishProcessor.dfsValues(t1pair)
        assertThat(t1dfs).containsExactly(t1v1, t1v2, t1v3, t1v4, t1v5, t1v6, t1v7)
    }

    @Test
    fun `can split`() {
        val t1pair = SnailFishProcessor.convertToSnailFish("[10,0]")
        SnailFishProcessor.split(t1pair)
        assertThat(t1pair).isEqualTo(SnailFishProcessor.convertToSnailFish("[[5,5],0]"))

        val t2pair = SnailFishProcessor.convertToSnailFish("[11,0]")
        SnailFishProcessor.split(t2pair)
        assertThat(t2pair).isEqualTo(SnailFishProcessor.convertToSnailFish("[[5,6],0]"))
    }

    @Test
    fun `can add values that require processing 1`() {
        val s1 = SnailFishProcessor.convertToSnailFish("[[[[4,3],4],4],[7,[[8,4],9]]]")
        val s2 = SnailFishProcessor.convertToSnailFish("[1,1]")
        val s3 = SnailFishProcessor.add(s1, s2)
        assertThat(s3).isEqualTo(SnailFishProcessor.convertToSnailFish("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"))
    }

    @Test
    fun `can add values that require processing 2`() {
        val s1 = SnailFishProcessor.convertToSnailFish("[[[[1,1],[2,2]],[3,3]],[4,4]]")
        val s2 = SnailFishProcessor.convertToSnailFish("[5,5]")
        val s3 = SnailFishProcessor.add(s1, s2)
        assertThat(s3).isEqualTo(SnailFishProcessor.convertToSnailFish("[[[[3,0],[5,3]],[4,4]],[5,5]]"))
    }

    @Test
    fun `can add values that require processing 3`() {
        val s1 = SnailFishProcessor.convertToSnailFish("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]")
        val s2 = SnailFishProcessor.convertToSnailFish("[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]")
        val s3 = SnailFishProcessor.add(s1, s2)
        assertThat(s3).isEqualTo(SnailFishProcessor.convertToSnailFish("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]"))
    }

    @Test
    fun `can add values that require processing 4`() {
        val s1 = SnailFishProcessor.convertToSnailFish("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]")
        val s2 = SnailFishProcessor.convertToSnailFish("[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]")
        val s3 = SnailFishProcessor.add(s1, s2)
        assertThat(s3).isEqualTo(SnailFishProcessor.convertToSnailFish("[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]"))
    }

    @Test
    fun `can add lists of values`() {
        val result1 = SnailFishProcessor.process(
            listOf(
                "[1,1]",
                "[2,2]",
                "[3,3]",
                "[4,4]"
            )
        )
        assertThat(result1).isEqualTo(SnailFishProcessor.convertToSnailFish("[[[[1,1],[2,2]],[3,3]],[4,4]]"))

        val result2 = SnailFishProcessor.process(
            listOf(
                "[1,1]",
                "[2,2]",
                "[3,3]",
                "[4,4]",
                "[5,5]"
            )
        )
        assertThat(result2).isEqualTo(SnailFishProcessor.convertToSnailFish("[[[[3,0],[5,3]],[4,4]],[5,5]]"))

        val result3 = SnailFishProcessor.process(
            listOf(
                "[1,1]",
                "[2,2]",
                "[3,3]",
                "[4,4]",
                "[5,5]",
                "[6,6]"
            )
        )
        assertThat(result3).isEqualTo(SnailFishProcessor.convertToSnailFish("[[[[5,0],[7,4]],[5,5]],[6,6]]"))
    }

    @Test
    fun `can process longer list`() {
        val result1 = SnailFishProcessor.process(
            listOf(
                "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]",
                "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]",
                "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]",
                "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]",
                "[7,[5,[[3,8],[1,4]]]]",
                "[[2,[2,2]],[8,[8,1]]]",
                "[2,9]",
                "[1,[[[9,3],9],[[9,0],[0,7]]]]",
                "[[[5,[7,4]],7],1]",
                "[[[[4,2],2],6],[8,7]]"
            )
        )
        assertThat(result1).isEqualTo(SnailFishProcessor.convertToSnailFish("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"))
    }

    @Test
    fun `can process test homework`() {
        val result1 = SnailFishProcessor.process(
            listOf(
                "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]",
                "[[[5,[2,8]],4],[5,[[9,9],0]]]",
                "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]",
                "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]",
                "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]",
                "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]",
                "[[[[5,4],[7,7]],8],[[8,3],8]]",
                "[[9,3],[[9,9],[6,[4,9]]]]",
                "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]",
                "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]"
            )
        )
        assertThat(result1).isEqualTo(SnailFishProcessor.convertToSnailFish("[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]"))
        assertThat(result1.magnitude()).isEqualTo(4140)
    }

}