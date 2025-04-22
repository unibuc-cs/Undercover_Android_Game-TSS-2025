package com.example.undercover.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.undercover.R


@Composable
fun MainScreen(onStartGame: (Int, Boolean) -> Unit) {
    var numPlayers by remember { mutableStateOf("") }
    var is18PlusEnabled by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_undercover_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Undercover",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Ghiciți cine nu e din grupul vostru!",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = numPlayers,
            onValueChange = {
                if (it.length <= 2) numPlayers = it.filter { c -> c.isDigit() }
                showError = false
            },
            label = { Text("Numărul de jucători") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Numărul de jucători"
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = is18PlusEnabled,
                onCheckedChange = { is18PlusEnabled = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "Activează cuvinte 18+",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val players = numPlayers.toIntOrNull() ?: 0
                if (players in 3..20) {
                    onStartGame(players, is18PlusEnabled)
                } else {
                    showError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Text(
                text = "Începe Jocul",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        AnimatedVisibility(
            visible = showError,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Text(
                text = "Introdu un număr între 3 și 20!",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}