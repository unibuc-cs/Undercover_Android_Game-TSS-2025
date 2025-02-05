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
    var players by remember { mutableStateOf(List(numPlayers) { "" }) }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var inputName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Introduceți numele jucătorilor", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(20.dp))

        players.forEachIndexed { index, name ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { selectedIndex = index },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (name.isEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            ) {
                Box(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (name.isEmpty()) "Jucător ${index + 1}" else name, fontSize = 18.sp)
                }
            }

            // Afișează input field doar pentru cardul selectat
            if (selectedIndex == index) {
                OutlinedTextField(
                    value = inputName,
                    onValueChange = { inputName = it },
                    label = { Text("Introduceți numele") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )

                Button(
                    onClick = {
                        if (inputName.isNotEmpty()) {
                            players = players.toMutableList().also { it[index] = inputName }
                            selectedIndex = -1
                            inputName = ""
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp),
                    enabled = inputName.isNotEmpty()
                ) {
                    Text("Confirmă")
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
