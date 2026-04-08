package com.example.whoossh.data

import android.content.Context
import android.content.SharedPreferences
import com.example.whoossh.model.BookingData
import com.example.whoossh.model.CoachClass
import com.example.whoossh.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("whoosh_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_LOGGED_IN_USER = "logged_in_user"
        private const val KEY_USERS = "registered_users"
        private const val KEY_TICKETS = "user_tickets"
        private const val KEY_NOTIF_PROMO = "notif_promo"
        private const val KEY_NOTIF_TRAVEL = "notif_travel"
        private const val KEY_NOTIF_UPDATE = "notif_update"
        private const val KEY_NOTIF_EMAIL = "notif_email"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_BIOMETRIC = "biometric_login"
        private const val KEY_SAVE_LOGIN = "save_login"
    }

    // ── USER REGISTRATION & AUTH ─────────────────────────────────────────────

    fun registerUser(user: User): Boolean {
        val users = getRegisteredUsers().toMutableList()
        if (users.any { it.email == user.email }) {
            return false // Email already exists
        }
        users.add(user)
        prefs.edit().putString(KEY_USERS, gson.toJson(users)).apply()
        return true
    }

    fun loginUser(email: String, password: String): User? {
        val users = getRegisteredUsers()
        return users.find { it.email == email && it.password == password }
    }

    fun getRegisteredUsers(): List<User> {
        val json = prefs.getString(KEY_USERS, null) ?: return emptyList()
        val type = object : TypeToken<List<User>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveLoggedInUser(user: User) {
        prefs.edit().putString(KEY_LOGGED_IN_USER, gson.toJson(user)).apply()
    }

    fun getLoggedInUser(): User? {
        val json = prefs.getString(KEY_LOGGED_IN_USER, null) ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearLoggedInUser() {
        prefs.edit().remove(KEY_LOGGED_IN_USER).apply()
    }

    fun updateUser(oldEmail: String, updatedUser: User) {
        val users = getRegisteredUsers().toMutableList()
        val index = users.indexOfFirst { it.email == oldEmail }
        if (index != -1) {
            users[index] = updatedUser
            prefs.edit().putString(KEY_USERS, gson.toJson(users)).apply()
        }
        // Also update logged-in user if same
        val loggedIn = getLoggedInUser()
        if (loggedIn?.email == oldEmail) {
            saveLoggedInUser(updatedUser)
        }
    }

    fun changePassword(email: String, oldPassword: String, newPassword: String): Boolean {
        val users = getRegisteredUsers().toMutableList()
        val index = users.indexOfFirst { it.email == email && it.password == oldPassword }
        if (index == -1) return false
        val updated = users[index].copy(password = newPassword)
        users[index] = updated
        prefs.edit().putString(KEY_USERS, gson.toJson(users)).apply()
        saveLoggedInUser(updated)
        return true
    }

    // ── TICKETS ──────────────────────────────────────────────────────────────

    fun saveTicket(booking: BookingData) {
        val tickets = getTickets().toMutableList()
        tickets.add(0, booking) // newest first
        val json = gson.toJson(tickets)
        prefs.edit().putString(KEY_TICKETS, json).apply()
    }

    fun getTickets(): List<BookingData> {
        val json = prefs.getString(KEY_TICKETS, null) ?: return emptyList()
        val type = object : TypeToken<List<BookingData>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getActiveTickets(): List<BookingData> {
        return getTickets().filter { !it.isUsed }
    }

    fun getHistoryTickets(): List<BookingData> {
        return getTickets().filter { it.isUsed }
    }

    fun getTicketCount(): Int = getTickets().size
    fun getActiveTicketCount(): Int = getActiveTickets().size

    // ── NOTIFICATION SETTINGS ────────────────────────────────────────────────

    fun setNotifPromo(enabled: Boolean) { prefs.edit().putBoolean(KEY_NOTIF_PROMO, enabled).apply() }
    fun getNotifPromo(): Boolean = prefs.getBoolean(KEY_NOTIF_PROMO, true)

    fun setNotifTravel(enabled: Boolean) { prefs.edit().putBoolean(KEY_NOTIF_TRAVEL, enabled).apply() }
    fun getNotifTravel(): Boolean = prefs.getBoolean(KEY_NOTIF_TRAVEL, true)

    fun setNotifUpdate(enabled: Boolean) { prefs.edit().putBoolean(KEY_NOTIF_UPDATE, enabled).apply() }
    fun getNotifUpdate(): Boolean = prefs.getBoolean(KEY_NOTIF_UPDATE, true)

    fun setNotifEmail(enabled: Boolean) { prefs.edit().putBoolean(KEY_NOTIF_EMAIL, enabled).apply() }
    fun getNotifEmail(): Boolean = prefs.getBoolean(KEY_NOTIF_EMAIL, false)

    // ── LANGUAGE ─────────────────────────────────────────────────────────────

    fun setLanguage(lang: String) { prefs.edit().putString(KEY_LANGUAGE, lang).apply() }
    fun getLanguage(): String = prefs.getString(KEY_LANGUAGE, "Bahasa Indonesia") ?: "Bahasa Indonesia"

    // ── PRIVACY ──────────────────────────────────────────────────────────────

    fun setBiometric(enabled: Boolean) { prefs.edit().putBoolean(KEY_BIOMETRIC, enabled).apply() }
    fun getBiometric(): Boolean = prefs.getBoolean(KEY_BIOMETRIC, false)

    fun setSaveLogin(enabled: Boolean) { prefs.edit().putBoolean(KEY_SAVE_LOGIN, enabled).apply() }
    fun getSaveLogin(): Boolean = prefs.getBoolean(KEY_SAVE_LOGIN, true)
}
