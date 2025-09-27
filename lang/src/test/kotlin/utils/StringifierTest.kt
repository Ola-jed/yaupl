package utils

import core.types.classes.YArray
import core.types.classes.YList
import core.types.classes.YSet
import core.types.classes.YString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StringifierTest {
    @Test
    fun `stringify should return null string for null input`() {
        assertEquals("null", Stringifier.stringify(null))
    }

    @Test
    fun `stringify should return integer without decimal part for double ending with _0`() {
        assertEquals("5", Stringifier.stringify(5.0))
        assertEquals("123", Stringifier.stringify(123.0))
    }

    @Test
    fun `stringify should return decimal string for non-integer doubles`() {
        assertEquals("5.25", Stringifier.stringify(5.25))
        assertEquals("0.125", Stringifier.stringify(0.125))
    }

    @Test
    fun `stringify should not trim precision beyond formatter pattern`() {
        assertEquals("3.141592653589793", Stringifier.stringify(Math.PI))
        assertEquals("2.718281828459045", Stringifier.stringify(Math.E))
    }

    @Test
    fun `stringify should handle very small doubles`() {
        val tiny = 1e-12
        assertEquals("0.000000000001", Stringifier.stringify(tiny))
    }

    @Test
    fun `stringify should handle very large doubles`() {
        val large = 1e12
        assertEquals("1000000000000", Stringifier.stringify(large))
    }

    @Test
    fun `stringify should return special values correctly`() {
        assertEquals("NaN", Stringifier.stringify(Double.NaN))
        assertEquals("∞", Stringifier.stringify(Double.POSITIVE_INFINITY))
        assertEquals("-∞", Stringifier.stringify(Double.NEGATIVE_INFINITY))
    }

    @Test
    fun `stringify should return toString of non-double types`() {
        assertEquals("hello", Stringifier.stringify("hello"))
        assertEquals("42", Stringifier.stringify(42))
        assertEquals("true", Stringifier.stringify(true))
    }

    @Test
    fun `stringify should return custom object toString`() {
        data class Person(val name: String)
        val person = Person("Alice")
        assertEquals("Person(name=Alice)", Stringifier.stringify(person))
    }

    @Test
    fun `stringify should handle custom yaupl string`() {
        val yStr = YString("hello world")
        assertEquals("hello world", Stringifier.stringify(yStr))
    }

    @Test
    fun `stringify should handle custom yaupl set`() {
        val ySet = YSet()
        assertEquals("Set {}", Stringifier.stringify(ySet))
    }

    @Test
    fun `stringify should handle custom yaupl list`() {
        val yList = YList()
        assertEquals("List []", Stringifier.stringify(yList))
    }

    @Test
    fun `stringify should handle custom yaupl array`() {
        val ySet = YArray(arrayOf(1, 2, 3))
        assertEquals("Array [1, 2, 3]", Stringifier.stringify(ySet))
    }
}
