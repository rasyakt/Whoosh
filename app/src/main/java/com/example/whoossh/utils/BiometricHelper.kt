package com.example.whoossh.utils

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * BiometricHelper - Utility class untuk menangani autentikasi biometrik
 * 
 * Fitur:
 * - Cek ketersediaan biometrik di perangkat
 * - Tampilkan prompt biometrik untuk autentikasi
 * - Support untuk login dan konfirmasi transaksi (refund/reschedule)
 */
object BiometricHelper {

    private const val TAG = "BiometricHelper"

    /**
     * Cek apakah perangkat mendukung biometrik
     */
    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        val result = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        Log.d(TAG, "isBiometricAvailable: result=$result")
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Cek status biometrik dan kembalikan pesan error jika ada
     */
    fun getBiometricStatus(context: Context): Pair<Boolean, String> {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d(TAG, "Biometric available")
                Pair(true, "Biometrik tersedia")
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.w(TAG, "No biometric hardware")
                Pair(false, "Perangkat tidak memiliki sensor biometrik")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.w(TAG, "Biometric hardware unavailable")
                Pair(false, "Sensor biometrik tidak tersedia saat ini")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.w(TAG, "No biometric enrolled")
                Pair(false, "Belum ada biometrik yang terdaftar. Silakan daftarkan sidik jari atau face unlock di pengaturan perangkat")
            }
            else -> {
                Log.w(TAG, "Biometric not available")
                Pair(false, "Biometrik tidak dapat digunakan")
            }
        }
    }

    /**
     * Tampilkan prompt biometrik untuk login
     */
    fun showBiometricPromptForLogin(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d(TAG, "showBiometricPromptForLogin called")
        showBiometricPrompt(
            activity = activity,
            title = "Login dengan Biometrik",
            subtitle = "Gunakan sidik jari atau face unlock untuk masuk",
            negativeButtonText = "Batal",
            onSuccess = onSuccess,
            onError = onError
        )
    }

    /**
     * Tampilkan prompt biometrik untuk konfirmasi refund
     */
    fun showBiometricPromptForRefund(
        activity: FragmentActivity,
        bookingCode: String,
        amount: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d(TAG, "showBiometricPromptForRefund called for $bookingCode")
        showBiometricPrompt(
            activity = activity,
            title = "Konfirmasi Refund",
            subtitle = "Refund tiket $bookingCode sebesar $amount",
            negativeButtonText = "Batal",
            onSuccess = onSuccess,
            onError = onError
        )
    }

    /**
     * Tampilkan prompt biometrik untuk konfirmasi reschedule
     */
    fun showBiometricPromptForReschedule(
        activity: FragmentActivity,
        bookingCode: String,
        newDate: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d(TAG, "showBiometricPromptForReschedule called for $bookingCode")
        showBiometricPrompt(
            activity = activity,
            title = "Konfirmasi Reschedule",
            subtitle = "Ubah jadwal tiket $bookingCode ke $newDate",
            negativeButtonText = "Batal",
            onSuccess = onSuccess,
            onError = onError
        )
    }

    /**
     * Fungsi internal untuk menampilkan prompt biometrik
     */
    private fun showBiometricPrompt(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        negativeButtonText: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d(TAG, "showBiometricPrompt: title=$title")
        
        // Cek ketersediaan biometrik terlebih dahulu
        val (isAvailable, statusMessage) = getBiometricStatus(activity)
        if (!isAvailable) {
            Log.e(TAG, "Biometric not available: $statusMessage")
            onError(statusMessage)
            return
        }
        
        val executor = ContextCompat.getMainExecutor(activity)
        
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.e(TAG, "onAuthenticationError: code=$errorCode, message=$errString")
                    when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_USER_CANCELED -> {
                            onError("Autentikasi dibatalkan")
                        }
                        BiometricPrompt.ERROR_LOCKOUT -> {
                            onError("Terlalu banyak percobaan. Silakan coba lagi nanti")
                        }
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                            onError("Biometrik terkunci. Gunakan metode lain untuk login")
                        }
                        else -> {
                            onError("Autentikasi gagal: $errString")
                        }
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.i(TAG, "onAuthenticationSucceeded")
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.w(TAG, "onAuthenticationFailed")
                    // Tidak perlu tampilkan error di sini, biarkan user coba lagi
                    // Error akan muncul jika terlalu banyak percobaan gagal
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setConfirmationRequired(true) // Tambahkan konfirmasi eksplisit
            .build()

        Log.d(TAG, "Showing biometric prompt...")
        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing biometric prompt", e)
            onError("Gagal menampilkan prompt biometrik: ${e.message}")
        }
    }
}
