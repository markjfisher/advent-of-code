package net.fish.y2020

import net.fish.Day
import net.fish.resourceLines
import net.fish.y2020.ParserType.ADDITION_BEFORE_MULTIPLICATION
import net.fish.y2020.ParserType.EQUAL_PRECEDENCE
import net.fish.y2020.ParserType.STANDARD

object Day18 : Day {
    private val data = resourceLines(2020, 18)

    override fun part1() = calculateSum(data, EQUAL_PRECEDENCE)
    override fun part2() = calculateSum(data, ADDITION_BEFORE_MULTIPLICATION)

    fun calculateSum(data: List<String>, parserType: ParserType): Long {
        val evaluator = Evaluator(parserType)
        return data.fold(0L) { acc, line -> acc + evaluator.eval(line) }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

class Evaluator(private val parserType: ParserType) {
    fun eval(expression: String): Long {
        return eval(parse(scan(expression)))
    }

    private fun parse(tokens: List<Token>): Expr {
        val parser = when(parserType) {
            EQUAL_PRECEDENCE -> EqualPrecedenceParser(tokens)
            ADDITION_BEFORE_MULTIPLICATION -> AdditionBeforeMultiplicationParser(tokens)
            STANDARD -> StandardPrecedenceParser(tokens)
        }
        return parser.parse()
    }

    private fun scan(expression: String): List<Token> = Scanner(expression).scanTokens()
    private fun eval(expr: Expr): Long = expr.accept(this)

    fun visitBinaryExpr(expr: BinaryExpr): Long {
        val left = eval(expr.left)
        val right = eval(expr.right)

        return when (expr.operator.type) {
            TokenType.PLUS -> left + right
            TokenType.STAR -> left * right
            else -> throw Exception("Invalid binary operator '${expr.operator.lexeme}'")
        }
    }

    fun visitLiteralExpr(expr: LiteralExpr): Long = expr.value
    fun visitGroupingExpr(expr: GroupingExpr): Long = eval(expr.expression)
}

data class Scanner(
    val source: String
) {
    private val tokens: MutableList<Token> = mutableListOf()
    private var start = 0
    private var current = 0

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null))
        return tokens
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun scanToken() {
        start = current

        when (val c = advance()) {
            ' ' -> {
                // Ignore whitespace.
            }
            '+' -> addToken(TokenType.PLUS)
            '*' -> addToken(TokenType.STAR)
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            else -> {
                when {
                    c.isDigit() -> number()
                    else -> invalidToken(c)
                }
            }
        }
    }

    private fun number() {
        while (peek().isDigit()) advance()

        val value = source
            .substring(start, current)
            .toLong()

        addToken(TokenType.NUMBER, value)
    }

    private fun advance() = source[current++]
    private fun peek(): Char = if (isAtEnd()) '\u0000' else source[current]
    private fun addToken(type: TokenType) = addToken(type, null)

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal))
    }

    private fun Char.isDigit() = this in '0'..'9'
    private fun invalidToken(c: Char) {
        throw Exception("Invalid token '$c'")
    }
}

data class Token(
    val type: TokenType,
    val lexeme: String?, // what we parsed it as if anything
    val literal: Any?    // the string thing it came from
)

enum class TokenType {
    PLUS,
    STAR,

    LEFT_PAREN,
    RIGHT_PAREN,

    NUMBER,

    EOF;
}

abstract class Parser(private val tokens: List<Token>) {
    private var current = 0

    // different implementations allow alternate precedence. whichever is called first is lowest priority.
    abstract fun expression(): Expr

    fun parse(): Expr {
        val expr = expression()
        if (!isAtEnd()) {
            throw Exception("Expected end of expression, found '${peek().lexeme}'")
        }
        return expr
    }

    fun equalPrecedence(): Expr {
        var left = primary()

        while (match(TokenType.STAR, TokenType.PLUS)) {
            val operator = previous()
            val right = primary()

            left = BinaryExpr(left, operator, right)
        }

        return left
    }

    fun multiplicationAfterAddition(): Expr {
        // We are a multiplication expression, but make sure addition can happen in our LEFT/RIGHT parts first
        var left = additionBeforeMultiplication()

        while (match(TokenType.STAR)) {
            val operator = previous()
            val right = additionBeforeMultiplication()

            left = BinaryExpr(left, operator, right)
        }

        return left
    }

    private fun additionBeforeMultiplication(): Expr {
        // We are an addition expression, only thing that can be LEFT/RIGHT of us is a "start" expression (number or bracket)
        var left = primary()

        while (match(TokenType.PLUS)) {
            val operator = previous()
            val right = primary()

            left = BinaryExpr(left, operator, right)
        }

        return left
    }

    private fun multiplication(): Expr {
        // Standard rules, * is higher than +, so only thing that is LEFT/RIGHT is a start expr
        var left = primary()

        while (match(TokenType.STAR)) {
            val operator = previous()
            val right = primary()

            left = BinaryExpr(left, operator, right)
        }

        return left
    }

    fun addition(): Expr {
        // We're adding, but allow * to happen before us
        var left = multiplication()

        while (match(TokenType.PLUS)) {
            val operator = previous()
            val right = multiplication()

            left = BinaryExpr(left, operator, right)
        }

        return left
    }

    // Either a number or an open bracket start us off
    private fun primary(): Expr {
        if (match(TokenType.NUMBER)) return LiteralExpr(previous().literal as Long)

        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            if (check(TokenType.RIGHT_PAREN)) advance() else throw Exception("Expected ')' after '${previous().lexeme}'.")
            return GroupingExpr(expr)
        }

        throw Exception("Expected expression after '${previous().lexeme}'.")
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(tokenType: TokenType): Boolean {
        return if (isAtEnd()) false else peek().type === tokenType
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd() = peek().type == TokenType.EOF
    private fun peek() = tokens[current]
    private fun previous() = tokens[current - 1]

}

enum class ParserType {
    STANDARD, EQUAL_PRECEDENCE, ADDITION_BEFORE_MULTIPLICATION
}

class StandardPrecedenceParser(tokens: List<Token>): Parser(tokens) {
    override fun expression(): Expr {
        return addition()
    }
}

class EqualPrecedenceParser(tokens: List<Token>): Parser(tokens) {
    override fun expression(): Expr {
        return equalPrecedence()
    }
}

class AdditionBeforeMultiplicationParser(tokens: List<Token>): Parser(tokens) {
    override fun expression(): Expr {
        return multiplicationAfterAddition()
    }
}

sealed class Expr {
    abstract fun accept(visitor: Evaluator): Long
}

data class BinaryExpr(
    val left: Expr,
    val operator: Token,
    val right: Expr
) : Expr() {
    override fun accept(visitor: Evaluator) = visitor.visitBinaryExpr(this)
}

data class LiteralExpr(val value: Long) : Expr() {
    override fun accept(visitor: Evaluator) = visitor.visitLiteralExpr(this)
}

data class GroupingExpr(val expression: Expr) : Expr() {
    override fun accept(visitor: Evaluator) = visitor.visitGroupingExpr(this)
}

/*
As you look out the window and notice a heavily-forested continent slowly appear over the horizon, you are
interrupted by the child sitting next to you. They're curious if you could help them with their math homework.

Unfortunately, it seems like this "math" follows different rules than you remember.

The homework (your puzzle input) consists of a series of expressions that consist of addition (+),
multiplication (*), and parentheses ((...)). Just like normal math, parentheses indicate that the expression
inside must be evaluated before it can be used by the surrounding expression. Addition still finds the sum
of the numbers on both sides of the operator, and multiplication still finds the product.

However, the rules of operator precedence have changed. Rather than evaluating multiplication before addition,
the operators have the same precedence, and are evaluated left-to-right regardless of the order in which they
appear.

For example, the steps to evaluate the expression 1 + 2 * 3 + 4 * 5 + 6 are as follows:

1 + 2 * 3 + 4 * 5 + 6
  3   * 3 + 4 * 5 + 6
      9   + 4 * 5 + 6
         13   * 5 + 6
             65   + 6
                 71

Parentheses can override this order; for example, here is what happens if parentheses are added
to form 1 + (2 * 3) + (4 * (5 + 6)):

1 + (2 * 3) + (4 * (5 + 6))
1 +    6    + (4 * (5 + 6))
     7      + (4 * (5 + 6))
     7      + (4 *   11   )
     7      +     44
            51

Here are a few more examples:

2 * 3 + (4 * 5) becomes 26.
5 + (8 * 3 + 9 + 3 * 4 * 3) becomes 437.
5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4)) becomes 12240.
((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2 becomes 13632.

Before you can help with the homework, you need to understand it yourself.
Evaluate the expression on each line of the homework; what is the sum of the
resulting values?

 */

/*
You manage to answer the child's questions and they finish part 1 of their homework,
but get stuck when they reach the next section: advanced math.

Now, addition and multiplication have different precedence levels, but they're not
the ones you're familiar with. Instead, addition is evaluated before multiplication.

For example, the steps to evaluate the expression 1 + 2 * 3 + 4 * 5 + 6 are now as follows:

1 + 2 * 3 + 4 * 5 + 6
  3   * 3 + 4 * 5 + 6
  3   *   7   * 5 + 6
  3   *   7   *  11
     21       *  11
         231

Here are the other examples from above:

1 + (2 * 3) + (4 * (5 + 6)) still becomes 51.
2 * 3 + (4 * 5) becomes 46.
5 + (8 * 3 + 9 + 3 * 4 * 3) becomes 1445.
5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4)) becomes 669060.
((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2 becomes 23340.

What do you get if you add up the results of evaluating the homework problems using these new rules?

 */