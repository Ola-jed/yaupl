import core.enum.TokenType
import core.error.types.RuntimeError
import core.`object`.Undefined
import core.runtime.Environment
import core.scanner.Token
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

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

    // TODO : assign variable, assign constant, undef variable, nested environments
}