package com.example.undercover.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import com.example.undercover.data.Player
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test


class PlayerSelectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val initialPlayers = listOf(
        Player("", "", ""),
        Player("", "", ""),
        Player("", "", "")
    )

    @Test
    fun addPlayer_increasesListSize() {
        var lastList: List<Player> = emptyList()
        composeTestRule.setContent {
            PlayerSelectionScreen(initialPlayers) { lastList = it }
        }
        composeTestRule.onNodeWithContentDescription("Add")
            .performClick()
        composeTestRule.onNodeWithText("Adaugă jucător")
            .assertIsEnabled()
        composeTestRule.onNodeWithText("Începe jocul")
            .assertIsNotEnabled()
    }

    @Test
    fun editPlayer_onLongClickOpensDialog_andConfirmEdit() {
        var lastList: List<Player> = emptyList()
        composeTestRule.setContent {
            PlayerSelectionScreen(initialPlayers) { lastList = it }
        }
        composeTestRule.onNodeWithText("Jucător 1")
            .performTouchInput { longClick() }
        composeTestRule.onNodeWithText("Editează")
            .performClick()
        composeTestRule.onNodeWithText("Confirmă")
            .assertIsNotEnabled()
        composeTestRule.onNodeWithText("Introduceți numele")
            .performTextInput("John")
        composeTestRule.onNodeWithText("Confirmă")
            .performClick()
        composeTestRule.onNodeWithText("John")
            .assertIsDisplayed()
    }

    @Test
    fun deletePlayer_reducesListSize() {
        var lastList: List<Player> = emptyList()
        composeTestRule.setContent {
            PlayerSelectionScreen(initialPlayers + Player("A", "", "")) { lastList = it }
        }
        composeTestRule.onNodeWithText("A")
            .performTouchInput { longClick() }
        composeTestRule.onNodeWithText("Șterge")
            .performClick()
        composeTestRule.onNodeWithText("A")
            .assertDoesNotExist()
    }

    @Test
    fun startGameButton_enabledOnlyWhenAllNamesSet_andMinSize() {
        var lastList: List<Player> = emptyList()
        composeTestRule.setContent {
            PlayerSelectionScreen(initialPlayers) { lastList = it }
        }
        composeTestRule.onNodeWithText("Începe jocul")
            .assertIsNotEnabled()
        initialPlayers.indices.forEach { index ->
            composeTestRule.onNodeWithText("Jucător ${index + 1}")
                .performClick()
            composeTestRule.onNodeWithText("Introduceți numele")
                .performTextInput("P$index")
            composeTestRule.onNodeWithText("Confirmă")
                .performClick()
        }
        composeTestRule.onNodeWithText("Începe jocul")
            .assertIsEnabled()
        composeTestRule.onNodeWithText("Începe jocul")
            .performClick()
        assertEquals(3, lastList.size)
        lastList.forEachIndexed { i, p -> assertEquals("P$i", p.name) }
    }
}
