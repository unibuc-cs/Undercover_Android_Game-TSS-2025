package com.example.undercover.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(onStartGame: (Int, Boolean) -> Unit) {
    var numPlayers by remember { mutableStateOf("") }
    var is18PlusEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Undercover Game", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = numPlayers,
            onValueChange = { numPlayers = it },
            label = { Text("Number of Players") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = is18PlusEnabled, onCheckedChange = { is18PlusEnabled = it })
            Text("Enable 18+ Words")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val players = numPlayers.toIntOrNull() ?: 0
                if (players > 2) {
                    onStartGame(players, is18PlusEnabled)
                }
            },
            enabled = numPlayers.toIntOrNull()?.let { it > 2 } ?: false
        ) {
            Text("Start Game")
        }
    }
}

