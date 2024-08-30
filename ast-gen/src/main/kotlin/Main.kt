import GenerateAst.defineAst

// Tool to generate the needed classes for the ast
// To add a new expression or statement type, just add it to the list and run (change the output dir if needed)
fun main() {
    val outputDir = "../lang/src/main/kotlin/ast"

    defineAst(
        outputDir, "Expr", listOf(
            "Assign : Token name, Expr value",
            "Binary : Expr left, Token operator, Expr right",
            "Call : Expr callee, Token paren, List<Expr> arguments",
            "Get : Expr obj, Token name",
            "Grouping : Expr expression",
            "Literal : Any? value",
            "StringLiteral : String value",
            "Logical : Expr left, Token operator, Expr right",
            "Set : Expr obj, Token name, Expr value",
            "Super : Token keyword, Token method",
            "This : Token keyword",
            "Unary : Token operator, Expr right",
            "Variable : Token name",
            "ArrayLiteral : List<Expr?> elements"
        )
    )

    defineAst(
        outputDir, "Stmt", listOf(
            "Block : List<Stmt> statements",
            "Class : Token name, Expr.Variable? superclass, List<Stmt.Function> methods",
            "Expression : Expr expression",
            "Function : Token name, List<Token> params, List<Stmt> body",
            "If : Expr condition, Stmt thenBranch, Stmt? elseBranch",
            "Print : Expr expression",
            "Return : Token keyword, Expr? value",
            "VariableDeclaration : Token name, Expr initializer",
            "ConstantDeclaration : Token name, Expr initializer",
            "While : Expr condition, Stmt body",
            "DoWhile : Expr condition, Stmt body",
            "Break: Token item",
            "Continue: Token item",
        )
    )
}

