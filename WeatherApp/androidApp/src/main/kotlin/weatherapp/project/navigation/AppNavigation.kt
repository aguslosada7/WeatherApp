package weatherapp.project.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import weatherapp.project.presentation.home.HomeScreen
import weatherapp.project.presentation.search.SearchScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToSearch = {
                    navController.navigate("search")
                }
            )
        }
        composable("search") {
            SearchScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
