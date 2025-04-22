package com.example.undercover.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.undercover.data.Player

@Composable
fun RoleConfigurationScreen(
    players: List<Player>,
    is18Plus: Boolean,
    onRolesConfigured: (List<Player>, Int, Int) -> Unit
) {
    var numUndercover by remember { mutableStateOf(maxOf(1, (players.size * 0.2).toInt())) }
    var numUndercoverText by remember { mutableStateOf(numUndercover.toString()) }

    var numMrWhite by remember {
        mutableStateOf(
            when {
                players.size >= 11 -> 3
                players.size >= 8 -> 2
                players.size >= 5 -> 1
                else -> 0
            }
        )
    }
    var numMrWhiteText by remember { mutableStateOf(numMrWhite.toString()) }

    val totalPlayers = players.size
    val sumSpecialRoles = numUndercover + numMrWhite
    val numCivils = totalPlayers - sumSpecialRoles

    val errorMessage = when {
        numUndercover < 1 -> "Trebuie să existe cel puțin 1 Undercover."
        numMrWhite < 0 -> "Numărul de Mr. White nu poate fi negativ."
        sumSpecialRoles >= totalPlayers -> "Prea mulți jucători speciali! Maxim ${totalPlayers - 1}."
        numUndercover >= numCivils -> "Prea mulți Undercover! Maxim ${numCivils - 1}."
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configurare Roluri",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Alege numărul de jucători speciali",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Undercover Input
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = numUndercoverText,
                onValueChange = { input ->
                    numUndercoverText = input
                    numUndercover = input.toIntOrNull() ?: 1
                },
                label = { Text("Număr Undercover") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                supportingText = {
                    Text("Recomandat: ${maxOf(1, (totalPlayers * 0.2).toInt())}")
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mr. White Input
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = numMrWhiteText,
                onValueChange = { input ->
                    numMrWhiteText = input
                    numMrWhite = input.toIntOrNull() ?: 0
                },
                label = { Text("Număr Mr. White") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                supportingText = {
                    Text(
                        text = when {
                            totalPlayers >= 11 -> "Recomandat: 3"
                            totalPlayers >= 8 -> "Recomandat: 2"
                            totalPlayers >= 5 -> "Recomandat: 1"
                            else -> "Opțional"
                        }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Role Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Undercover:", fontWeight = FontWeight.Medium)
                    Text("$numUndercover")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mr. White:", fontWeight = FontWeight.Medium)
                    Text("$numMrWhite")
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Civili:", fontWeight = FontWeight.Bold)
                    Text("$numCivils", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (errorMessage != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
        }

        Button(
            onClick = { onRolesConfigured(players, numUndercover, numMrWhite) },
            enabled = errorMessage == null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text("Confirmă Rolurile", fontSize = 18.sp)
        }
    }
}