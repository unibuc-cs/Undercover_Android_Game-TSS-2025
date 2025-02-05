package com.example.undercover.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlayerSelectionScreen(numPlayers: Int, onPlayersSet: (List<String>) -> Unit) {
    var players by remember { mutableStateOf(List(numPlayers) { "" }) }  // ✅ Gestionăm corect lista de stări
    var selectedCardIndex by remember { mutableIntStateOf(-1) }
    var inputName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Enter Player Names", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(20.dp))

        players.forEachIndexed { index, name ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable(enabled = name.isEmpty()) {
                        selectedCardIndex = index
                        inputName = ""  // ✅ Resetăm input-ul pentru fiecare player nou selectat
                    },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (name.isEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            ) {
                Box(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (name.isEmpty()) "Player ${index + 1}" else name, fontSize = 18.sp)
                }
            }

            // Afișează input field doar pentru cardul selectat
            if (selectedCardIndex == index) {
                OutlinedTextField(
                    value = inputName,
                    onValueChange = { inputName = it },
                    label = { Text("Enter Name") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (inputName.isNotEmpty()) {
                            players = players.toMutableList().also { it[index] = inputName }  // ✅ Actualizăm corect lista
                            selectedCardIndex = -1  // ✅ Resetăm selecția
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp),
                    enabled = inputName.isNotEmpty()
                ) {
                    Text("Confirm")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Butonul "Start Game" (activ doar când toți jucătorii au nume)
        Button(
            onClick = { onPlayersSet(players) },
            enabled = players.all { it.isNotEmpty() }
        ) {
            Text("Start Game")
        }
    }
}
