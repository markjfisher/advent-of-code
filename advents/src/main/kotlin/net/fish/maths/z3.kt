package net.fish.maths

import com.microsoft.z3.ArithSort
import com.microsoft.z3.BoolSort
import com.microsoft.z3.Context
import com.microsoft.z3.Expr
import com.microsoft.z3.IntExpr
import com.microsoft.z3.Model
import com.microsoft.z3.Status

interface Z3Expr {
    val ctx: Context
    val expr: Expr<out ArithSort>
}

class Z3Int(
    override val ctx: Context,
    override val expr: IntExpr,
) : Z3Expr

class Z3ArithExpr(
    override val ctx: Context,
    override val expr: Expr<out ArithSort>
) : Z3Expr

class Z3BoolExpr(
    val ctx: Context,
    val expr: Expr<BoolSort>
)

operator fun Z3Expr.minus(other: Long): Z3Expr {
    return Z3ArithExpr(ctx, ctx.mkSub(expr, ctx.mkInt(other)))
}

operator fun Long.minus(other: Z3Expr): Z3Expr {
    return Z3ArithExpr(other.ctx, other.ctx.mkSub(other.ctx.mkInt(this), other.expr))
}

operator fun Z3Expr.minus(other: Z3Expr): Z3Expr {
    return Z3ArithExpr(other.ctx, other.ctx.mkSub(expr, other.expr))
}

operator fun Z3Expr.plus(other: Long): Z3Expr {
    return Z3ArithExpr(ctx, ctx.mkAdd(expr, ctx.mkInt(other)))
}

operator fun Long.plus(other: Z3Expr): Z3Expr {
    return Z3ArithExpr(other.ctx, other.ctx.mkAdd(other.ctx.mkInt(this), other.expr))
}

operator fun Z3Expr.plus(other: Z3Expr): Z3Expr {
    return Z3ArithExpr(ctx, ctx.mkAdd(expr, other.expr))
}

operator fun Z3Expr.times(other: Long): Z3Expr {
    return Z3ArithExpr(ctx, ctx.mkMul(expr, ctx.mkInt(other)))
}

operator fun Long.times(other: Z3Expr): Z3Expr {
    return Z3ArithExpr(other.ctx, other.ctx.mkMul(other.ctx.mkInt(this), other.expr))
}

operator fun Z3Expr.times(other: Z3Expr): Z3Expr {
    return Z3ArithExpr(ctx, ctx.mkMul(expr, other.expr))
}

operator fun Z3Expr.div(other: Long): Z3Expr {
    return Z3ArithExpr(ctx, ctx.mkDiv(expr, ctx.mkInt(other)))
}

operator fun Long.div(other: Z3Expr): Z3Expr {
    return Z3ArithExpr(other.ctx, other.ctx.mkDiv(other.ctx.mkInt(this), other.expr))
}

operator fun Z3Expr.div(other: Z3Expr): Z3Expr {
    return Z3ArithExpr(other.ctx, other.ctx.mkDiv(expr, other.expr))
}

infix fun Z3Expr.eq(other: Z3Expr): Z3BoolExpr {
    return Z3BoolExpr(ctx, ctx.mkEq(this.expr, other.expr))
}

class Z3Context(private val ctx: Context) {
    lateinit var model: Model

    fun int(name: String) = Z3Int(ctx, ctx.mkIntConst(name))

    fun solve(equations: List<Z3BoolExpr>) {
        val solver = ctx.mkSolver()
        solver.add(*equations.map { it.expr }.toTypedArray())

        require(solver.check() == Status.SATISFIABLE) {
            "Equation set not satisfiable"
        }

        this.model = solver.model
    }

    fun eval(expr: Z3Expr): String {
        return model.eval(expr.expr, true).toString()
    }
}

fun <R> z3(fn: Z3Context.() -> R): R {
    return Context().use {
        Z3Context(it).fn()
    }
}