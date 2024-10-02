import java.io.PrintWriter

plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.ola"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    withSourcesJar()
}

tasks.register("astGen") {
    group = "com.ola"
    description = "Generates AST code in the current module"

    doLast {
        val defineType = fun(writer: PrintWriter, className: String, baseName: String, fieldList: String?) {
            if (fieldList != null) {
                writer.println("    class $className(")
                val fields = fieldList.split(", ")
                for (field in fields) {
                    val fieldData = field.split(" ")
                    writer.println("        val ${fieldData[1]}: ${fieldData[0]},")
                }
                writer.println("    ) : $baseName() {")
            } else {
                writer.println("    class $className (): $baseName() {")
            }

            writer.println("        override fun <R> accept(visitor: Visitor<R>) = visitor.visit$className$baseName(this)")
            writer.println("    }\n")
        }

        val defineVisitor = fun(writer: PrintWriter, baseName: String, types: List<String>) {
            writer.println("    interface Visitor<R> {")

            for (type in types) {
                val typeName = type.split(":")[0].trim()
                writer.println("        fun visit$typeName$baseName(${baseName.lowercase()}: $typeName): R")
            }

            writer.println("    }")
            writer.println()
        }

        val defineAst = fun(outputDir: String, baseName: String, types: List<String>) {
            val path = "$outputDir/$baseName.kt"
            val file = File(path)
            file.parentFile.mkdirs()

            if (!file.exists()) {
                file.createNewFile()
            }

            PrintWriter(path, "UTF-8").use { writer ->
                writer.println("package ast\n")
                writer.println("import core.scanner.Token\n")
                writer.println("sealed class $baseName {")
                defineVisitor(writer, baseName, types)
                writer.println("    abstract fun <R> accept(visitor: Visitor<R>): R\n")

                for (type in types) {
                    val components = type.split(":")
                    val className = components[0].trim()
                    val fieldList = components.getOrNull(1)?.trim()
                    defineType(writer, className, baseName, fieldList)
                }

                writer.println("}")
            }
        }


        val outputDir = layout.projectDirectory.dir("src/main/kotlin/ast").toString()

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
                "Class : Token name, Expr.Variable? superclass, List<Stmt.Function> methods, List<Stmt.Function> staticMethods",
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
                "Import : Token keyword, String path"
            )
        )
    }
}