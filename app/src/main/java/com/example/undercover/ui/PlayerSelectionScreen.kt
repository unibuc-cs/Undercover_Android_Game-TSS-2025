package com.example.undercover.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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

@Composable
fun PlayerSelectionScreen(players: List<Player>, onPlayersSet: (List<Player>) -> Unit) {
    var updatedPlayers by remember { mutableStateOf(players) }
    var selectedCardIndex by remember { mutableIntStateOf(-1) }
    var inputName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Introduceți numele jucătorilor", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(20.dp))

        updatedPlayers.forEachIndexed { index, player ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        selectedCardIndex = index
                        inputName = player.name
                    },
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

            if (selectedCardIndex == index) {
                OutlinedTextField(
                    value = inputName,
                    onValueChange = { inputName = it },
                    label = { Text("Introduceți numele") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (inputName.isNotEmpty()) {
                            updatedPlayers = updatedPlayers.toMutableList()
                                .also { it[index] = player.copy(name = inputName) }
                            selectedCardIndex = -1
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

        Button(
            onClick = { onPlayersSet(updatedPlayers) },
            enabled = updatedPlayers.all { it.name.isNotEmpty() }
        ) {
            Text("Începe jocul")
        }
    }
}


