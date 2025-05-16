package com.example.undercover.integration

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.undercover.data.Player
import com.example.undercover.data.WordGenerator
import com.example.undercover.ui.RoleConfigurationScreen
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameSetupIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val wordGenerator = WordGenerator(context)

    @Test
    fun testWordGenerationAndRoleAssignment() {
        // Setup players with initial roles
        val players = List(6) { index ->
            when (index) {
                0, 1 -> Player("Player$index", "Undercover", "")
                2 -> Player("Player$index", "Mr. White", "")
                else -> Player("Player$index", "Civil", "")
            }
        }
        
        var configuredPlayers: List<Player>? = null
        var numUndercover = 0
        var numMrWhite = 0

        // Configure roles
        composeTestRule.setContent {
            RoleConfigurationScreen(
                players = players,
                is18Plus = false
            ) { updatedPlayers, undercover, mrWhite ->
                configuredPlayers = updatedPlayers
                numUndercover = undercover
                numMrWhite = mrWhite
            }
        }

        // Wait for UI to be ready and verify initial state
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("Configurare Roluri").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Verify initial role counts
        composeTestRule.onNodeWithText("Undercover: 2").assertExists()
        composeTestRule.onNodeWithText("Mr. White: 1").assertExists()

        // Set roles (2 undercover, 1 mr white)
        composeTestRule.onAllNodesWithText("+")[0].performClick() // Increase undercover to 2
        
        // Wait for the confirm button to be visible
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("Confirmă Rolurile").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        composeTestRule.onNodeWithText("Confirmă Rolurile").performClick()

        // Wait for configuration to complete
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            configuredPlayers != null
        }

        // Verify role configuration
        assertNotNull(configuredPlayers)
        assertEquals(6, configuredPlayers?.size)
        assertEquals(2, numUndercover)
        assertEquals(1, numMrWhite)

        // Generate words for the game
        val (civilianWord, undercoverWord) = wordGenerator.generateWords(false)

        // Verify word generation
        assertNotNull(civilianWord)
        assertNotNull(undercoverWord)
        assertNotEquals(civilianWord, undercoverWord)

        // Assign words to players
        val finalPlayers = configuredPlayers?.map { player ->
            when (player.role) {
                "Civil" -> player.copy(word = civilianWord)
                "Undercover" -> player.copy(word = undercoverWord)
                else -> player // Mr. White stays with empty word
            }
        }

        // Verify final player setup
        assertNotNull(finalPlayers)
        assertEquals(6, finalPlayers?.size)
        
        // Count roles
        val civilianCount = finalPlayers?.count { it.role == "Civil" } ?: 0
        val undercoverCount = finalPlayers?.count { it.role == "Undercover" } ?: 0
        val mrWhiteCount = finalPlayers?.count { it.role == "Mr. White" } ?: 0
        
        // Count words
        val civilianWordCount = finalPlayers?.count { it.word == civilianWord } ?: 0
        val undercoverWordCount = finalPlayers?.count { it.word == undercoverWord } ?: 0
        val emptyWordCount = finalPlayers?.count { it.word.isEmpty() } ?: 0

        // Print debug information
        println("Final players:")
        finalPlayers?.forEach { player ->
            println("Player: ${player.name}, Role: ${player.role}, Word: ${player.word}")
        }

        // Verify role distribution
        assertEquals(3, civilianCount)
        assertEquals(2, undercoverCount)
        assertEquals(1, mrWhiteCount)

        // Verify word distribution
        assertEquals(3, civilianWordCount)
        assertEquals(2, undercoverWordCount)
        assertEquals(1, emptyWordCount)
    }
}

