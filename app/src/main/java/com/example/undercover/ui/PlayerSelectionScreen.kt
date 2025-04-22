package com.example.undercover.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.undercover.data.Player

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerSelectionScreen(players: List<Player>, onPlayersSet: (List<Player>) -> Unit) {
    BackHandler(enabled = true) { /* Disabled back navigation */ }

    var updatedPlayers by remember { mutableStateOf(players) }
    var selectedCardIndex by remember { mutableStateOf(-1) }
    var inputName by remember { mutableStateOf("") }
    var showOptionsDialog by remember { mutableStateOf(false) }
    var selectedPlayerIndex by remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Introduceți numele jucătorilor",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Apasă lung pentru a edita sau șterge",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(updatedPlayers) { index, player ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                if (player.name.isEmpty()) {
                                    selectedCardIndex = index
                                    inputName = player.name
                                }
                            },
                            onLongClick = {
                                if (player.name.isNotEmpty()) {
                                    selectedPlayerIndex = index
                                    showOptionsDialog = true
                                }
                            }
                        )
                        .animateItemPlacement(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (player.name.isEmpty())
                            MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = if (player.name.isEmpty())
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (player.name.isEmpty()) "Jucător ${index + 1}" else player.name,
                            fontSize = 18.sp,
                            fontWeight = if (player.name.isEmpty()) FontWeight.Normal else FontWeight.Medium
                        )

                        if (player.name.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editable",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                if (selectedCardIndex == index && player.name.isEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = inputName,
                            onValueChange = { inputName = it },
                            label = { Text("Introduceți numele") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            trailingIcon = {
                                if (inputName.isNotEmpty()) {
                                    IconButton(onClick = { inputName = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear"
                                        )
                                    }
                                }
                            }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    if (inputName.isNotEmpty()) {
                                        updatedPlayers = updatedPlayers.toMutableList()
                                            .also { it[index] = player.copy(name = inputName) }
                                        selectedCardIndex = -1
                                        inputName = ""
                                    }
                                },
                                modifier = Modifier.padding(top = 8.dp),
                                enabled = inputName.isNotEmpty(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Confirmă")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    updatedPlayers = updatedPlayers + Player("", "", "")
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adaugă jucător")
            }

            Button(
                onClick = { onPlayersSet(updatedPlayers) },
                enabled = updatedPlayers.size >= 3 && updatedPlayers.all { it.name.isNotEmpty() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Începe jocul")
            }
        }
    }

    if (showOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showOptionsDialog = false },
            title = {
                Text(
                    text = updatedPlayers[selectedPlayerIndex].name,
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text("Ce dorești să faci cu acest jucător?") },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            inputName = updatedPlayers[selectedPlayerIndex].name
                            updatedPlayers = updatedPlayers.toMutableList().also {
                                it[selectedPlayerIndex] = it[selectedPlayerIndex].copy(name = "")
                            }
                            selectedCardIndex = selectedPlayerIndex
                            showOptionsDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Editează")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editează")
                        }
                    }

                    Button(
                        onClick = {
                            updatedPlayers = updatedPlayers.toMutableList()
                                .also { it.removeAt(selectedPlayerIndex) }
                            showOptionsDialog = false
                        },
                        enabled = updatedPlayers.size > 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Șterge")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Șterge")
                        }
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showOptionsDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Anulează")
                }
            }
        )
    }
}