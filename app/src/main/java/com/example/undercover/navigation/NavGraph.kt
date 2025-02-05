package com.example.undercover.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.undercover.ui.GameScreen
import com.example.undercover.ui.MainScreen
import com.example.undercover.ui.PlayerSelectionScreen
import com.example.undercover.ui.RoleAssignmentScreen

sealed class Screen(val route: String) {
    data object Main : Screen("main_screen")
    data object PlayerSelection : Screen("player_selection_screen/{numPlayers}/{is18Plus}") {
        fun createRoute(numPlayers: Int, is18Plus: Boolean) =
            "player_selection_screen/$numPlayers/$is18Plus"
    }

    data object RoleAssignment : Screen("role_assignment_screen/{players}/{is18Plus}") {
        fun createRoute(players: List<String>, is18Plus: Boolean): String {
            val encodedPlayers = players.joinToString(",") // Transformă lista într-un string
            return "role_assignment_screen/$encodedPlayers/$is18Plus"
        }
    }

    data object Game : Screen("game_screen/{players}") {
        fun createRoute(players: Map<String, String>): String {
            val encodedPlayers = players.entries.joinToString(";") { "${it.key},${it.value}" }
            return "game_screen/$encodedPlayers"
        }
    }
}


@Composable
fun NavGraph(startDestination: String = Screen.Main.route) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Main.route) {
            MainScreen { numPlayers, is18Plus ->
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
                navController.navigate(Screen.RoleAssignment.createRoute(players, is18Plus))
            }
        }

        composable(
            route = Screen.RoleAssignment.route,
            arguments = listOf(
                navArgument("players") { type = NavType.StringType },
                navArgument("is18Plus") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val playersString = backStackEntry.arguments?.getString("players") ?: ""
            val players = playersString.split(",").map { it.trim() }
            val is18Plus = backStackEntry.arguments?.getBoolean("is18Plus") ?: false

            RoleAssignmentScreen(players, is18Plus) { assignedPlayers ->
                navController.navigate(Screen.Game.createRoute(assignedPlayers))
            }
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("players") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val playersString = backStackEntry.arguments?.getString("players") ?: ""
            val players = playersString.split(";").associate {
                val (name, role) = it.split(",")
                name to role
            }

            GameScreen(players) {
                navController.navigate(Screen.Main.route) // Revine la ecranul principal după joc
            }
        }


    }
}

