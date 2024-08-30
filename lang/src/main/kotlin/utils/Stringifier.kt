package utils

import java.text.DecimalFormat

object Stringifier {
    private val decimalFormat = DecimalFormat("#.#######################")

    fun stringify(value: Any?): String {
        if (value == null) {
            return "null"
        }

        if (value is Double) {
            var text = decimalFormat.format(value)

            if (text.endsWith(".0")) {
                text = text.removeSuffix(".0")
            }

            return text
        }

        return value.toString()
    }
}