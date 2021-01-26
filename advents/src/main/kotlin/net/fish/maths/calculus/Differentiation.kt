package net.fish.maths.calculus
import net.fish.maths.functions.IFunction
import kotlin.math.abs
import kotlin.math.pow

// Taken from https://github.com/bnorm/java-math/tree/master/src/math/calculus

/**
 * A static library for derivative calculations of functions.
 *
 * @author Brian Norman
 * @version 0.1 beta
 */
object Differentiation {
    /**
     * Returns a small value for h that is used for computing the derivative.
     *
     * @param x the point at which the h value will be used.
     * @return a small value for h.
     */
    fun hValue(x: Double): Double {
        return abs(x / 1000.0).coerceAtLeast(0.0001)
    }

    /**
     * Returns the derivative of the specified function at the specified point. Taken from Numerical Mathematics and
     * Computing (6th Edition) by Ward Cheney and David Kincaid, pages 172-173.
     *
     * @param f the function to derive.
     * @param x the point of derivation.
     * @return the derivative of the function.
     */
    fun derivative(f: IFunction, x: Double): Double {
        val h = hValue(x)
        val xph = f.eval(x + h)
        val xmh = f.eval(x - h)
        return 1.0 / (2.0 * h) * (xph - xmh) -
                1.0 / (12.0 * h) * (f.eval(x + 2.0 * h) - 2.0 * xph + 2.0 * xmh - f.eval(x - 2.0 * h))
    }

    /**
     * Returns the functional representation of the derivative of the specified function. Every time the returned
     * function is evaluated at a point the derivative of the specified function is calculated.
     *
     * @param f the function to derive.
     * @return the derivative of the function.
     */
    fun derivative(f: IFunction): IFunction {
        return IFunction { x: Double -> derivative(f, x) }
    }

    /**
     * Returns the functional representation of the Richardson Extrapolation of the derivative of the specified function.
     * Every time the returned function is evaluated at a point the derivative of the specified function is calculated.
     * The default number of iterations for the Richardson Extrapolation is 10.
     *
     * @param f the function to derive.
     * @return the derivative of the function.
     */
    fun extrapDerivative(f: IFunction): IFunction {
        return IFunction { x: Double -> extrapDerivative(f, x) }
    }
    /**
     * Returns the Richardson Extrapolation of the derivative of the specified function at the specified point. Computes
     * the specified number of iterations for the Richardson Extrapolation. Taken from Numerical Mathematics and
     * Computing (6th Edition) by Ward Cheney and David Kincaid, page 170.
     *
     * @param f the function to derive.
     * @param x the point of derivation.
     * @param n the number of Richardson Extrapolation iterations.
     * @return the derivative of the specified function.
     */
    @JvmOverloads
    fun extrapDerivative(f: IFunction, x: Double, n: Int = 10): Double {
        val d = DoubleArray(n + 1)
        var h = hValue(x)
        for (i in 0..n) {
            d[i] = (f.eval(x + h) - f.eval(x - h)) / (2.0 * h)
            h /= 2.0
        }
        for (i in 1..n) {
            for (j in n downTo i) {
                d[j] = d[j] + (d[j] - d[j - 1]) / (4.0.pow(i.toDouble()) - 1.0)
            }
        }
        return d[n]
    }

    /**
     * Returns the functional representation of the Richardson Extrapolation of the derivative of the specified function.
     * Every time the returned function is evaluated at a point the derivative of the specified function is calculated.
     *
     * @param f the function to derive.
     * @return the derivative of the function.
     */
    fun extrapDerivative(f: IFunction, n: Int): IFunction {
        return IFunction { x: Double -> extrapDerivative(f, x, n) }
    }

    /**
     * Returns the second derivative of the specified function at the specified point. Taken from Numerical Mathematics
     * and Computing (6th Edition) by Ward Cheney and David Kincaid, page 173.
     *
     * @param f the function to derive.
     * @param x the point of derivation.
     * @return the second derivative of the function.
     */
    fun secondDerivative(f: IFunction, x: Double): Double {
        val h = hValue(x)
        return 1.0 / (h * h) * (f.eval(x + h) - 2.0 * f.eval(x) + f.eval(x - h))
    }

    /**
     * Returns the functional representation of the second derivative of the specified function. Every time the returned
     * function is evaluated at a point the derivative of the specified function is calculated.
     *
     * @param f the function to derive.
     * @return the second derivative of the function.
     */
    fun secondDerivative(f: IFunction): IFunction {
        return IFunction { x: Double -> secondDerivative(f, x) }
    }
}