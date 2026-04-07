package com.example.whoossh.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object SelectSchedule : Screen("select_schedule")
    data object SelectCoach : Screen("select_coach")
    data object SelectSeat : Screen("select_seat")
    data object Summary : Screen("summary")
    data object ETicket : Screen("e_ticket")
}
