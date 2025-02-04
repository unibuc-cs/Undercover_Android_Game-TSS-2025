package com.example.undercover.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.undercover.ui.MainScreen
import com.example.undercover.ui.PlayerSelectionScreen
import com.example.undercover.ui.WordAssignmentScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Main : Screen("main_screen")
    object PlayerSelection : Screen("player_selection_screen/{numPlayers}/{is18Plus}") {
        fun createRoute(numPlayers: Int, is18Plus: Boolean) =
            "player_selection_screen/$numPlayers/$is18Plus"
    }

    object WordAssignment : Screen("word_assignment_screen/{players}/{is18Plus}") {
        fun createRoute(players: List<String>, is18Plus: Boolean): String {
            val encodedPlayers =
                URLEncoder.encode(players.joinToString(","), StandardCharsets.UTF_8.toString())
            return "word_assignment_screen/$encodedPlayers/$is18Plus"
        }
    }
}

@Composable
fun NavGraph(startDestination: String = Screen.Main.route) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Main.route) {
            MainScreen { numPlayers, is18Plus ->
                Log.d(
                    "NavGraph",
                    "Navigating to PlayerSelectionScreen with numPlayers: $numPlayers, is18Plus: $is18Plus"
                )
                navController.navigate(Screen.PlayerSelection.createRoute(numPlayers, is18Plus))
            }
        }

        composable(
            route = Screen.PlayerSelection.route,
            arguments = listOf(
                navArgument("numPlayers") { type = NavType.IntType },
                navArgument("is18Plus") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val numPlayers = backStackEntry.arguments?.getInt("numPlayers") ?: 0
            val is18Plus = backStackEntry.arguments?.getBoolean("is18Plus") ?: false

            PlayerSelectionScreen(numPlayers) { players ->
                navController.navigate(Screen.WordAssignment.createRoute(players, is18Plus))
            }
        }

        composable(
            route = Screen.WordAssignment.route,
            arguments = listOf(
                navArgument("players") { type = NavType.StringType },
                navArgument("is18Plus") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val playersString = backStackEntry.arguments?.getString("players") ?: ""
            val players = playersString.split(",").map { it.trim() }
            val is18Plus = backStackEntry.arguments?.getBoolean("is18Plus") ?: false
            Log.d(
                "NavGraph",
                "Navigating to WordAssignmentScreen with players: $players, is18Plus: $is18Plus"
            )

            WordAssignmentScreen(players, is18Plus) {
                Log.d("NavGraph", "Game is starting")
                // Poți naviga către următorul ecran aici dacă este necesar
            }
        }
    }
}
