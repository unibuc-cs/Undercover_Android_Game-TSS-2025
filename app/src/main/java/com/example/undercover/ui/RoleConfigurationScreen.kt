package com.example.undercover.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.focus.onFocusChanged
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
    // Valorile implicite se bazează pe numărul total de jucători
    var numUndercover by remember { mutableIntStateOf(maxOf(1, (players.size * 0.2).toInt())) }
    var numUndercoverText by remember { mutableStateOf(numUndercover.toString()) }

    var numMrWhite by remember {
        mutableIntStateOf(
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

    // Verificări suplimentare:
    // 1. Trebuie să existe cel puțin 1 Undercover.
    // 2. Numărul de Mr. White nu poate fi negativ.
    // 3. Suma Undercover + Mr. White trebuie să fie mai mică decât numărul total de jucători.
    // 4. Numărul de Undercover trebuie să fie mai mic decât numărul de Civili.
    val errorMessage = when {
        numUndercover < 1 ->
            "Trebuie să existe cel puțin 1 Undercover."

        numMrWhite < 0 ->
            "Numărul de Mr. White nu poate fi negativ."

        sumSpecialRoles >= totalPlayers ->
            "Suma Undercover + Mr. White trebuie să fie mai mică decât numărul total de jucători!"

        numUndercover >= numCivils ->
            "Numărul de Undercover trebuie să fie mai mic decât numărul de Civili!"

        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configurare Roluri",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = numUndercoverText,
            onValueChange = { input ->
                numUndercoverText = input
                numUndercover = input.toIntOrNull() ?: 1
            },
            label = { Text("Număr Undercover") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        numUndercoverText = ""
                    }
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = numMrWhiteText,
            onValueChange = { input ->
                numMrWhiteText = input
                numMrWhite = input.toIntOrNull() ?: 0
            },
            label = { Text("Număr Mr. White") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        numMrWhiteText = ""
                    }
                }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Afișează feedback cu privire la numărul de Civili
        Text(
            text = "Număr Civili: $numCivils",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        )

        // Spacer-ul cu weight(1f) împinge conținutul de sus, menținând butonul în partea de jos
        Spacer(modifier = Modifier.weight(1f))

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = { onRolesConfigured(players, numUndercover, numMrWhite) },
            enabled = errorMessage == null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text("Confirmă", fontSize = 18.sp)
        }
    }
}
