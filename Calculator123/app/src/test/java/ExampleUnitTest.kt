package com.example.testscalc

import com.nagpal.shivam.expressionparser.Expression
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        val myClass = Expression("2 + 2")
        val expected = "4.0"
        assertEquals(expected, myClass.evaluate().toString())
    }
    @Test
    @Throws(Exception::class)
    fun multi_isCorrect() {
        val myClass = Expression("2 * 2")
        val expected = "4.0"
        assertEquals(expected, myClass.evaluate().toString())
    } @Test
    @Throws(Exception::class)
    fun subtract() {
        val myClass = Expression("2 - 2")
        val expected = "0.0"
        assertEquals(expected, myClass.evaluate().toString())
    } @Test
    @Throws(Exception::class)
    fun div() {
        val myClass = Expression("2 / 2")
        val expected = "1.0"
        assertEquals(expected, myClass.evaluate().toString())
    }
    @Test
    @Throws(Exception::class)
    fun div0() {
        val myClass = Expression("2 / 0")
        val expected = "Infinity"
        assertEquals(expected, myClass.evaluate().toString())
    }
}