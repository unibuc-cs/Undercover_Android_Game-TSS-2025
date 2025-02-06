package com.example.undercover.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.undercover.data.Player
import com.example.undercover.data.WordGenerator

@Composable
fun RoleAssignmentScreen(
    players: List<Player>,
    is18Plus: Boolean,
    numUndercover: Int,
    numMrWhite: Int,
    onGameStart: (List<Player>) -> Unit
) {
    BackHandler(enabled = true) {
    }
    val context = LocalContext.current
    val wordGenerator = remember { WordGenerator(context) }
    var assignedPlayers by remember { mutableStateOf(emptyList<Player>()) }
    var currentPlayerIndex by remember { mutableIntStateOf(0) }
    var showPopup by remember { mutableStateOf(true) }
    var revealedWord by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(players) {
        assignedPlayers = assignRoles(players, wordGenerator, is18Plus, numUndercover, numMrWhite)
        showPopup = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = showPopup && assignedPlayers.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "E rândul lui ${assignedPlayers[currentPlayerIndex].name}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = revealedWord
                            ?: "Apasă pe 'Reveal Word' pentru a vedea cuvântul tău.",
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (revealedWord == null) {
                                revealedWord = assignedPlayers[currentPlayerIndex].word
                            } else {
                                revealedWord = null
                                if (currentPlayerIndex < assignedPlayers.size - 1) {
                                    currentPlayerIndex++
                                } else {
                                    showPopup = false
                                    onGameStart(assignedPlayers)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDD2C00)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (revealedWord == null) "Reveal Word" else "Next",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            if (revealedWord != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun assignRoles(
    players: List<Player>,
    wordGenerator: WordGenerator,
    is18Plus: Boolean,
    numUndercover: Int,
    numMrWhite: Int
): List<Player> {
    val totalPlayers = players.size
    val roles = mutableListOf<String>()

    // Adăugăm Mr. White
    repeat(numMrWhite) { roles.add("Mr. White") }

    // Adăugăm Undercover
    repeat(numUndercover) { roles.add("Undercover") }

    // Restul sunt Civili
    while (roles.size < totalPlayers) {
        roles.add("Civil")
    }

    roles.shuffle()

    val (civilianWord, undercoverWord) = wordGenerator.generateWords()

    return players.mapIndexed { index, player ->
        val role = roles[index]
        val word = when (role) {
            "Civil" -> civilianWord
            "Undercover" -> undercoverWord
            "Mr. White" -> "???"
            else -> "Error"
        }
        player.copy(role = role, word = word)
    }
}