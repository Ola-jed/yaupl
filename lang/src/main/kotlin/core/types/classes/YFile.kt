package core.types.classes

import core.error.types.RuntimeError
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable
import utils.FunctionArities
import java.io.File

class YFile(private val path: String) : YInstance(YClass("File", null, mapOf())) {
    override fun get(name: Token): Any {
        return when (name.lexeme) {
            "create" -> create(name)
            "delete" -> delete(name)
            "exists" -> exists(name)
            "read" -> read(name)
            "readLines" -> readLines(name)
            "write" -> write(name)
            "append" -> append(name)
            "rename" -> rename(name)
            "size" -> size(name)
            "absolutePath" -> absolutePath(name)
            "isDirectory" -> isDirectory(name)
            else -> throw RuntimeError(name, "Undefined File property ${name.lexeme}.")
        }
    }

    override fun set(name: Token, value: Any?) {
        throw RuntimeError(name, "Cannot add properties to file.")
    }

    private val create = fun(name: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.ZERO_PARAMETERS

            override fun call(interpreter: Interpreter, arguments: List<Any?>) {
                try {
                    File(path).createNewFile()
                } catch (e: Exception) {
                    throw RuntimeError(name, "File creation failed : ${e.message ?: ""}.")
                }
            }
        }
    }

    private val delete = fun(name: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.ZERO_PARAMETERS

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Boolean {
                try {
                    val file = File(path)
                    return if (file.isDirectory) file.deleteRecursively() else file.delete()
                } catch (e: Exception) {
                    throw RuntimeError(name, "File deletion failed : ${e.message ?: ""}.")
                }
            }
        }
    }

    private val exists = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.ZERO_PARAMETERS

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Boolean {
                val file = File(path)
                return file.exists()
            }
        }
    }

    private val read = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.ZERO_PARAMETERS

            override fun call(interpreter: Interpreter, arguments: List<Any?>): String {
                val file = File(path)
                return file.readText()
            }
        }
    }

    private val readLines = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.ZERO_PARAMETERS

            override fun call(interpreter: Interpreter, arguments: List<Any?>): YList {
                val file = File(path)
                return YList(file.readLines())
            }
        }
    }

    private val write = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.UNARY

            override fun call(interpreter: Interpreter, arguments: List<Any?>) {
                val file = File(path)
                file.writeText(arguments.first().toString())
            }
        }
    }

    private val append = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.UNARY

            override fun call(interpreter: Interpreter, arguments: List<Any?>) {
                val file = File(path)
                file.appendText(arguments.first().toString())
            }
        }
    }

    private val rename = fun(name: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.UNARY

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Boolean {
                val file = File(path)
                val destination = File(arguments.first().toString())
                if (destination.exists()) {
                    return false
                }

                try {
                    return file.renameTo(destination)
                } catch (e: Exception) {
                    throw RuntimeError(name, "File renaming failed : ${e.message ?: ""}.")
                }
            }
        }
    }

    private val size = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.ZERO_PARAMETERS

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Long {
                val file = File(path)
                return file.length()
            }
        }
    }

    private val absolutePath = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.ZERO_PARAMETERS

            override fun call(interpreter: Interpreter, arguments: List<Any?>): YString {
                return YString(File(path).absolutePath)
            }
        }
    }

    private val isDirectory = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.ZERO_PARAMETERS

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Boolean {
                val file = File(path)
                return file.isDirectory
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is YFile) {
            return false
        }

        return path == other.path
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}