package com.example.undercover.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.undercover.data.Player
import com.example.undercover.data.WordGenerator

@Composable
fun RoleAssignmentScreen(
    players: List<Player>,
    is18Plus: Boolean,
    numUndercover: Int,
    numMrWhite: Int,
    onGameStart: (List<Player>) -> Unit
) {
    BackHandler(enabled = true) { /* Disabled back navigation */ }

    val context = LocalContext.current
    val wordGenerator = remember { WordGenerator(context) }
    var assignedPlayers by remember { mutableStateOf(emptyList<Player>()) }
    var currentPlayerIndex by remember { mutableIntStateOf(0) }
    var showPopup by remember { mutableStateOf(true) }
    var revealedWord by remember { mutableStateOf<String?>(null) }
    var showRoleInfo by remember { mutableStateOf(false) }

    LaunchedEffect(players) {
        assignedPlayers = assignRoles(players, wordGenerator, is18Plus, numUndercover, numMrWhite)
        showPopup = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        // Background pattern or image could be added here

        if (showPopup && assignedPlayers.isNotEmpty()) {
            Dialog(
                onDismissRequest = { /* Dialog cannot be dismissed */ },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "E rândul lui",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        Text(
                            text = assignedPlayers[currentPlayerIndex].name,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = revealedWord
                                        ?: "Apasă butonul pentru a vedea rolul și cuvântul tău",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (revealedWord == null) {
                                    revealedWord = """
                                        Rol: ${assignedPlayers[currentPlayerIndex].role}
                                        Cuvânt: ${assignedPlayers[currentPlayerIndex].word}
                                    """.trimIndent()
                                } else {
                                    revealedWord = null
                                    if (currentPlayerIndex < assignedPlayers.size - 1) {
                                        currentPlayerIndex++
                                    } else {
                                        showPopup = false
                                        onGameStart(assignedPlayers)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 4.dp
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (revealedWord == null) "Afișează Rolul" else "Următorul",
                                    fontSize = 18.sp
                                )
                                if (revealedWord != null) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Următorul"
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = { showRoleInfo = true }
                        ) {
                            Text("Informații despre roluri")
                        }
                    }
                }
            }
        }

        if (showRoleInfo) {
            AlertDialog(
                onDismissRequest = { showRoleInfo = false },
                title = { Text("Informații Roluri") },
                text = {
                    Column {
                        Text(
                            "• Civil: Știe cuvântul și trebuie să identifice Undercover",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "• Undercover: Are un cuvânt similar și trebuie să se ascundă",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "• Mr. White: Nu știe cuvântul și trebuie să-l ghicească",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { showRoleInfo = false }) {
                        Text("Am înțeles")
                    }
                }
            )
        }
    }
}

private fun assignRoles(
    players: List<Player>,
    wordGenerator: WordGenerator,
    is18Plus: Boolean,
    numUndercover: Int,
    numMrWhite: Int
): List<Player> {
    val totalPlayers = players.size
    val roles = mutableListOf<String>().apply {
        repeat(numMrWhite) { add("Mr. White") }
        repeat(numUndercover) { add("Undercover") }
        while (size < totalPlayers) {
            add("Civil")
        }
        shuffle()
    }

    val (civilianWord, undercoverWord) = wordGenerator.generateWords(is18Plus)

    return players.mapIndexed { index, player ->
        player.copy(
            role = roles[index],
            word = when (roles[index]) {
                "Civil" -> civilianWord
                "Undercover" -> undercoverWord
                "Mr. White" -> "Tu esti Mr. White"
                else -> "Error"
            }
        )
    }
}