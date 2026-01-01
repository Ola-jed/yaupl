package core

class CompilationError(override val message: String) : RuntimeException(message)
