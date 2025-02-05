package com.example.undercover.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.undercover.data.Player
import com.example.undercover.data.WordGenerator

@Composable
fun RoleAssignmentScreen(
    players: List<Player>,
    is18Plus: Boolean,
    onGameStart: (List<Player>) -> Unit
) {
    val context = LocalContext.current
    val wordGenerator = remember { WordGenerator(context) }
    var assignedPlayers by remember { mutableStateOf(emptyList<Player>()) }
    var currentPlayerIndex by remember { mutableIntStateOf(0) }
    var showPopup by remember { mutableStateOf(true) }
    var revealedWord by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(players) {
        assignedPlayers = assignRoles(players, wordGenerator, is18Plus)
        showPopup = true
    }

    if (showPopup && assignedPlayers.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("E rândul lui ${assignedPlayers[currentPlayerIndex].name}") },
            text = {
                if (revealedWord == null) {
                    Text("Apasă pe 'Reveal Word' pentru a vedea cuvântul tău.")
                } else {
                    Text("Cuvântul tău este: $revealedWord")
                }
            },
            confirmButton = {
                if (revealedWord == null) {
                    Button(onClick = {
                        revealedWord = assignedPlayers[currentPlayerIndex].word
                    }) {
                        Text("Reveal Word")
                    }
                } else {
                    Button(onClick = {
                        revealedWord = null
                        if (currentPlayerIndex < assignedPlayers.size - 1) {
                            currentPlayerIndex++
                        } else {
                            showPopup = false
                            onGameStart(assignedPlayers)
                        }
                    }) {
                        Text("Next")
                    }
                }
            }
        )
    }
}

fun assignRoles(
    players: List<Player>,
    wordGenerator: WordGenerator,
    is18Plus: Boolean
): List<Player> {
    val totalPlayers = players.size
    val roles = mutableListOf<String>()

    // 1x Mr. White (dacă sunt cel puțin 5 jucători)
    if (totalPlayers >= 5) {
        roles.add("Mr. White")
    }

    // Calculăm numărul de Undercoveri (~20% din jucători, dar minim 1)
    val numUndercover = maxOf(1, (totalPlayers * 0.2).toInt())
    repeat(numUndercover) { roles.add("Undercover") }

    // Restul sunt Civili
    while (roles.size < totalPlayers) {
        roles.add("Civil")
    }

    // Amestecăm rolurile
    roles.shuffle()

    // Generăm cuvintele pentru civili și undercoveri
//    val civilianWord = aiWordGenerator.generateWord("Generate a civilian word")
//    val undercoverWord = aiWordGenerator.generateWord("Generate a similar word for undercover")
    val (civilianWord, undercoverWord) = wordGenerator.generateWords(totalPlayers)

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
