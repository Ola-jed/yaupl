package utils

// Util object to store common number of arguments of functions
object FunctionArities {
    val MAX_VARIADIC_PARAMETERS = (0..255).toSet()
    val ZERO_PARAMETERS = setOf(0)
    val NULLABLE = setOf(0, 1)
    val UNARY = setOf(1)
    val BINARY = setOf(2)
}