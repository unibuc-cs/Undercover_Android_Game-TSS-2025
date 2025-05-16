package com.example.undercover.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import com.example.undercover.data.Player
import com.example.undercover.ui.GameScreen
import org.junit.Rule
import org.junit.Test

class GameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gameScreen_displaysCorrectInitialState() {
        val players = listOf(
            Player("Stefan", "Civil", "apple"),
            Player("Diana", "Civil", "apple"),
            Player("Tavi", "Undercover", "banana"),
            Player("Nicoleta", "Mr. White", ""),
            Player("Lorena", "Civil", "apple"),
        )

        composeTestRule.setContent {
            GameScreen(
                players = players,
                onGameEnd = {},
                onResetWords = {},
                onNavigateToPlayers = {}
            )
        }


        composeTestRule.onAllNodesWithText("Stefan").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Diana").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Tavi").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Nicoleta").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Lorena").onFirst().assertIsDisplayed()
    }

    @Test
    fun gameScreen_buttonStates_areCorrect() {
        val endGamePlayers = listOf(
            Player("Stefan", "Civil", "apple"),
            Player("Nicoleta", "Mr.White", "")
        )

        composeTestRule.setContent {
            GameScreen(
                players = endGamePlayers,
                onGameEnd = {},
                onResetWords = {},
                onNavigateToPlayers = {}
            )
        }

        composeTestRule.onNodeWithText("Jocul s-a terminat").assertIsDisplayed()
    }
} 