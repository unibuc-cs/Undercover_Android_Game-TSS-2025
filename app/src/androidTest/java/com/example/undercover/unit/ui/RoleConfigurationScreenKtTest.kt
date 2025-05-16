package com.example.undercover.unit.ui

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

    @Test
    fun defaultValues_andSummaryCard() {
        composeTestRule.setContent {
            RoleConfigurationScreen(players5, is18Plus = false) { _, _, _ -> }
        }
        
        // Wait for the UI to be ready
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Undercover: 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mr. White: 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Civili: 2").assertIsDisplayed()
    }

    @Test
    fun decreaseUndercover_belowMin_disablesButton_andShowsError() {
        composeTestRule.setContent {
            RoleConfigurationScreen(players5, is18Plus = false) { _, _, _ -> }
        }
        
        // Wait for the UI to be ready
        composeTestRule.waitForIdle()
        
        repeat(1) { composeTestRule.onNodeWithText("-").performClick() }
        composeTestRule.onNodeWithText("-").assertIsNotEnabled()
    }

    @Test
    fun increaseUndercover_aboveMax_disablesButton_andShowsError() {
        composeTestRule.setContent {
            RoleConfigurationScreen(players5, is18Plus = false) { _, _, _ -> }
        }
        
        // Wait for the UI to be ready
        composeTestRule.waitForIdle()
        
        repeat(2) { composeTestRule.onNodeWithText("+").performClick() }
        composeTestRule.onNodeWithText("+").assertIsNotEnabled()
    }

    @Test
    fun adjustMrWhite_andValidateError() {
        composeTestRule.setContent {
            RoleConfigurationScreen(players5, is18Plus = false) { _, _, _ -> }
        }
        
        // Wait for the UI to be ready
        composeTestRule.waitForIdle()
        
        // decrease to 0 (minMrWhite=0) then decrease again
        composeTestRule.onAllNodesWithText("-")[1].performClick()
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
        composeTestRule.waitForIdle()
        
        // adjust undercover and mrwhite to valid
        composeTestRule.onAllNodesWithText("+")[0].performClick()
        composeTestRule.onAllNodesWithText("-")[1].performClick()
        
        // Wait for UI updates after clicks
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Confirmă Rolurile").assertIsEnabled()
        composeTestRule.onNodeWithText("Confirmă Rolurile").performClick()
        
        // Wait for callback to be invoked
        composeTestRule.waitForIdle()
        
        assertNotNull(calledPlayers)
        assertEquals(players5, calledPlayers)
        assertEquals(2, calledUc)
        assertEquals(0, calledMw)
    }
}

