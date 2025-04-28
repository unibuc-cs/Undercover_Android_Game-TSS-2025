package com.example.undercover.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testErrorMessage_whenInvalidNumberOfPlayers() {
        composeTestRule.setContent {
            MainScreen(onStartGame = { _, _ -> })
        }

        composeTestRule.onNodeWithText("Numărul de jucători")
            .performTextInput("2")

        composeTestRule.onNodeWithText("Începe Jocul").performClick()

        composeTestRule.onNodeWithText("Introdu un număr între 3 și 20!")
            .assertIsDisplayed()
    }

    @Test
    fun testErrorMessage_disappears_whenValidNumberOfPlayers() {
        composeTestRule.setContent {
            MainScreen(onStartGame = { _, _ -> })
        }

        composeTestRule.onNodeWithText("Numărul de jucători")
            .performTextInput("2")

        composeTestRule.onNodeWithText("Începe Jocul").performClick()

        composeTestRule.onNodeWithText("Introdu un număr între 3 și 20!")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Numărul de jucători")
            .performTextReplacement("5")

        composeTestRule.onNodeWithText("Începe Jocul").performClick()

        composeTestRule.onNodeWithText("Introdu un număr între 3 și 20!")
            .assertDoesNotExist()
    }

    @Test
    fun testStartGameButton_onClick() {
        var startGameCalled = false

        composeTestRule.setContent {
            MainScreen(onStartGame = { numPlayers, is18PlusEnabled ->
                startGameCalled = true
                assertEquals(5, numPlayers)
                assertFalse(is18PlusEnabled)
            })
        }

        composeTestRule.onNodeWithText("Numărul de jucători")
            .performTextInput("5")

        composeTestRule.onNodeWithText("Începe Jocul")
            .performClick()

        assertTrue(startGameCalled)
    }
}


