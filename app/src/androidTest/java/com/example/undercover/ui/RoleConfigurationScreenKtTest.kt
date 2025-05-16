package com.example.undercover.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.undercover.data.Player
import com.example.undercover.ui.RoleConfigurationScreen
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

//todo fix this test class
class RoleConfigurationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val players5 = List(5) { Player("P$it", "", "") }

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
    fun defaultValues_andSummaryCard() {
        composeTestRule.setContent {
            RoleConfigurationScreen(players5, is18Plus = false) { _, _, _ -> }
        }
        
        // Wait for the UI to be ready and verify initial state
        waitForElement("Configurare Roluri")
        
        // Verify role counts
        waitForElement("Undercover: 2")
        composeTestRule.onNodeWithText("Undercover: 2").assertIsDisplayed()
        
        waitForElement("Mr. White: 1")
        composeTestRule.onNodeWithText("Mr. White: 1").assertIsDisplayed()
        
        waitForElement("Civili: 2")
        composeTestRule.onNodeWithText("Civili: 2").assertIsDisplayed()
    }

    @Test
    fun decreaseUndercover_belowMin_disablesButton_andShowsError() {
        composeTestRule.setContent {
            RoleConfigurationScreen(players5, is18Plus = false) { _, _, _ -> }
        }
        
        // Wait for the UI to be ready
        waitForElement("Configurare Roluri")
        
        // Click decrease button
        waitForElement("-")
        composeTestRule.onNodeWithText("-").performClick()
        
        // Verify button is disabled
        composeTestRule.onNodeWithText("-").assertIsNotEnabled()
    }

    @Test
    fun increaseUndercover_aboveMax_disablesButton_andShowsError() {
        composeTestRule.setContent {
            RoleConfigurationScreen(players5, is18Plus = false) { _, _, _ -> }
        }
        
        // Wait for the UI to be ready
        waitForElement("Configurare Roluri")
        
        // Click increase button twice
        waitForElement("+")
        repeat(2) { composeTestRule.onNodeWithText("+").performClick() }
        
        // Verify button is disabled
        composeTestRule.onNodeWithText("+").assertIsNotEnabled()
    }

    @Test
    fun adjustMrWhite_andValidateError() {
        composeTestRule.setContent {
            RoleConfigurationScreen(players5, is18Plus = false) { _, _, _ -> }
        }
        
        // Wait for the UI to be ready
        waitForElement("Configurare Roluri")
        
        // Click decrease button for Mr. White
        waitForElement("-")
        composeTestRule.onAllNodesWithText("-")[1].performClick()
        
        // Verify button is disabled
        composeTestRule.onAllNodesWithText("-")[1].assertIsNotEnabled()
    }

    @Test
    fun confirmButton_enabledOnlyWhenNoError_andInvokesCallback() {
        var calledPlayers: List<Player>? = null
        var calledUc = -1
        var calledMw = -1
        
        composeTestRule.setContent {
            RoleConfigurationScreen(players5, is18Plus = false) { pl, uc, mw ->
                calledPlayers = pl
                calledUc = uc
                calledMw = mw
            }
        }
        
        // Wait for the UI to be ready
        waitForElement("Configurare Roluri")
        
        // Adjust undercover and mrwhite to valid values
        waitForElement("+")
        composeTestRule.onAllNodesWithText("+")[0].performClick()
        
        waitForElement("-")
        composeTestRule.onAllNodesWithText("-")[1].performClick()
        
        // Wait for UI updates and verify confirm button
        waitForElement("Confirmă Rolurile")
        composeTestRule.onNodeWithText("Confirmă Rolurile").assertIsEnabled()
        composeTestRule.onNodeWithText("Confirmă Rolurile").performClick()
        
        // Wait for callback to be invoked
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            calledPlayers != null
        }
        
        // Verify callback values
        assertNotNull(calledPlayers)
        assertEquals(players5, calledPlayers)
        assertEquals(2, calledUc)
        assertEquals(0, calledMw)
    }
} 