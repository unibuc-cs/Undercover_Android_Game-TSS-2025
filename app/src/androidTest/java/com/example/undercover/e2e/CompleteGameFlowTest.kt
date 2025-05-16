package com.example.undercover.e2e

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.undercover.data.Player
import com.example.undercover.ui.MainScreen
import com.example.undercover.ui.PlayerSelectionScreen
import com.example.undercover.ui.RoleConfigurationScreen
import com.example.undercover.ui.GameScreen
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CompleteGameFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun waitForElement(text: String, timeoutMillis: Long = 5000) {
        composeTestRule.waitUntil(timeoutMillis = timeoutMillis) {
            try {
                composeTestRule.onNodeWithText(text).assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun testCompleteGameFlow() {
        // State variables to track the flow
        var currentScreen = "main"
        var playerCount = 0
        var is18Plus = false
        var selectedPlayers: List<Player>? = null
        var configuredPlayers: List<Player>? = null
        var numUndercover = 0
        var numMrWhite = 0
        var gameEnded = false
        var wordsReset = false
        var navigatedToPlayers = false

        // Single setContent that handles all screens
        composeTestRule.setContent {
            when (currentScreen) {
                "main" -> {
                    MainScreen(onStartGame = { count, plus18 ->
                        playerCount = count
                        is18Plus = plus18
                        currentScreen = "player_selection"
                    })
                }
                "player_selection" -> {
                    PlayerSelectionScreen(
                        players = List(playerCount) { Player("", "", "") },
                        onPlayersSet = { players ->
                            selectedPlayers = players
                            currentScreen = "role_config"
                        }
                    )
                }
                "role_config" -> {
                    RoleConfigurationScreen(
                        players = selectedPlayers ?: emptyList(),
                        is18Plus = is18Plus
                    ) { players, undercover, mrWhite ->
                        configuredPlayers = players
                        numUndercover = undercover
                        numMrWhite = mrWhite
                        currentScreen = "game"
                    }
                }
                "game" -> {
                    GameScreen(
                        players = configuredPlayers ?: emptyList(),
                        onGameEnd = { gameEnded = true },
                        onResetWords = { wordsReset = true },
                        onNavigateToPlayers = { navigatedToPlayers = true }
                    )
                }
            }
        }

        // Step 1: Main Screen
        waitForElement("Numărul de jucători")
        composeTestRule.onNodeWithText("Numărul de jucători").performClick()
        composeTestRule.onNodeWithText("Numărul de jucători").performTextInput("6")
        
        waitForElement("Începe Jocul")
        composeTestRule.onNodeWithText("Începe Jocul").performClick()

        // Wait for navigation to complete and verify we're on player selection screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            currentScreen == "player_selection"
        }
        assertEquals("player_selection", currentScreen)
        assertEquals(6, playerCount)
        assert(!is18Plus)

        // Step 2: Player Selection Screen
        for (i in 0 until 6) {
            val playerText = "Jucător ${i + 1}"
            waitForElement(playerText)
            composeTestRule.onNodeWithText(playerText).performClick()
            
            waitForElement("Introduceți numele")
            composeTestRule.onNodeWithText("Introduceți numele").performTextInput("Player$i")
            
            waitForElement("Confirmă")
            composeTestRule.onNodeWithText("Confirmă").performClick()
        }

        waitForElement("Începe jocul")
        composeTestRule.onNodeWithText("Începe jocul").performClick()

        // Wait for navigation to role config screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            currentScreen == "role_config"
        }
        assertEquals("role_config", currentScreen)
        assertNotNull(selectedPlayers)
        assertEquals(6, selectedPlayers?.size)
        assertTrue(selectedPlayers?.all { it.name.isNotEmpty() } == true)

        // Step 3: Role Configuration Screen
        waitForElement("+")
        composeTestRule.onAllNodesWithText("+")[0].performClick()
        
        waitForElement("Confirmă Rolurile")
        composeTestRule.onNodeWithText("Confirmă Rolurile").performClick()

        // Wait for navigation to game screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            currentScreen == "game"
        }
        assertEquals("game", currentScreen)
        assertNotNull(configuredPlayers)
        assertEquals(6, configuredPlayers?.size)
        assertEquals(2, numUndercover)
        assertEquals(1, numMrWhite)

        // Step 4: Game Screen
        configuredPlayers?.forEach { player ->
            waitForElement(player.name)
            composeTestRule.onNodeWithText(player.name).assertIsDisplayed()
        }

        // Simulate game end
        waitForElement("Termină Jocul")
        composeTestRule.onNodeWithText("Termină Jocul").performClick()
        assert(gameEnded)

        // Verify game end state
        waitForElement("Jocul s-a terminat")
        composeTestRule.onNodeWithText("Jocul s-a terminat").assertIsDisplayed()

        // Test reset functionality
        waitForElement("Resetează Cuvintele")
        composeTestRule.onNodeWithText("Resetează Cuvintele").performClick()
        assert(wordsReset)

        // Test navigation back to players
        waitForElement("Înapoi la Jucători")
        composeTestRule.onNodeWithText("Înapoi la Jucători").performClick()
        assert(navigatedToPlayers)
    }
} 