package com.example.undercover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.undercover.navigation.NavGraph
import com.example.undercover.ui.theme.UndercoverTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UndercoverTheme {
                NavGraph()
            }
        }
    }
}
