package net.fish.maths.functions

/**
 * Defines a mathematical relationship that represents a function.  Used to describe a relationship between an input
 * space and an output space. For the case of this representation the space is one dimensional.
 *
 * @author Brian Norman
 * @version 0.1 beta
 */
fun interface IFunction {
    /**
     * Defines a mathematical relationship between an input and an output value.
     *
     * @param x input value.
     * @return output value.
     */
    fun eval(x: Double): Double

    /**
     * Returns a new function that is a copy of this class and has the specified value added to its output.
     *
     * @param n the value by which the output is shifted.
     * @return a new shifted function.
     */
    fun add(n: Double): IFunction {
        return IFunction { x: Double -> eval(x) + n }
    }

    /**
     * Returns a new function that is a copy of this class and has the specified function at the same input added to its
     * output.
     *
     * @param f the function by which this class is shifted.
     * @return a new shifted function.
     */
    fun add(f: IFunction): IFunction {
        return IFunction { x: Double -> eval(x) + f.eval(x) }
    }

    /**
     * Returns a new function that is a copy of this class and has the specified value subtracted from its output.
     *
     * @param n the value by which the output is shifted.
     * @return a new shifted function.
     */
    fun subtract(n: Double): IFunction {
        return add(-n)
    }

    /**
     * Returns a new function that is a copy of this class and has the specified function at the same input subtracted
     * from its output.
     *
     * @param f the function by which this class is shifted.
     * @return a new shifted function.
     */
    fun subtract(f: IFunction): IFunction {
        return IFunction { x: Double -> eval(x) - f.eval(x) }
    }

    /**
     * Returns a new function that is a copy of this class and has its output multiplied by the specified value.
     *
     * @param n the value by which the output is scaled.
     * @return a new scaled function.
     */
    fun multiply(n: Double): IFunction {
        return IFunction { x: Double -> eval(x) * n }
    }

    /**
     * Returns a new function that is a copy of this class and has its output multiplied by the specified function at
     * the same input.
     *
     * @param f the function by which the output is scaled.
     * @return a new scaled function.
     */
    fun multiply(f: IFunction): IFunction {
        return IFunction { x: Double -> eval(x) * f.eval(x) }
    }

    /**
     * Returns a new function that is a copy of this class and has its output divided by the specified value.
     *
     * @param n the value by which the output is scaled.
     * @return a new scaled function.
     */
    fun divide(n: Double): IFunction {
        return multiply(1.0 / n)
    }

    /**
     * Returns a new function that is a copy of this class and has its output divided by the specified function at the
     * same input.
     *
     * @param f the function by which the output is scaled.
     * @return a new scaled function.
     */
    fun divide(f: IFunction): IFunction {
        return IFunction { x: Double -> eval(x) / f.eval(x) }
    }

    /**
     * Returns a new function that is a copy of this class and has its input first evaluated by the specified function
     * and uses that as input.
     *
     * @param f the function that evaluates the input first.
     * @return a new composite function.
     */
    fun composite(f: IFunction): IFunction {
        return IFunction { x: Double -> eval(f.eval(x)) }
    }
}