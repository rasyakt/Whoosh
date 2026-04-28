package com.example.whoossh.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.whoossh.ui.screens.*
import com.example.whoossh.viewmodel.BookingViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: BookingViewModel
) {
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
                onNavigateToETicket = {
                    navController.navigate(Screen.ETicket.route)
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
            val passenger = passengerId?.let { 
                viewModel.getSelectedPassengerById(it) ?: viewModel.getSavedPassengerById(it)
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
                    navController.navigate(Screen.Summary.route)
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
            SummaryScreen(
                viewModel = viewModel,
                onConfirm = {
                    navController.navigate(Screen.ETicket.route) {
                        popUpTo(Screen.Dashboard.route)
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
            
            LaunchedEffect(bookingCode) {
                bookingCode?.let { viewModel.loadTicketByCode(it) }
            }

            ETicketScreen(
                viewModel = viewModel,
                onBackToDashboard = {
                    viewModel.resetBooking()
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
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
