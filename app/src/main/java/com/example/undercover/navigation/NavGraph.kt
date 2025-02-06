package com.example.undercover.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.undercover.data.Player
import com.example.undercover.ui.GameScreen
import com.example.undercover.ui.MainScreen
import com.example.undercover.ui.PlayerSelectionScreen
import com.example.undercover.ui.RoleAssignmentScreen
import com.example.undercover.ui.RoleConfigurationScreen
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {
    data object Main : Screen("main_screen")
    data object PlayerSelection : Screen("player_selection_screen/{players}/{is18Plus}") {
        fun createRoute(players: List<Player>, is18Plus: Boolean): String {
            val encodedPlayers = Gson().toJson(players)
            return "player_selection_screen/${URLEncoder.encode(encodedPlayers, "UTF-8")}/$is18Plus"
        }
    }

    data object RoleConfiguration : Screen("role_configuration_screen/{players}/{is18Plus}") {
        fun createRoute(players: List<Player>, is18Plus: Boolean): String {
            val encodedPlayers = Gson().toJson(players)
            return "role_configuration_screen/${
                URLEncoder.encode(
                    encodedPlayers,
                    "UTF-8"
                )
            }/$is18Plus"
        }
    }

    data object RoleAssignment :
        Screen("role_assignment_screen/{players}/{is18Plus}/{numUndercover}/{numMrWhite}") {
        fun createRoute(
            players: List<Player>,
            is18Plus: Boolean,
            numUndercover: Int,
            numMrWhite: Int
        ): String {
            val encodedPlayers = Gson().toJson(players)
            return "role_assignment_screen/${
                URLEncoder.encode(
                    encodedPlayers,
                    "UTF-8"
                )
            }/$is18Plus/$numUndercover/$numMrWhite"
        }
    }

    data object Game : Screen("game_screen/{players}") {
        fun createRoute(players: List<Player>): String {
            val encodedPlayers = Gson().toJson(players)
            return "game_screen/${URLEncoder.encode(encodedPlayers, "UTF-8")}"
        }
    }
}

@Composable
fun NavGraph(startDestination: String = Screen.Main.route) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Main.route) {
            MainScreen { numPlayers, is18Plus ->
                val players = List(numPlayers) { Player("", "", "") }
                navController.navigate(Screen.PlayerSelection.createRoute(players, is18Plus))
            }
        }

        composable(
            route = Screen.PlayerSelection.route,
            arguments = listOf(
                navArgument("players") { type = NavType.StringType },
                navArgument("is18Plus") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val playersJson = backStackEntry.arguments?.getString("players") ?: "[]"
            val decodedPlayersJson = URLDecoder.decode(playersJson, "UTF-8")
            val playersType = object : TypeToken<List<Player>>() {}.type
            val players: List<Player> = Gson().fromJson(decodedPlayersJson, playersType)
            val is18Plus = backStackEntry.arguments?.getBoolean("is18Plus") ?: false

            PlayerSelectionScreen(
                players = players,
                onPlayersSet = { updatedPlayers ->
                    navController.navigate(
                        Screen.RoleConfiguration.createRoute(
                            updatedPlayers,
                            is18Plus
                        )
                    )
                }
            )
        }

        composable(
            route = Screen.RoleConfiguration.route,
            arguments = listOf(
                navArgument("players") { type = NavType.StringType },
                navArgument("is18Plus") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val playersJson = backStackEntry.arguments?.getString("players") ?: "[]"
            val decodedPlayersJson = URLDecoder.decode(playersJson, "UTF-8")
            val playersType = object : TypeToken<List<Player>>() {}.type
            val players: List<Player> = Gson().fromJson(decodedPlayersJson, playersType)
            val is18Plus = backStackEntry.arguments?.getBoolean("is18Plus") ?: false

            RoleConfigurationScreen(
                players = players,
                is18Plus = is18Plus,
                onRolesConfigured = { updatedPlayers, numUndercover, numMrWhite ->
                    navController.navigate(
                        Screen.RoleAssignment.createRoute(
                            updatedPlayers,
                            is18Plus,
                            numUndercover,
                            numMrWhite
                        )
                    )
                }
            )
        }

        composable(
            route = Screen.RoleAssignment.route,
            arguments = listOf(
                navArgument("players") { type = NavType.StringType },
                navArgument("is18Plus") { type = NavType.BoolType },
                navArgument("numUndercover") { type = NavType.IntType },
                navArgument("numMrWhite") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val playersJson = backStackEntry.arguments?.getString("players") ?: "[]"
            val decodedPlayersJson = URLDecoder.decode(playersJson, "UTF-8")
            val playersType = object : TypeToken<List<Player>>() {}.type
            val players: List<Player> = Gson().fromJson(decodedPlayersJson, playersType)
            val is18Plus = backStackEntry.arguments?.getBoolean("is18Plus") ?: false
            val numUndercover = backStackEntry.arguments?.getInt("numUndercover") ?: 1
            val numMrWhite = backStackEntry.arguments?.getInt("numMrWhite") ?: 0

            RoleAssignmentScreen(
                players = players,
                is18Plus = is18Plus,
                numUndercover = numUndercover,
                numMrWhite = numMrWhite,
                onGameStart = { assignedPlayers ->
                    navController.navigate(Screen.Game.createRoute(assignedPlayers))
                }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("players") { type = NavType.StringType })
        ) { backStackEntry ->
            val playersJson = backStackEntry.arguments?.getString("players") ?: "[]"
            val decodedJson = URLDecoder.decode(playersJson, "UTF-8")
            val playersType = object : TypeToken<List<Player>>() {}.type
            val players: List<Player> = Gson().fromJson(decodedJson, playersType)
            val numUndercover = players.count { it.role == "Undercover" }
            val numMrWhite = players.count { it.role == "Mr. White" }

            GameScreen(
                players = players,
                onGameEnd = {
                    navController.navigate(Screen.PlayerSelection.createRoute(players, false))
                },
                onResetWords = {
                    navController.navigate(
                        Screen.RoleAssignment.createRoute(
                            players,
                            false,
                            numUndercover,
                            numMrWhite
                        )
                    )
                },
                onNavigateToPlayers = {
                    navController.navigate(Screen.PlayerSelection.createRoute(players, false))
                }
            )
        }
    }
}
