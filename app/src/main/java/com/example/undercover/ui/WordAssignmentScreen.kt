package com.example.undercover.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.undercover.data.AiWordGenerator

@Composable
fun WordAssignmentScreen(players: List<String>, is18Plus: Boolean, onGameStart: () -> Unit) {
    val context = LocalContext.current
    val aiWordGenerator = remember { AiWordGenerator(context) }
    var assignedWords by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var currentPlayerIndex by remember { mutableIntStateOf(0) }
    var showPopup by remember { mutableStateOf(false) }
    var revealedWord by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(players) {
        val tempWords = players.map { player ->
            val word = aiWordGenerator.generateWord("Generate a word pair") ?: "Unknown"
            Log.d("WordAssignment", "Generated word for $player: $word")
            player to word
        }
        assignedWords = tempWords
        showPopup = true
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Word Assignment", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(20.dp))

        if (assignedWords.isNotEmpty()) {
            assignedWords.forEach { (player, _) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$player: ***", fontSize = 18.sp)
                    }
                }
            }
        } else {
            CircularProgressIndicator()
            Text("Generating words...", fontSize = 16.sp)
        }
    }

    if (showPopup && assignedWords.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { },
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
                        revealedWord = assignedWords.getOrNull(currentPlayerIndex)?.second ?: "Eroare"
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
                            onGameStart()
                        }
                    }) {
                        Text("Next")
                    }
                }
            }
        )
    }
}
