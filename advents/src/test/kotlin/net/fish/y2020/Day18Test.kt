package net.fish.y2020

import net.fish.y2020.ParserType.ADDITION_BEFORE_MULTIPLICATION
import net.fish.y2020.ParserType.EQUAL_PRECEDENCE
import net.fish.y2020.ParserType.STANDARD
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day18Test {

    @Test
    fun `can create and evaluate expressions for type equal precedence`() {
        assertThat(Evaluator(EQUAL_PRECEDENCE).eval("1 + 1")).isEqualTo(2)
        assertThat(Evaluator(EQUAL_PRECEDENCE).eval("3 * 2")).isEqualTo(6)
        assertThat(Evaluator(EQUAL_PRECEDENCE).eval("1 + 2 + 3")).isEqualTo(6)
        assertThat(Evaluator(EQUAL_PRECEDENCE).eval("1 + (2 + 3)")).isEqualTo(6)

        // Evaluation is in order, no precedence of +/*
        assertThat(Evaluator(EQUAL_PRECEDENCE).eval("2 + 3 * 4")).isEqualTo(20)
        assertThat(Evaluator(EQUAL_PRECEDENCE).eval("2 * 3 + 4")).isEqualTo(10)

        // Reddit question
        assertThat(Evaluator(EQUAL_PRECEDENCE).eval("6 * 8 * (2 * 9) + 2 * 8 * 4")).isEqualTo(27712)
        assertThat(Evaluator(ADDITION_BEFORE_MULTIPLICATION).eval("6 * 8 * (2 * 9) + 2 * 8 * 4")).isEqualTo(30720)
    }

    @Test
    fun `can create and evaluate expressions for addition before multiplication`() {
        assertThat(Evaluator(ADDITION_BEFORE_MULTIPLICATION).eval("1 + 1")).isEqualTo(2)
        assertThat(Evaluator(ADDITION_BEFORE_MULTIPLICATION).eval("3 * 2")).isEqualTo(6)
        assertThat(Evaluator(ADDITION_BEFORE_MULTIPLICATION).eval("1 + 2 + 3")).isEqualTo(6)
        assertThat(Evaluator(ADDITION_BEFORE_MULTIPLICATION).eval("1 + (2 + 3)")).isEqualTo(6)

        // Evaluation is in precedence of + over *
        assertThat(Evaluator(ADDITION_BEFORE_MULTIPLICATION).eval("2 + 3 * 4")).isEqualTo(20)
        assertThat(Evaluator(ADDITION_BEFORE_MULTIPLICATION).eval("2 * 3 + 4")).isEqualTo(14)
    }

    @Test
    fun `can create and evaluate expressions for standard maths`() {
        assertThat(Evaluator(STANDARD).eval("1 + 1")).isEqualTo(2)
        assertThat(Evaluator(STANDARD).eval("3 * 2")).isEqualTo(6)
        assertThat(Evaluator(STANDARD).eval("1 + 2 + 3")).isEqualTo(6)
        assertThat(Evaluator(STANDARD).eval("1 + (2 + 3)")).isEqualTo(6)

        // Evaluation is in precedence of + over *
        assertThat(Evaluator(STANDARD).eval("2 + 3 * 4")).isEqualTo(14)
        assertThat(Evaluator(STANDARD).eval("2 * 3 + 4")).isEqualTo(10)
    }

    @Test
    fun `can calculate sum of all values`() {
        assertThat(Day18.calculateSum(listOf("1 + 2 * 3 + 4 * 5 + 6", "1 + (2 * 3) + (4 * (5 + 6))"), EQUAL_PRECEDENCE)).isEqualTo(122)
        assertThat(Day18.calculateSum(listOf("1 + 2 * 3 + 4 * 5 + 6", "1 + (2 * 3) + (4 * (5 + 6))"), ADDITION_BEFORE_MULTIPLICATION)).isEqualTo(282)
        assertThat(Day18.calculateSum(listOf("1 + 2 * 3 + 4 * 5 + 6", "1 + (2 * 3) + (4 * (5 + 6))"), STANDARD)).isEqualTo(84)
    }

}