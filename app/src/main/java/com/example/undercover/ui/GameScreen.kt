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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.undercover.data.Player

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    players: List<Player>,
    onGameEnd: () -> Unit,
    onResetWords: () -> Unit,
    onNavigateToPlayers: () -> Unit
) {
    // Folosim mutableStateListOf pentru a putea actualiza listele din UI
    val activePlayers = remember { mutableStateListOf(*players.toTypedArray()) }
    val eliminatedPlayers = remember { mutableStateListOf<Player>() }

    var mrWhiteGuess by remember { mutableStateOf("") }
    var showGuessDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var guessFeedback by remember { mutableStateOf("") }
    var showEliminationConfirmation by remember { mutableStateOf(false) }
    var showNavigationConfirmation by remember { mutableStateOf(false) }
    var showResetConfirmation by remember { mutableStateOf(false) }
    var playerToEliminate by remember { mutableStateOf<Player?>(null) }
    var isForgetfulMode by remember { mutableStateOf(false) }
    var selectedPlayerForHint by remember { mutableStateOf<Player?>(null) }

    // Condiția de rulare a jocului
    val undercoverCount = activePlayers.count { it.role == "Undercover" }
    val civilianCount = activePlayers.count { it.role == "Civil" }
    val isGameRunning =
        activePlayers.size >= 2 && undercoverCount > 0 && undercoverCount < civilianCount

    // Selectăm aleator un jucător (cu excepția lui Mr. White) pentru a începe jocul
    val startingPlayer = remember {
        activePlayers.filter { it.role != "Mr. White" }.randomOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Undercover Game", fontSize = 22.sp) }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Card pentru afișarea jucătorului care începe
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Jucătorul care începe:", fontSize = 20.sp)
                        Text(
                            text = startingPlayer?.name ?: "Niciun jucător",
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Card cu lista jucătorilor activi
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Jucători activi:", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn {
                            items(activePlayers) { player ->
                                Button(
                                    onClick = {
                                        playerToEliminate = player
                                        showEliminationConfirmation = true
                                    },
                                    enabled = isGameRunning,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(player.name)
                                }
                            }
                        }
                    }
                }

                // Card cu lista jucătorilor eliminați (doar dacă există)
                if (eliminatedPlayers.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Eliminați:", fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            eliminatedPlayers.forEach { player ->
                                Text("${player.name} - ${player.role}", fontSize = 16.sp)
                            }
                        }
                    }
                }

                // Modul "Uituc" – switch pentru activare
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Modul Uituc", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isForgetfulMode,
                        onCheckedChange = { isForgetfulMode = it }
                    )
                }

                // Rând cu butoanele pentru resetarea cuvintelor și navigarea la ecranul jucătorilor
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = { showResetConfirmation = true },
                        enabled = isGameRunning
                    ) {
                        Text("Resetează Cuvintele")
                    }
                    OutlinedButton(
                        onClick = { showNavigationConfirmation = true },
                        enabled = isGameRunning
                    ) {
                        Text("Înapoi la Jucători")
                    }
                }

                // Dacă jocul nu mai poate continua, afișăm butonul de terminare
                if (!isGameRunning) {
                    Button(
                        onClick = onGameEnd,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Jocul s-a terminat")
                    }
                }
            }
        }
    )

    // Dialogul pentru ghicirea de către Mr. White
    if (showGuessDialog) {
        AlertDialog(
            onDismissRequest = { /* Nu permite închiderea pe click în afara dialogului */ },
            title = { Text("Mr. White trebuie să ghicească cuvântul civililor!") },
            text = {
                OutlinedTextField(
                    value = mrWhiteGuess,
                    onValueChange = { mrWhiteGuess = it },
                    label = { Text("Introdu răspunsul") },
                    modifier = Modifier.fillMaxWidth()
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

    // Dialogul pentru afișarea feedback-ului după ghicire
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

    // Dialog pentru confirmarea eliminării unui jucător
    if (showEliminationConfirmation) {
        AlertDialog(
            onDismissRequest = { showEliminationConfirmation = false },
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
                    showEliminationConfirmation = false
                }) {
                    Text("Da")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showEliminationConfirmation = false
                    playerToEliminate = null
                }) {
                    Text("Anulează")
                }
            }
        )
    }

    // Dacă modul "Uituc" este activat, afișăm un dialog în care se poate alege un jucător pentru a vedea cuvântul
    if (isForgetfulMode && activePlayers.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { isForgetfulMode = false },
            title = { Text("Alege un jucător pentru a vedea cuvântul") },
            text = {
                LazyColumn {
                    items(activePlayers) { player ->
                        Button(
                            onClick = {
                                selectedPlayerForHint = player
                                isForgetfulMode = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(player.name)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { isForgetfulMode = false }) {
                    Text("Închide")
                }
            }
        )
    }

    // Dialog pentru afișarea cuvântului jucătorului selectat (din modul "Uituc")
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

    if (showNavigationConfirmation) {
        AlertDialog(
            onDismissRequest = { showNavigationConfirmation = false },
            title = { Text("Confirmare navigare") },
            text = { Text("Ești sigur că vrei să te întorci la ecranul jucătorilor? Progresul jocului se poate pierde.") },
            confirmButton = {
                Button(onClick = {
                    showNavigationConfirmation = false
                    onNavigateToPlayers()
                }) {
                    Text("Da, mergi la Jucători")
                }
            },
            dismissButton = {
                Button(onClick = { showNavigationConfirmation = false }) {
                    Text("Anulează")
                }
            }
        )
    }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Confirmare resetare") },
            text = { Text("Ești sigur că vrei să schimbi cuvintele atribuite jucătorilor?") },
            confirmButton = {
                Button(onClick = {
                    showResetConfirmation = false
                    onResetWords()
                }) {
                    Text("Da, schimbă cuvintele")
                }
            },
            dismissButton = {
                Button(onClick = { showResetConfirmation = false }) {
                    Text("Anulează")
                }
            }
        )
    }
}
