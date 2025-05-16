package com.example.undercover.unit.data

import com.example.undercover.data.Player
import com.example.undercover.data.WordGenerator
import com.example.undercover.ui.assignRoles
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Test

class AssignRolesMockTest {
    @Test
    fun assignRoles_assignsCorrectWordsToRoles() {
        val mockWordGenerator = mockk<WordGenerator>()
        every { mockWordGenerator.generateWords(any()) } returns ("soare" to "luna")

        val players = listOf(
            Player("Andrei", "", ""),
            Player("Maria", "", ""),
            Player("Vlad", "", ""),
            Player("Elena", "", ""),
        )

        val assigned = assignRoles(
            players = players,
            wordGenerator = mockWordGenerator,
            is18Plus = false,
            numUndercover = 1,
            numMrWhite = 1
        )

        val undercover = assigned.find { it.role == "Undercover" }
        val mrWhite = assigned.find { it.role == "Mr. White" }
        val civilians = assigned.filter { it.role == "Civil" }

        assertEquals("luna", undercover?.word)
        assertEquals("Tu esti Mr. White", mrWhite?.word)
        civilians.forEach { civil ->
            assertEquals("soare", civil.word)
        }
    }
}
