package com.example.whoossh.navigation

import android.content.Intent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.whoossh.ui.screens.*
import com.example.whoossh.model.Passenger
import com.example.whoossh.model.BookingData
import com.example.whoossh.viewmodel.BookingViewModel
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: BookingViewModel,
    deepLinkIntent: Intent? = null
) {
    val scope = rememberCoroutineScope()
    // Handle deep link when intent changes
    LaunchedEffect(deepLinkIntent) {
        deepLinkIntent?.data?.let { uri ->
            android.util.Log.d("NavGraph", "Processing deep link: $uri")
            // Navigation will be handled by the deep link configuration in composable
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // ── SPLASH ───────────────────────────────────────────────────────────
        composable(
            route = Screen.Splash.route,
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            SplashScreen(
                onSplashFinished = {
                    // Hanya pindah ke Dashboard jika posisi terakhir masih di Splash Screen
                    // (Mencegah Splash menimpa navigasi Deep Link yang sudah terbuka)
                    if (navController.currentDestination?.route == Screen.Splash.route) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // ── LOGIN ────────────────────────────────────────────────────────────
        composable(
            route = Screen.Login.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(400))
            }
        ) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // ── REGISTER ─────────────────────────────────────────────────────────
        composable(
            route = Screen.Register.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(400))
            }
        ) {
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ── DASHBOARD ────────────────────────────────────────────────────────
        composable(
            route = Screen.Dashboard.route,
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            DashboardScreen(
                viewModel = viewModel,
                onSearchSchedule = {
                    if (viewModel.searchSchedules()) {
                        navController.navigate(Screen.SelectSchedule.route)
                    }
                },
                onLoginRequired = {
                    navController.navigate(Screen.Login.route)
                },
                onLogout = {
                    viewModel.logout()
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.TravelHistory.route)
                },
                onNavigateToPromo = {
                    navController.navigate(Screen.Promo.route)
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.NotificationSettings.route)
                },
                onNavigateToLanguage = {
                    navController.navigate(Screen.Language.route)
                },
                onNavigateToPrivacy = {
                    navController.navigate(Screen.PrivacySecurity.route)
                },
                onNavigateToChangePassword = {
                    navController.navigate(Screen.ChangePassword.route)
                },
                onNavigateToHelpCenter = {
                    navController.navigate(Screen.HelpCenter.route)
                },
                onNavigateToPassengerList = {
                    navController.navigate(Screen.PassengerList.route)
                },
                onNavigateToETicket = {
                    navController.navigate(Screen.ETicket.route)
                },
                onNavigateToUnpaidTicket = {
                    navController.navigate(Screen.UnpaidTicket.route)
                }
            )
        }

        // ── SELECT SCHEDULE ──────────────────────────────────────────────────
        composable(
            route = Screen.SelectSchedule.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            SelectScheduleScreen(
                viewModel = viewModel,
                onScheduleSelected = {
                    navController.navigate(Screen.SelectCoach.route)
                },
                onLoginRequired = {
                    navController.navigate(Screen.Login.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── SELECT COACH ─────────────────────────────────────────────────────
        composable(
            route = Screen.SelectCoach.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            SelectCoachScreen(
                viewModel = viewModel,
                onCoachSelected = {
                    navController.navigate(Screen.SelectSeat.route)
                },
                onManagePassengers = {
                    navController.navigate(Screen.PassengerList.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── PASSENGER LIST ───────────────────────────────────────────────────
        composable(
            route = Screen.PassengerList.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(300))
            }
        ) {
            PassengerListScreen(
                viewModel = viewModel,
                onDone = { navController.popBackStack() },
                onAddPassenger = {
                    navController.navigate(Screen.AddPassenger.route)
                },
                onEditPassenger = { passenger ->
                    navController.navigate("${Screen.EditPassenger.route}/${passenger.id}")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── ADD PASSENGER ────────────────────────────────────────────────────
        composable(
            route = Screen.AddPassenger.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            AddEditPassengerScreen(
                viewModel = viewModel,
                passenger = null,
                onSave = { navController.popBackStack() },
                onSelectCountry = { navController.navigate(Screen.SelectCountry.route) },
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }

        // ── EDIT PASSENGER ───────────────────────────────────────────────────
        composable(
            route = "${Screen.EditPassenger.route}/{passengerId}",
            arguments = listOf(
                navArgument("passengerId") {
                    type = NavType.StringType
                }
            ),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) { backStackEntry ->
            val passengerId = backStackEntry.arguments?.getString("passengerId")
            val passenger: Passenger? = passengerId?.let { id: String ->
                viewModel.getSelectedPassengerById(id) ?: viewModel.getSavedPassengerById(id)
            }
            
            AddEditPassengerScreen(
                viewModel = viewModel,
                passenger = passenger,
                onSave = { navController.popBackStack() },
                onSelectCountry = { navController.navigate(Screen.SelectCountry.route) },
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }

        // ── SELECT SEAT ──────────────────────────────────────────────────────
        composable(
            route = Screen.SelectSeat.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            SelectSeatScreen(
                viewModel = viewModel,
                onSeatSelected = {
                    scope.launch {
                        viewModel.confirmBooking(isPaid = false)
                        navController.navigate(Screen.UnpaidTicket.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── UNPAID TICKET ────────────────────────────────────────────────────
        composable(
            route = Screen.UnpaidTicket.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            UnpaidTicketScreen(
                viewModel = viewModel,
                onPay = {
                    navController.navigate(Screen.Payment.route)
                },
                onCancel = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                onReturnTrip = {
                    // Navigate back to dashboard with swapped stations
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        // ── PAYMENT ──────────────────────────────────────────────────────────
        composable(
            route = Screen.Payment.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(300))
            }
        ) {
            PaymentScreen(
                viewModel = viewModel,
                onPaymentSuccess = {
                    viewModel.markAsPaid()
                    navController.navigate(Screen.ETicket.route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── SUMMARY ──────────────────────────────────────────────────────────
        composable(
            route = Screen.Summary.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            // Keep Summary for compatibility or redirect to Payment
            PaymentScreen(
                viewModel = viewModel,
                onPaymentSuccess = {
                    scope.launch {
                        viewModel.confirmBooking(isPaid = true)
                        navController.navigate(Screen.ETicket.route) {
                            popUpTo(Screen.Dashboard.route)
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── E-TICKET ─────────────────────────────────────────────────────────
        composable(
            route = Screen.ETicket.route + "?bookingCode={bookingCode}",
            arguments = listOf(
                navArgument("bookingCode") {
                    type = NavType.StringType
                    nullable = true
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "whoossh://ticket/{bookingCode}" },
                navDeepLink { uriPattern = "https://whoosh.id/ticket/{bookingCode}" }
            ),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(500))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(400))
            }
        ) { backStackEntry ->
            val bookingCode = backStackEntry.arguments?.getString("bookingCode")
            val isFromDeepLink = bookingCode != null
            
            // Load ticket when deep link is opened
            LaunchedEffect(bookingCode) {
                bookingCode?.let { code ->
                    android.util.Log.d("NavGraph", "Deep link opened with booking code: $code")
                    
                    // Refresh tickets first to ensure we have latest data
                    if (viewModel.isLoggedIn && viewModel.userId > 0) {
                        android.util.Log.d("NavGraph", "User logged in, refreshing tickets...")
                        viewModel.refreshTickets()
                        // Wait a bit for refresh to complete
                        kotlinx.coroutines.delay(500)
                    }
                    
                    // Then load the specific ticket
                    viewModel.loadTicketByCode(code)
                }
            }

            ETicketScreen(
                viewModel = viewModel,
                onBackToDashboard = {
                    viewModel.resetBooking()
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onReschedule = {
                    // Navigate to reschedule flow (future implementation)
                    // For now, handled by dialog in ETicketScreen
                },
                onRefund = {
                    // Navigate to refund flow (future implementation)
                    // For now, handled by dialog in ETicketScreen
                },
                onAddInfant = {
                    // Navigate to add infant flow (future implementation)
                    // For now, handled by dialog in ETicketScreen
                },
                showQrOnEntry = isFromDeepLink
            )
        }

        // ── EDIT PROFILE ─────────────────────────────────────────────────────
        composable(
            route = Screen.EditProfile.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            EditProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ── CHANGE PASSWORD ──────────────────────────────────────────────────
        composable(
            route = Screen.ChangePassword.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            ChangePasswordScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ── TRAVEL HISTORY ───────────────────────────────────────────────────
        composable(
            route = Screen.TravelHistory.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            TravelHistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ── PROMO ────────────────────────────────────────────────────────────
        composable(
            route = Screen.Promo.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            PromoScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── NOTIFICATION SETTINGS ────────────────────────────────────────────
        composable(
            route = Screen.NotificationSettings.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            NotificationSettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ── LANGUAGE ─────────────────────────────────────────────────────────
        composable(
            route = Screen.Language.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            LanguageScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ── PRIVACY & SECURITY ───────────────────────────────────────────────
        composable(
            route = Screen.PrivacySecurity.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            PrivacySecurityScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ── HELP CENTER ──────────────────────────────────────────────────────
        composable(
            route = Screen.HelpCenter.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
            }
        ) {
            HelpCenterScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── SELECT COUNTRY ───────────────────────────────────────────────────
        composable(
            route = Screen.SelectCountry.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(400))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(300))
            }
        ) {
            SelectCountryScreen(
                onCountrySelected = { country ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_country", country)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
