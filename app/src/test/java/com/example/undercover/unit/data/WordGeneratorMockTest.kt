package com.example.undercover.unit.data
import junit.framework.TestCase.assertEquals

import com.example.undercover.data.WordGenerator
import io.mockk.every
import io.mockk.mockk
import org.junit.Test


class WordGeneratorMockTest {
    @Test
    fun testMockedWordGenerator() {
        val wordGenerator = mockk<WordGenerator>()
        every { wordGenerator.generateWords(any()) } returns ("pizza" to "pasta")

        val words = wordGenerator.generateWords(false)

        assertEquals("pizza", words.first)
        assertEquals("pasta", words.second)
    }
}
