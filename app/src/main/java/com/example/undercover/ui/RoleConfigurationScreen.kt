package com.example.undercover.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.undercover.data.Player

@Composable
fun RoleConfigurationScreen(
    players: List<Player>,
    is18Plus: Boolean,
    onRolesConfigured: (List<Player>, Int, Int) -> Unit
) {
    val totalPlayers = players.size
    val (minUndercover, maxUndercover) = calculateUndercoverRange(totalPlayers)
    val (minMrWhite, maxMrWhite) = calculateMrWhiteRange(totalPlayers)

    var numUndercover by remember { mutableStateOf(maxUndercover) }
    var numMrWhite by remember { mutableStateOf(maxMrWhite) }

    val sumSpecialRoles = numUndercover + numMrWhite
    val numCivils = totalPlayers - sumSpecialRoles

    val errorMessage = when {
        numUndercover < minUndercover -> "Trebuie să existe cel puțin $minUndercover Undercover."
        numMrWhite < minMrWhite -> "Numărul de Mr. White nu poate fi mai mic de $minMrWhite."
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

        Spacer(modifier = Modifier.height(32.dp))

        // Undercover Role Adjustment
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (numUndercover > minUndercover) numUndercover-- },
                enabled = numUndercover > minUndercover,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "-",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Undercover: $numUndercover",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { if (numUndercover < maxUndercover) numUndercover++ },
                enabled = numUndercover < maxUndercover,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "+",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Mr. White Role Adjustment
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (numMrWhite > minMrWhite) numMrWhite-- },
                enabled = numMrWhite > minMrWhite,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "-",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Mr. White: $numMrWhite",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { if (numMrWhite < maxMrWhite) numMrWhite++ },
                enabled = numMrWhite < maxMrWhite,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "+",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Role Summary Card
        RoleSummaryCard(
            numUndercover = numUndercover,
            numMrWhite = numMrWhite,
            numCivils = numCivils
        )

        Spacer(modifier = Modifier.weight(1f))

        if (errorMessage != null) {
            ErrorMessage(errorMessage)
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


@Composable
private fun RoleSummaryCard(
    numUndercover: Int,
    numMrWhite: Int,
    numCivils: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            RoleSummaryItem("Undercover:", numUndercover)
            RoleSummaryItem("Mr. White:", numMrWhite)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            RoleSummaryItem("Civili:", numCivils, isBold = true)
        }
    }
}

@Composable
private fun RoleSummaryItem(label: String, value: Int, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
        )
        Text(
            text = value.toString(),
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun calculateUndercoverRange(totalPlayers: Int): Pair<Int, Int> {
    return when {
        totalPlayers >= 10 -> 1 to 4
        totalPlayers >= 7 -> 1 to 3
        totalPlayers >= 5 -> 1 to 2
        else -> 1 to 1
    }
}

private fun calculateMrWhiteRange(totalPlayers: Int): Pair<Int, Int> {
    return when {
        totalPlayers >= 11 -> 1 to 3
        totalPlayers >= 8 -> 1 to 2
        totalPlayers >= 5 -> 0 to 1
        else -> 0 to 0
    }
}