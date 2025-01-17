import core.enum.TokenType
import core.error.types.RuntimeError
import core.`object`.Undefined
import core.runtime.Environment
import core.scanner.Token
import core.types.classes.YArray
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EnvironmentTest {
    @Test
    fun getVariableDefinedWithValue() {
        val env = Environment()
        val token = Token(TokenType.IDENTIFIER, "foo")
        env.define(token, 2)
        assertEquals(2, env.get(token))
    }

    @Test
    fun getConstantDefinedWithValue() {
        val env = Environment()
        val token = Token(TokenType.IDENTIFIER, "foo")
        env.define(token, 2, constant = true)
        assertEquals(2, env.get(token))
    }

    @Test
    fun getVariableDefinedWithoutValue() {
        val env = Environment()
        val token = Token(TokenType.IDENTIFIER, "foo")
        env.define(token, Undefined)
        assertFailsWith<RuntimeError>(block = { env.get(token) })
    }

    @Test
    fun getVariableNotDefined() {
        val env = Environment()
        val token = Token(TokenType.IDENTIFIER, "foo")
        assertFailsWith<RuntimeError>(block = { env.get(token) })
    }

    @Test
    fun assignVariableAndGetCorrectValue() {
        val env = Environment()
        val token = Token(TokenType.IDENTIFIER, "foo")
        env.define(token, 2)
        assertEquals(2, env.get(token))
        env.assign(token, 0)
        assertEquals(0, env.get(token))
    }

    @Test
    fun assignConstantThrows() {
        val env = Environment()
        val token = Token(TokenType.IDENTIFIER, "foo")
        env.define(token, 2, constant = true)
        assertEquals(2, env.get(token))
        assertFailsWith<RuntimeError>(block = { env.assign(token, 0) })
    }

    @Test
    fun assignWithValueOfIncompatibleType() {
        val env = Environment()
        val token = Token(TokenType.IDENTIFIER, "foo")
        val array = YArray(arrayOf(0))
        env.define(token, 2)
        assertEquals(2, env.get(token))
        assertFailsWith<RuntimeError>(block = { env.assign(token, array) })
    }

    @Test
    fun undefVariable() {
        val env = Environment()
        val token = Token(TokenType.IDENTIFIER, "foo")
        env.define(token, 2)
        assertEquals(2, env.get(token))
        env.undef(token)
        assertFailsWith<RuntimeError>(block = { env.get(token) })
    }

    @Test
    fun getVariableThroughNestedEnvironments() {
        val env = Environment()
        val token = Token(TokenType.IDENTIFIER, "foo")
        val outer = Environment(Environment(env))
        env.define(token, 2)
        assertEquals(2, outer.get(token))
    }

    @Test
    fun assignVariableThroughNestedEnvironments() {
        val env = Environment()
        val token = Token(TokenType.IDENTIFIER, "foo")
        val outer = Environment(Environment(env))
        env.define(token, 2)
        assertEquals(2, outer.get(token))
        outer.assign(token, 0)
        assertEquals(0, outer.get(token))
    }
}