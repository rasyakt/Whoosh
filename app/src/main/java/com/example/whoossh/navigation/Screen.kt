package com.example.whoossh.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Dashboard : Screen("dashboard")
    data object SelectSchedule : Screen("select_schedule")
    data object SelectCoach : Screen("select_coach")
    data object SelectSeat : Screen("select_seat")
    data object Summary : Screen("summary")
    data object ETicket : Screen("e_ticket")
    data object EditProfile : Screen("edit_profile")
    data object ChangePassword : Screen("change_password")
    data object TravelHistory : Screen("travel_history")
    data object Promo : Screen("promo")
    data object NotificationSettings : Screen("notification_settings")
    data object Language : Screen("language")
    data object PrivacySecurity : Screen("privacy_security")
    data object HelpCenter : Screen("help_center")
}
