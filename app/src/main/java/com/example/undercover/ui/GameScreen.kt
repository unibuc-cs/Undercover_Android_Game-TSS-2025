package com.example.undercover.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameScreen(players: Map<String, String>, onGameEnd: () -> Unit) {
    var activePlayers by remember { mutableStateOf(players.keys.toList()) }
    var eliminatedPlayers by remember { mutableStateOf(mutableMapOf<String, String>()) }
    var mrWhiteGuess by remember { mutableStateOf("") }
    var showGuessDialog by remember { mutableStateOf(false) }

    val startingPlayer = remember {
        activePlayers.filter { players[it] != "Mr. White" }.randomOrNull()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Jucătorul care începe: $startingPlayer", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Jucători activi:", fontSize = 18.sp)
        activePlayers.forEach { player ->
            Button(
                onClick = {
                    activePlayers = activePlayers - player
                    eliminatedPlayers[player] = players[player] ?: "Unknown"
                    if (players[player] == "Mr. White") {
                        showGuessDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text(player)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Eliminați:", fontSize = 18.sp)
        eliminatedPlayers.forEach { (player, role) ->
            Text(text = "$player - $role", fontSize = 16.sp)
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
                    Button(onClick = { showGuessDialog = false }) {
                        Text("Trimite răspunsul")
                    }
                }
            )
        }

        if (activePlayers.size == 1) {
            Button(onClick = onGameEnd) {
                Text("Jocul s-a terminat")
            }
        }
    }
}