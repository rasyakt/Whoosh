package com.example.whoossh.data

import android.content.Context
import android.content.SharedPreferences
import com.example.whoossh.model.User
import com.google.gson.Gson

/**
 * UserPreferences - Menyimpan data lokal di perangkat.
 *
 * Setelah migrasi ke SQL Server API, class ini hanya digunakan untuk:
 * 1. Cache session user yang sedang login (agar tidak perlu login ulang tiap buka app)
 * 2. Menyimpan pengaturan lokal (notifikasi, bahasa, biometric, dll.)
 * 3. Cache status pembayaran tiket (untuk mengatasi delay sync dengan server)
 *
 * Semua operasi CRUD (register, login, booking, tiket) sudah ditangani oleh API.
 */
class UserPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("whoosh_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_LOGGED_IN_USER = "logged_in_user"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NOTIF_PROMO = "notif_promo"
        private const val KEY_NOTIF_TRAVEL = "notif_travel"
        private const val KEY_NOTIF_UPDATE = "notif_update"
        private const val KEY_NOTIF_EMAIL = "notif_email"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_BIOMETRIC = "biometric_login"
        private const val KEY_SAVE_LOGIN = "save_login"
        private const val KEY_PAID_TICKETS = "paid_tickets_cache"
        private const val KEY_CANCELLED_TICKETS = "cancelled_tickets_cache"
        private const val KEY_REFUNDED_TICKETS = "refunded_tickets_cache"
        
        // Bank Account Info
        private const val KEY_BANK_NAME = "bank_name"
        private const val KEY_ACCOUNT_NO = "account_no"
        private const val KEY_ACCOUNT_HOLDER = "account_holder"
    }

    // ── USER SESSION CACHE ──────────────────────────────────────────────────

    fun saveLoggedInUser(user: User) {
        prefs.edit().putString(KEY_LOGGED_IN_USER, gson.toJson(user)).apply()
    }

    fun saveLoggedInUser(user: User, userId: Int) {
        prefs.edit()
            .putString(KEY_LOGGED_IN_USER, gson.toJson(user))
            .putInt(KEY_USER_ID, userId)
            .apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, 0)
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
        prefs.edit()
            .remove(KEY_LOGGED_IN_USER)
            .remove(KEY_USER_ID)
            .apply()
    }

    // ── PAID TICKETS CACHE ───────────────────────────────────────────────────
    // Cache booking codes yang sudah dibayar untuk mengatasi delay sync server

    fun savePaidTicket(bookingCode: String) {
        val paidTickets = getPaidTickets().toMutableSet()
        paidTickets.add(bookingCode)
        prefs.edit().putStringSet(KEY_PAID_TICKETS, paidTickets).apply()
    }

    fun getPaidTickets(): Set<String> {
        return prefs.getStringSet(KEY_PAID_TICKETS, emptySet()) ?: emptySet()
    }

    fun isPaidTicket(bookingCode: String): Boolean {
        return getPaidTickets().contains(bookingCode)
    }

    fun removePaidTicket(bookingCode: String) {
        val paidTickets = getPaidTickets().toMutableSet()
        paidTickets.remove(bookingCode)
        prefs.edit().putStringSet(KEY_PAID_TICKETS, paidTickets).apply()
    }

    fun clearPaidTicketsCache() {
        prefs.edit().remove(KEY_PAID_TICKETS).apply()
    }

    // ── CANCELLED TICKETS CACHE ──────────────────────────────────────────────
    // Cache booking codes yang sudah dibatalkan untuk mengatasi delay sync server

    fun saveCancelledTicket(bookingCode: String) {
        val cancelledTickets = getCancelledTickets().toMutableSet()
        cancelledTickets.add(bookingCode)
        prefs.edit().putStringSet(KEY_CANCELLED_TICKETS, cancelledTickets).apply()
    }

    fun getCancelledTickets(): Set<String> {
        return prefs.getStringSet(KEY_CANCELLED_TICKETS, emptySet()) ?: emptySet()
    }

    fun isCancelledTicket(bookingCode: String): Boolean {
        return getCancelledTickets().contains(bookingCode)
    }

    fun removeCancelledTicket(bookingCode: String) {
        val cancelledTickets = getCancelledTickets().toMutableSet()
        cancelledTickets.remove(bookingCode)
        prefs.edit().putStringSet(KEY_CANCELLED_TICKETS, cancelledTickets).apply()
    }

    fun clearCancelledTicketsCache() {
        prefs.edit().remove(KEY_CANCELLED_TICKETS).apply()
    }

    // ── REFUNDED TICKETS CACHE ──────────────────────────────────────────────
    
    fun saveRefundedTicket(bookingCode: String) {
        val refunded = getRefundedTickets().toMutableSet()
        refunded.add(bookingCode)
        prefs.edit().putStringSet(KEY_REFUNDED_TICKETS, refunded).apply()
    }

    fun getRefundedTickets(): Set<String> {
        return prefs.getStringSet(KEY_REFUNDED_TICKETS, emptySet()) ?: emptySet()
    }

    fun isRefundedTicket(bookingCode: String): Boolean {
        return getRefundedTickets().contains(bookingCode)
    }

    fun removeRefundedTicket(bookingCode: String) {
        val refunded = getRefundedTickets().toMutableSet()
        refunded.remove(bookingCode)
        prefs.edit().putStringSet(KEY_REFUNDED_TICKETS, refunded).apply()
    }

    fun clearRefundedTicketsCache() {
        prefs.edit().remove(KEY_REFUNDED_TICKETS).apply()
    }

    // ── BANK ACCOUNT INFO ────────────────────────────────────────────────────
    
    fun saveBankAccount(bankName: String, accountNo: String, accountHolder: String) {
        prefs.edit()
            .putString(KEY_BANK_NAME, bankName)
            .putString(KEY_ACCOUNT_NO, accountNo)
            .putString(KEY_ACCOUNT_HOLDER, accountHolder)
            .apply()
    }

    fun getBankName(): String = prefs.getString(KEY_BANK_NAME, "") ?: ""
    fun getAccountNo(): String = prefs.getString(KEY_ACCOUNT_NO, "") ?: ""
    fun getAccountHolder(): String = prefs.getString(KEY_ACCOUNT_HOLDER, "") ?: ""

    fun clearBankAccount() {
        prefs.edit()
            .remove(KEY_BANK_NAME)
            .remove(KEY_ACCOUNT_NO)
            .remove(KEY_ACCOUNT_HOLDER)
            .apply()
    }

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
