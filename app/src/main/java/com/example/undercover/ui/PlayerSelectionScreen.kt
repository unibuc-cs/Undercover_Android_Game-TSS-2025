package com.example.undercover.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.undercover.data.Player

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerSelectionScreen(players: List<Player>, onPlayersSet: (List<Player>) -> Unit) {
    var updatedPlayers by remember { mutableStateOf(players) }
    var selectedCardIndex by remember { mutableIntStateOf(-1) }
    var inputName by remember { mutableStateOf("") }
    var showOptionsDialog by remember { mutableStateOf(false) }
    var selectedPlayerIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Introduceți numele jucătorilor", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(updatedPlayers.size) { index ->
                val player = updatedPlayers[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedCardIndex = index
                            inputName = player.name
                        }
                        .combinedClickable(
                            onClick = {
                                selectedCardIndex = index
                                inputName = player.name
                            },
                            onLongClick = {
                                selectedPlayerIndex = index
                                showOptionsDialog = true
                            }
                        ),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (player.name == "") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (player.name == "") "Jucător ${index + 1}" else player.name,
                            fontSize = 18.sp
                        )
                    }
                }

                if (selectedCardIndex == index && player.name == "") {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = inputName,
                            onValueChange = { inputName = it },
                            label = { Text("Introduceți numele") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    if (inputName.isNotEmpty()) {
                                        updatedPlayers = updatedPlayers.toMutableList()
                                            .also { it[index] = player.copy(name = inputName) }
                                        selectedCardIndex = -1
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .padding(top = 8.dp),
                                enabled = inputName.isNotEmpty()
                            ) {
                                Text("Confirmă")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                updatedPlayers = updatedPlayers + Player("", "", "")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary) // Culoare diferită
        ) {
            Text("+", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { onPlayersSet(updatedPlayers) },
            enabled = updatedPlayers.all { it.name.isNotEmpty() }
        ) {
            Text("Începe jocul")
        }
    }

    if (showOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showOptionsDialog = false },
            title = { Text("Opțiuni pentru ${updatedPlayers[selectedPlayerIndex].name}") },
            text = { Text("Ce dorești să faci?") },
            confirmButton = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            inputName = ""
                            updatedPlayers[selectedPlayerIndex].name = ""
                            selectedCardIndex = selectedPlayerIndex
                            showOptionsDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Editează")
                    }

                    Button(
                        onClick = {
                            updatedPlayers = updatedPlayers.toMutableList()
                                .also { it.removeAt(selectedPlayerIndex) }
                            showOptionsDialog = false
                        },
                        enabled = updatedPlayers.size > 3,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Șterge")
                    }

                    Button(
                        onClick = { showOptionsDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Anulează")
                    }
                }
            },
            dismissButton = {}
        )
    }

}



