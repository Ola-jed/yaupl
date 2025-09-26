package utils

import core.types.classes.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class TypeDeducerTest {
    companion object {
        @JvmStatic
        fun typeCases(): Stream<Arguments> = Stream.of(
            Arguments.of(42, "Number"),
            Arguments.of(3.14, "Number"),
            Arguments.of("hello", "String"),
            Arguments.of(YArray(3), "Array"),
            Arguments.of(YList(), "List"),
            Arguments.of(YSet(), "Set"),
            Arguments.of(YFile("test.txt"), "File"),
            Arguments.of(YString("abc"), "String"),
            Arguments.of(YClass("MyClass", null, mapOf()), "Class"),
            Arguments.of(object {}, "")
        )
    }

    @ParameterizedTest
    @MethodSource("typeCases")
    fun `infer type names correctly`(input: Any, expected: String) {
        assertEquals(expected, TypeDeducer.inferTypeName(input))
    }
}
