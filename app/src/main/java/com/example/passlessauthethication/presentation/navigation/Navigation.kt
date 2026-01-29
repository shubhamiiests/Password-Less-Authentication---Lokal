package com.example.passlessauthethication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.passlessauthethication.domain.usecase.ManageSessionUseCase
import com.example.passlessauthethication.presentation.login.LoginScreen
import com.example.passlessauthethication.presentation.otp.OtpScreen
import com.example.passlessauthethication.presentation.session.SessionScreen
import kotlinx.coroutines.flow.first

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Otp : Screen("otp/{email}") {
        fun createRoute(email: String) = "otp/$email"
    }
    object Session : Screen("session")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    manageSessionUseCase: ManageSessionUseCase = hiltViewModel<NavigationViewModel>().manageSessionUseCase
) {
    // Check for existing session
    LaunchedEffect(Unit) {
        val session = manageSessionUseCase.getCurrentSession().first()
        if (session != null) {
            navController.navigate(Screen.Session.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToOtp = { email ->
                    navController.navigate(Screen.Otp.createRoute(email))
                }
            )
        }

        composable(
            route = Screen.Otp.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OtpScreen(
                email = email,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSession = {
                    navController.navigate(Screen.Session.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Session.route) {
            SessionScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Session.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

// Helper ViewModel for navigation dependency injection
@dagger.hilt.android.lifecycle.HiltViewModel
class NavigationViewModel @javax.inject.Inject constructor(
    val manageSessionUseCase: ManageSessionUseCase
) : androidx.lifecycle.ViewModel()