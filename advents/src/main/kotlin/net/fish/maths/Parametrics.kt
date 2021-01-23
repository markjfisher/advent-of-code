package net.fish.maths

import net.fish.maths.calculus.Differentiation
import net.fish.maths.functions.IFunction

data class Parametrics(
    val fx: IFunction,
    val fy: IFunction,
    val fz: IFunction,
    val tx: IFunction = Differentiation.derivative(fx),
    val ty: IFunction = Differentiation.derivative(fy),
    val tz: IFunction = Differentiation.derivative(fz),
    val nx: IFunction = Differentiation.secondDerivative(fx),
    val ny: IFunction = Differentiation.secondDerivative(fy),
    val nz: IFunction = Differentiation.secondDerivative(fz),
)
