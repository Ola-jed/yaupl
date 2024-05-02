package ast

class AstPrinter : Expr.Visitor<String> {
    fun print(expr: Expr): String {
        return expr.accept(this)
    }

    override fun visitBinaryExpr(expr: Expr.Binary): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): String {
        return parenthesize("group", expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): String {
        return if (expr.value == null) "null" else expr.value.toString()
    }

    override fun visitUnaryExpr(expr: Expr.Unary): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    private fun parenthesize(name: String, vararg expressions: Expr): String {
        val sb = StringBuilder()
        sb.append("($name")

        for (expression in expressions) {
            sb.append(" ${expression.accept(this)}")
        }

        sb.append(')')
        return sb.toString()
    }

}