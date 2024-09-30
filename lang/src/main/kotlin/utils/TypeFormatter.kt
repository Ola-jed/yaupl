package utils

import core.types.classes.*
import core.types.function.YFunction

object TypeFormatter {
    fun formatToReadable(x: Any): String {
        return when (val str = x::class.simpleName) {
            YArray::class.simpleName -> "Array"
            YList::class.simpleName -> "List"
            YSet::class.simpleName -> "Set"
            YFile::class.simpleName -> "File"
            YString::class.simpleName -> "String"
            YFunction::class.simpleName -> "Function"
            YClass::class.simpleName -> "Class"
            Int::class.simpleName, Double::class.simpleName -> "Number"
            else -> str ?: ""
        }
    }
}