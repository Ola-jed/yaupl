package utils

import core.types.classes.YArray
import core.types.classes.YClass
import core.types.classes.YString
import core.types.function.YFunction

object TypeFormatter {
    fun formatToReadable(x: Any): String {
        return when (val str = x::class.simpleName) {
            YArray::class.simpleName -> "Array"
            YString::class.simpleName -> "String"
            YFunction::class.simpleName -> "Function"
            YClass::class.simpleName -> "Class"
            Int::class.simpleName, Double::class.simpleName -> "Number"
            else -> str ?: ""
        }
    }
}