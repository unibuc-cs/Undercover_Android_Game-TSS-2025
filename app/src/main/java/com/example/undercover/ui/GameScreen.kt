package com.example.undercover.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
fun GameScreen(
    players: List<Player>,
    onGameEnd: () -> Unit,
    onResetWords: () -> Unit,
    onNavigateToPlayers: () -> Unit
) {
    val activePlayers by remember { mutableStateOf(players.toMutableList()) }
    val eliminatedPlayers by remember { mutableStateOf(mutableListOf<Player>()) }
    var mrWhiteGuess by remember { mutableStateOf("") }
    var showGuessDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var guessFeedback by remember { mutableStateOf("") }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var playerToEliminate by remember { mutableStateOf<Player?>(null) }
    var isForgetfulMode by remember { mutableStateOf(false) }
    var selectedPlayerForHint by remember { mutableStateOf<Player?>(null) }
    var showHintDialog by remember { mutableStateOf(false) }
    val undercoverCount = activePlayers.count { it.role == "Undercover" }
    val civilianCount = activePlayers.count { it.role == "Civil" }
    val isGameRunning =
        activePlayers.size >= 2 && undercoverCount > 0 && undercoverCount < civilianCount


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
                enabled = isGameRunning,
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

        // Modul Uituc - Afișează un switch pentru activare
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Modul Uituc", fontSize = 16.sp)
            Spacer(modifier = Modifier.padding(8.dp))
            Switch(checked = isForgetfulMode, onCheckedChange = { isForgetfulMode = it })
        }

        if (isForgetfulMode) {
            Button(
                onClick = { showHintDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Vezi cuvântul unui jucător")
            }
        }

        // Dialog pentru selectarea unui jucător și afișarea cuvântului său
        if (showHintDialog) {
            AlertDialog(
                onDismissRequest = { showHintDialog = false },
                title = { Text("Alege un jucător") },
                text = {
                    Column {
                        activePlayers.forEach { player ->
                            Button(
                                onClick = {
                                    selectedPlayerForHint = player
                                    showHintDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(player.name)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showHintDialog = false }) {
                        Text("Închide")
                    }
                }
            )
        }

        // Dacă un jucător este selectat, arată-i cuvântul
        selectedPlayerForHint?.let { player ->
            AlertDialog(
                onDismissRequest = { selectedPlayerForHint = null },
                title = { Text("Cuvântul jucătorului ${player.name}") },
                text = { Text("Cuvânt: ${player.word}") },
                confirmButton = {
                    Button(onClick = { selectedPlayerForHint = null }) {
                        Text("OK")
                    }
                }
            )
        }

        // Butoane pentru resetare și navigare
        Button(
            enabled = isGameRunning,
            onClick = onResetWords,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Resetează Cuvintele")
        }

        Button(
            enabled = isGameRunning,
            onClick = onNavigateToPlayers,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Înapoi la Jucători")
        }

        if (!isGameRunning) {
            Button(onClick = onGameEnd) {
                Text("Jocul s-a terminat")
            }
        }

    }
}
