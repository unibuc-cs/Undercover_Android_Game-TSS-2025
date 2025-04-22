package com.example.undercover.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.undercover.data.Player

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    players: List<Player>,
    onGameEnd: () -> Unit,
    onResetWords: () -> Unit,
    onNavigateToPlayers: () -> Unit
) {
    BackHandler(enabled = true) {}

    // State management
    val activePlayers = remember { mutableStateListOf(*players.toTypedArray()) }
    val eliminatedPlayers = remember { mutableStateListOf<Player>() }

    var mrWhiteGuess by remember { mutableStateOf("") }
    var showGuessDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var guessFeedback by remember { mutableStateOf("") }
    var showEliminationConfirmation by remember { mutableStateOf(false) }
    var showNavigationConfirmation by remember { mutableStateOf(false) }
    var showResetConfirmation by remember { mutableStateOf(false) }
    var playerToEliminate by remember { mutableStateOf<Player?>(null) }
    var isForgetfulMode by remember { mutableStateOf(false) }
    var selectedPlayerForHint by remember { mutableStateOf<Player?>(null) }

    // Game status tracking
    val undercoverCount = activePlayers.count { it.role == "Undercover" }
    val civilianCount = activePlayers.count { it.role == "Civil" }
    val mrWhiteCount = activePlayers.count { it.role == "Mr. White" }
    val isGameRunning = activePlayers.size > 2 && undercoverCount < civilianCount + mrWhiteCount

    var startingPlayer by remember {
        mutableStateOf(activePlayers.filter { it.role != "Mr. White" }.randomOrNull())
    }

    // Game status summary for dashboard
    val gameStatus = when {
        !isGameRunning && activePlayers.size <= 2 -> "Joc terminat: Prea puțini jucători rămași!"
        !isGameRunning && undercoverCount >= civilianCount + mrWhiteCount ->
            "Joc terminat: Undercoverul a câștigat!"

        else -> "Joc în desfășurare"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Undercover Game", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Game status dashboard
                GameStatusDashboard(
                    gameStatus = gameStatus,
                    civilianCount = civilianCount,
                    undercoverCount = undercoverCount,
                    mrWhiteCount = mrWhiteCount
                )

                // Starting player card with improved styling
                StartingPlayerCard(startingPlayer)

                // Active players with animated cards
                ActivePlayersCard(
                    activePlayers = activePlayers,
                    isGameRunning = isGameRunning,
                    onPlayerClick = { player ->
                        playerToEliminate = player
                        showEliminationConfirmation = true
                    }
                )

                // Eliminated players section
                if (eliminatedPlayers.isNotEmpty()) {
                    EliminatedPlayersCard(eliminatedPlayers)
                }

                // Forgetful mode control
                ForgetfulModeToggle(
                    isForgetfulMode = isForgetfulMode,
                    onToggleChanged = { isForgetfulMode = it }
                )

                // Game control buttons
                GameControlButtons(
                    isGameRunning = isGameRunning,
                    onResetClick = { showResetConfirmation = true },
                    onBackToPlayersClick = { showNavigationConfirmation = true },
                    onGameEndClick = onGameEnd
                )
            }
        }
    )

    // Mr. White guess dialog
    if (showGuessDialog) {
        MrWhiteGuessDialog(
            mrWhiteGuess = mrWhiteGuess,
            onGuessChange = { mrWhiteGuess = it },
            onSubmit = {
                showGuessDialog = false
                val civilianWord = activePlayers.find { it.role == "Civil" }?.word
                val isCorrectGuess = civilianWord?.let {
                    areWordsSimilar(mrWhiteGuess, it)
                } ?: false

                guessFeedback = if (isCorrectGuess) {
                    "Felicitări! Mr. White a ghicit corect și câștigă jocul!"
                } else {
                    "Mr. White a greșit! Jocul continuă."
                }
                mrWhiteGuess = ""
                showFeedbackDialog = true
            }
        )
    }

    // Feedback dialog
    if (showFeedbackDialog) {
        AlertDialog(
            onDismissRequest = { showFeedbackDialog = false },
            title = { Text("Rezultat", fontWeight = FontWeight.Bold) },
            text = { Text(guessFeedback) },
            confirmButton = {
                Button(
                    onClick = {
                        showFeedbackDialog = false
                        if (guessFeedback.contains("câștigă")) {
                            onGameEnd()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Elimination confirmation dialog
    if (showEliminationConfirmation) {
        AlertDialog(
            onDismissRequest = { showEliminationConfirmation = false },
            title = { Text("Elimină jucător", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Ești sigur că vrei să elimini acest jucător?")
                    Text(
                        text = playerToEliminate?.name ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        playerToEliminate?.let { player ->
                            activePlayers.remove(player)
                            eliminatedPlayers.add(player)
                            if (player == startingPlayer) {
                                startingPlayer =
                                    activePlayers.filter { it.role != "Mr. White" }.randomOrNull()
                            }
                            if (player.role == "Mr. White") {
                                showGuessDialog = true
                            }
                        }
                        playerToEliminate = null
                        showEliminationConfirmation = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Elimină")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showEliminationConfirmation = false
                        playerToEliminate = null
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Anulează")
                }
            }
        )
    }

    // Forgetful mode player selection dialog
    if (isForgetfulMode && activePlayers.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { isForgetfulMode = false },
            title = { Text("Afișează un cuvânt", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Alege un jucător pentru a vedea cuvântul său:",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(activePlayers) { player ->
                            Button(
                                onClick = {
                                    selectedPlayerForHint = player
                                    isForgetfulMode = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Text(player.name, fontSize = 16.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = { isForgetfulMode = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Închide")
                }
            }
        )
    }

    // Show player's word dialog
    selectedPlayerForHint?.let { player ->
        AlertDialog(
            onDismissRequest = { selectedPlayerForHint = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cuvântul jucătorului", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Jucător: ${player.name}", fontWeight = FontWeight.Medium)
                    Text("Rol: ${player.role}", fontWeight = FontWeight.Medium)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                    Text(
                        text = player.word,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedPlayerForHint = null },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Navigation confirmation dialog
    if (showNavigationConfirmation) {
        AlertDialog(
            onDismissRequest = { showNavigationConfirmation = false },
            title = { Text("Atenție", fontWeight = FontWeight.Bold) },
            text = {
                Text("Ești sigur că vrei să te întorci la ecranul jucătorilor? Progresul jocului se va pierde.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNavigationConfirmation = false
                        onNavigateToPlayers()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Da, înapoi la jucători")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showNavigationConfirmation = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Anulează")
                }
            }
        )
    }

    // Reset confirmation dialog
    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Resetare cuvinte", fontWeight = FontWeight.Bold) },
            text = {
                Text("Ești sigur că vrei să schimbi cuvintele atribuite jucătorilor?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmation = false
                        onResetWords()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Da, schimbă cuvintele")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showResetConfirmation = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Anulează")
                }
            }
        )
    }
}

@Composable
fun GameStatusDashboard(
    gameStatus: String,
    civilianCount: Int,
    undercoverCount: Int,
    mrWhiteCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = gameStatus,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (gameStatus.contains("terminat"))
                    MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RoleCountBadge("Civili", civilianCount, MaterialTheme.colorScheme.primary)
                RoleCountBadge("Undercover", undercoverCount, MaterialTheme.colorScheme.tertiary)
                RoleCountBadge("Mr. White", mrWhiteCount, MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
fun RoleCountBadge(role: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = role,
            fontSize = 14.sp
        )
    }
}

@Composable
fun StartingPlayerCard(startingPlayer: Player?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Jucătorul care începe:",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = startingPlayer?.name ?: "Niciun jucător",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ActivePlayersCard(
    activePlayers: List<Player>,
    isGameRunning: Boolean,
    onPlayerClick: (Player) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Jucători activi:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            LazyColumn(
                modifier = Modifier.height(300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(activePlayers) { player ->
                    Button(
                        onClick = { onPlayerClick(player) },
                        enabled = isGameRunning,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = player.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            if (!isGameRunning) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Jocul s-a încheiat. Nu mai poți elimina jucători.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EliminatedPlayersCard(eliminatedPlayers: List<Player>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Jucători eliminați:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            LazyColumn(
                modifier = Modifier.height(120.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(eliminatedPlayers) { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(player.name, fontWeight = FontWeight.Medium)
                        Text(
                            text = player.role,
                            color = when (player.role) {
                                "Civil" -> MaterialTheme.colorScheme.primary
                                "Undercover" -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.secondary
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ForgetfulModeToggle(
    isForgetfulMode: Boolean,
    onToggleChanged: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Modul Uituc",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Activează pentru a vedea cuvintele jucătorilor",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                )
            }
            Switch(
                checked = isForgetfulMode,
                onCheckedChange = onToggleChanged
            )
        }
    }
}

@Composable
fun GameControlButtons(
    isGameRunning: Boolean,
    onResetClick: () -> Unit,
    onBackToPlayersClick: () -> Unit,
    onGameEndClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onResetClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Resetează Cuvintele", fontSize = 16.sp)
            }
        }

        Button(
            onClick = onBackToPlayersClick,
            enabled = isGameRunning,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Înapoi la Jucători", fontSize = 16.sp)
            }
        }

        if (!isGameRunning) {
            Button(
                onClick = onGameEndClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text("Jocul s-a terminat - Înapoi la Start", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun MrWhiteGuessDialog(
    mrWhiteGuess: String,
    onGuessChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                "Mr. White are o șansă!",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "Mr. White poate câștiga dacă ghicește cuvântul civililor:",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = mrWhiteGuess,
                    onValueChange = onGuessChange,
                    label = { Text("Introdu răspunsul") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = mrWhiteGuess.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Trimite răspunsul")
            }
        }
    )
}

// Păstrăm funcțiile utilitare existente
fun levenshteinDistance(s: String, t: String): Int {
    val m = s.length
    val n = t.length
    val dp = Array(m + 1) { IntArray(n + 1) }

    for (i in 0..m) {
        dp[i][0] = i
    }
    for (j in 0..n) {
        dp[0][j] = j
    }

    for (i in 1..m) {
        for (j in 1..n) {
            val cost = if (s[i - 1] == t[j - 1]) 0 else 1
            dp[i][j] = minOf(
                dp[i - 1][j] + 1,      // Ștergere
                dp[i][j - 1] + 1,      // Inserare
                dp[i - 1][j - 1] + cost // Înlocuire
            )
        }
    }
    return dp[m][n]
}

fun areWordsSimilar(word1: String, word2: String, threshold: Double = 0.3): Boolean {
    val w1 = word1.trim().lowercase()
    val w2 = word2.trim().lowercase()

    // Dacă ambele cuvinte sunt goale, le considerăm similare
    if (w1.isEmpty() && w2.isEmpty()) return true

    val distance = levenshteinDistance(w1, w2)
    val maxLength = maxOf(w1.length, w2.length)

    // Calculăm raportul dintre distanța Levenshtein și lungimea maximă
    val normalizedDistance = distance.toDouble() / maxLength

    // Dacă raportul este sub sau egal cu pragul, considerăm că cuvintele sunt similare
    return normalizedDistance <= threshold
}