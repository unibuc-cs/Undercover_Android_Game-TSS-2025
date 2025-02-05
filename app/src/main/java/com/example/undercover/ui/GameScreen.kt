package com.example.undercover.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.undercover.data.Player

@SuppressLint("MutableCollectionMutableState")
@Composable
fun GameScreen(players: List<Player>, onGameEnd: () -> Unit) {
    val activePlayers by remember { mutableStateOf(players.toMutableList()) }
    val eliminatedPlayers by remember { mutableStateOf(mutableListOf<Player>()) }
    var mrWhiteGuess by remember { mutableStateOf("") }
    var showGuessDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var guessFeedback by remember { mutableStateOf("") }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var playerToEliminate by remember { mutableStateOf<Player?>(null) }

    val startingPlayer = remember {
        activePlayers.filter { it.role != "Mr. White" }.randomOrNull()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Jucătorul care începe: ${startingPlayer?.name}", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Jucători activi:", fontSize = 18.sp)
        activePlayers.forEach { player ->
            Button(
                onClick = {
                    playerToEliminate = player
                    showConfirmationDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(player.name)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Eliminați:", fontSize = 18.sp)
        eliminatedPlayers.forEach { player ->
            Text(text = "${player.name} - ${player.role}", fontSize = 16.sp)
        }

        if (showGuessDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Mr. White trebuie să ghicească cuvântul civililor!") },
                text = {
                    OutlinedTextField(
                        value = mrWhiteGuess,
                        onValueChange = { mrWhiteGuess = it },
                        label = { Text("Introdu răspunsul") }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        showGuessDialog = false
                        val civilianWord = activePlayers.find { it.role == "Civil" }?.word
                        guessFeedback = if (mrWhiteGuess.equals(civilianWord, ignoreCase = true)) {
                            "Felicitări! Mr. White a ghicit corect și câștigă jocul!"
                        } else {
                            "Mr. White a greșit! Jocul continuă."
                        }
                        showFeedbackDialog = true
                    }) {
                        Text("Trimite răspunsul")
                    }
                }
            )
        }

        if (showFeedbackDialog) {
            AlertDialog(
                onDismissRequest = { showFeedbackDialog = false },
                title = { Text("Rezultat") },
                text = { Text(guessFeedback) },
                confirmButton = {
                    Button(onClick = {
                        showFeedbackDialog = false
                        if (guessFeedback.contains("câștigă")) {
                            onGameEnd()
                        }
                    }) {
                        Text("OK")
                    }
                }
            )
        }

        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text("Ești sigur că vrei să elimini acest jucător?") },
                text = { Text("${playerToEliminate?.name}") },
                confirmButton = {
                    Button(onClick = {
                        playerToEliminate?.let {
                            activePlayers.remove(it)
                            eliminatedPlayers.add(it)
                            if (it.role == "Mr. White") {
                                showGuessDialog = true
                            }
                        }
                        playerToEliminate = null
                        showConfirmationDialog = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showConfirmationDialog = false
                        playerToEliminate = null
                    }) {
                        Text("Anulează")
                    }
                }
            )
        }

        // Verificăm condițiile de finalizare a jocului
        if (activePlayers.size < 2 || activePlayers.none { it.role == "Undercover" } ||
            (activePlayers.count { it.role == "Civilian" } == 1 && activePlayers.count { it.role == "Undercover" } == 1)) {
            Button(onClick = onGameEnd) {
                Text("Jocul s-a terminat")
            }
        }
    }
}
