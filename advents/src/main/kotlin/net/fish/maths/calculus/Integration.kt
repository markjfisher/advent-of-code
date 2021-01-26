package net.fish.maths.calculus

import net.fish.maths.functions.IFunction
import kotlin.math.abs
import kotlin.math.pow

// taken from https://github.com/bnorm/java-math/tree/master/src/math/calculus
/**
 * A static library for integral calculations of functions.
 *
 * @author Brian Norman
 * @version 0.1 beta
 */
object Integration {
    /**
     * Returns the lower summation of the function over the specified range with the specified number of divisions. Taken
     * from Numerical Mathematics and Computing (6th Edition) by Ward Cheney and David Kincaid, page 185.
     *
     * @param f the function to summate.
     * @param a the starting point of the range.
     * @param b the ending point of the range.
     * @param n the number of divisions.
     * @return the lower sum of the function.
     */
    fun sumLower(f: IFunction, a: Double, b: Double, n: Int): Double {
        val h = (b - a) / n
        var sum = 0.0
        for (i in n downTo 1) {
            sum += f.eval(a + i * h)
        }
        return sum * h
    }

    /**
     * Returns the upper summation of the function over the specified range with the specified number of divisions. Taken
     * from Numerical Mathematics and Computing (6th Edition) by Ward Cheney and David Kincaid, page 185.
     *
     * @param f the function to summate.
     * @param a the starting point of the range.
     * @param b the ending point of the range.
     * @param n the number of divisions.
     * @return the upper sum of the function.
     */
    fun sumUpper(f: IFunction, a: Double, b: Double, n: Int): Double {
        return sumLower(f, a, b, n) + (b - a) * (f.eval(a) - f.eval(b)) / n
    }

    /**
     * Returns the trapezoid summation of the function over the specified range with the specified number of divisions.
     * Taken from Numerical Mathematics and Computing (6th Edition) by Ward Cheney and David Kincaid, page 191.
     *
     * @param f the function to summate.
     * @param a the starting point of the range.
     * @param b the ending point of the range.
     * @param n the number of division.
     * @return the trapezoid sum of the function.
     */
    fun trapizoid(f: IFunction, a: Double, b: Double, n: Int): Double {
        val h = (b - a) / n
        var sum = 1.0 / 2.0 * (f.eval(a) + f.eval(b))
        for (i in 1 until n) {
            sum += f.eval(a + i * h)
        }
        return sum * h
    }

    /**
     * Returns the Romberg extrapolated summation of the function over the specified range with the specified number of
     * divisions. Taken from Numerical Mathematics and Computing (6th Edition) by Ward Cheney and David Kincaid, page
     * 206.
     *
     * @param f the function to summate.
     * @param a the starting point of the range.
     * @param b the ending point of the range.
     * @param n the number of division.
     * @return the Romberg sum of the function.
     */
    fun romberg(f: IFunction, a: Double, b: Double, n: Int): Double {
        val r = DoubleArray(n + 1)
        var h = b - a
        r[0] = h / 2.0 * (f.eval(a) + f.eval(b))
        for (i in 1..n) {
            h /= 2.0
            var sum = 0.0
            var k = 1
            while (k <= Math.pow(2.0, i.toDouble()) - 1) {
                sum += f.eval(a + k * h)
                k += 2
            }
            r[i] = 1.0 / 2.0 * r[i - 1] + sum * h
        }
        for (i in 1..n) {
            for (j in n downTo i) {
                r[j] = r[j] + (r[j] - r[j - 1]) / (4.0.pow(i.toDouble()) - 1.0)
            }
        }
        return r[n]
    }

    /**
     * Returns the Simpson summation of the function over the specified range to the specified precision or max level of
     * division. Taken from Numerical Mathematics and Computing (6th Edition) by Ward Cheney and David Kincaid, page
     * 224.
     *
     * @param f         the function to summate.
     * @param a         the starting point of the range.
     * @param b         the ending point of the range.
     * @param epsilon   the desired precision.
     * @param level_max the max level of division.
     * @return the Simpson sum of the function.
     */
    fun simpson(f: IFunction, a: Double, b: Double, epsilon: Double, level_max: Int): Double {
        return simpson(f, a, b, epsilon, 0, level_max)
    }

    /**
     * Returns the Simpson summation of the function over the specified range to the specified precision or max level of
     * division. This function is designed to be recursive and has a level for each call. Taken from Numerical
     * Mathematics and Computing (6th Edition) by Ward Cheney and David Kincaid, page 224.
     *
     * @param f         the function to summate.
     * @param a         the starting point of the range.
     * @param b         the ending point of the range.
     * @param epsilon   the desired precision.
     * @param level     the current level.
     * @param level_max the max level of division.
     * @return the Simpson sum of the function.
     */
    private fun simpson(f: IFunction, a: Double, b: Double, epsilon: Double, level: Int, level_max: Int): Double {
        val levelp1 = level + 1
        val h = b - a
        val c = (a + b) / 2.0
        val d = (a + c) / 2.0
        val e = (c + b) / 2.0
        val fa = f.eval(a)
        val fb = f.eval(b)
        val fc = f.eval(c)
        val oneSimp = h * (fa + 4.0 * fc + fb) / 6.0
        val twoSimp = h * (fa + 4.0 * f.eval(d) + 2.0 * fc + 4.0 * f.eval(e) + fb) / 12.0
        return when {
            levelp1 >= level_max -> twoSimp
            abs(twoSimp - oneSimp) < 15.0 * epsilon -> twoSimp + (twoSimp - oneSimp) / 15.0
            else -> simpson(f, a, c, epsilon / 2.0, levelp1, level_max) + simpson(f, c, b, epsilon / 2.0, levelp1, level_max)
        }
    }
}
