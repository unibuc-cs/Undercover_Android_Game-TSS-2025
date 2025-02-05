package com.example.undercover.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.undercover.data.WordGenerator

@Composable
fun RoleAssignmentScreen(
    players: List<String>,
    is18Plus: Boolean,
    onGameStart: (Map<String, String>) -> Unit
) {
    val context = LocalContext.current
//    val aiWordGenerator = remember { AiWordGenerator(context) }
    val wordGenerator = remember { WordGenerator(context) }
    var assignedWords by remember { mutableStateOf(emptyMap<String, String>()) }
    var currentPlayerIndex by remember { mutableIntStateOf(0) }
    var showPopup by remember { mutableStateOf(true) }
    var revealedWord by remember { mutableStateOf<String?>(null) }

    // Generăm cuvintele doar o singură dată
    LaunchedEffect(players) {
        assignedWords = assignWords(players, wordGenerator, is18Plus)
        showPopup = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Word Assignment", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(20.dp))

        assignedWords.forEach { (player, _) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.LightGray)
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$player: ***", fontSize = 18.sp)
                }
            }
        }
    }

    // Pop-up pentru fiecare jucător
    if (showPopup && assignedWords.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { /* Nu permitem închiderea accidentală */ },
            title = { Text("E rândul lui ${players[currentPlayerIndex]}") },
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
                        revealedWord = assignedWords[players[currentPlayerIndex]]
                    }) {
                        Text("Reveal Word")
                    }
                } else {
                    Button(onClick = {
                        revealedWord = null
                        if (currentPlayerIndex < players.size - 1) {
                            currentPlayerIndex++
                        } else {
                            showPopup = false
                            onGameStart(players.associateWith { assignedWords[it] ?: "" })
                        }
                    }) {
                        Text("Next")
                    }
                }
            }
        )
    }
}

/**
 * Funcție pentru a distribui cuvintele în funcție de rolurile atribuite.
 */
fun assignWords(
    players: List<String>,
//    aiWordGenerator: AiWordGenerator,
    wordGenerator: WordGenerator,
    is18Plus: Boolean
): Map<String, String> {
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
    val wordPairs = wordGenerator.generateWords(totalPlayers)
    val civilianWord = wordPairs.first
    val undercoverWord = wordPairs.second

    val assignedWords = mutableMapOf<String, String>()
    players.forEachIndexed { index, player ->
        val role = roles[index]
        val word = when (role) {
            "Civil" -> civilianWord
            "Undercover" -> undercoverWord
            "Mr. White" -> "???"
            else -> "Error"
        }
        assignedWords[player] = word
    }

    // Log pentru debugging
    Log.d("RoleAssignment", "Assigned words: $assignedWords")

    return assignedWords
}
