package com.example.whoossh.utils

import android.util.Log
import com.example.whoossh.model.BookingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {

    // ╔═══════════════════════════════════════════════════════════════╗
    // ║  KONFIGURASI SMTP — Credentials dipindahkan ke backend      ║
    // ║  JANGAN hardcode credentials di sini untuk security         ║
    // ║  Environment-specific config via BuildConfig atau API       ║
    // ╚═══════════════════════════════════════════════════════════════╝
    private const val SMTP_HOST = "smtp.gmail.com"
    private const val SMTP_PORT = "587"

    // ⚠️ SECURITY FIX: Gunakan BuildConfig untuk dev/debug credentials
    // Production: Gunakan backend API untuk mengirim email, bukan client-side
    private val SENDER_EMAIL: String
        get() = com.example.whoossh.BuildConfig.SMTP_SENDER_EMAIL

    private val SENDER_PASSWORD: String
        get() = com.example.whoossh.BuildConfig.SMTP_SENDER_PASSWORD

    private const val TAG = "EmailSender"

    private var isEmailConfigured: Boolean = false

    init {
        // Validate email configuration at startup
        isEmailConfigured = try {
            SENDER_EMAIL.isNotBlank() && SENDER_PASSWORD.isNotBlank()
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Email configuration not set in BuildConfig")
            false
        }
    }

    /**
     * Mengirim e-ticket ke email penerima secara asinkron.
     * Fungsi ini harus dipanggil dari coroutine (Dispatchers.IO).
     *
     * ⚠️ SECURITY: Jika email belum dikonfigurasi di BuildConfig,
     * gunakan backend API untuk mengirim email (recommended untuk production)
     */
    suspend fun sendETicket(
        recipientEmail: String,
        bookingData: BookingData
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // Check if email is configured
            if (!isEmailConfigured) {
                Log.w(TAG, "Email not configured in BuildConfig. Skipping email send. Use backend API instead.")
                return@withContext false
            }

            val props = createProperties()
            val session = createSession(props)

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(SENDER_EMAIL, "KCIC Ticketing System"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                subject = "E-Ticket Purchase - Whoosh"
                setContent(buildETicketHtml(bookingData), "text/html; charset=utf-8")
            }

            Transport.send(message)
            Log.i(TAG, "E-ticket successfully sent to $recipientEmail")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send e-ticket: ${e.message}", e)
            false
        }
    }

    /**
     * Mengirim notifikasi refund ke email user.
     */
    suspend fun sendRefundNotification(
        recipientEmail: String,
        bookingData: BookingData,
        refundAmount: Int,
        bankName: String = "",
        accountNo: String = "",
        accountHolder: String = ""
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // Check if email is configured
            if (!isEmailConfigured) {
                Log.w(TAG, "Email not configured in BuildConfig. Skipping refund notification send. Use backend API instead.")
                return@withContext false
            }

            val props = createProperties()
            val session = createSession(props)

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(SENDER_EMAIL, "KCIC Ticketing System"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                subject = "Ticket Refund Confirmation - Whoosh"
                setContent(buildRefundHtml(bookingData, refundAmount, bankName, accountNo, accountHolder), "text/html; charset=utf-8")
            }

            Transport.send(message)
            Log.i(TAG, "Refund notification sent to $recipientEmail")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send refund notification: ${e.message}", e)
            false
        }
    }

    /**
     * Mengirim notifikasi reschedule ke email user.
     */
    suspend fun sendRescheduleNotification(
        recipientEmail: String,
        bookingData: BookingData,
        newDate: String,
        newTime: String,
        newArrivalTime: String,
        rescheduleFee: Int
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // Check if email is configured
            if (!isEmailConfigured) {
                Log.w(TAG, "Email not configured in BuildConfig. Skipping reschedule notification send. Use backend API instead.")
                return@withContext false
            }

            val props = createProperties()
            val session = createSession(props)

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(SENDER_EMAIL, "KCIC Ticketing System"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                subject = "Ticket Reschedule Confirmation - Whoosh"
                setContent(buildRescheduleHtml(bookingData, newDate, newTime, newArrivalTime, rescheduleFee), "text/html; charset=utf-8")
            }

            Transport.send(message)
            Log.i(TAG, "Reschedule notification sent to $recipientEmail")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send reschedule notification: ${e.message}", e)
            false
        }
    }

    private fun createProperties() = Properties().apply {
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.host", SMTP_HOST)
        put("mail.smtp.port", SMTP_PORT)
        put("mail.smtp.ssl.trust", SMTP_HOST)
    }

    private fun createSession(props: Properties) = Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD)
        }
    })

    /**
     * Membuat HTML template e-ticket yang profesional sesuai format KCIC.
     * ✅ FIX: Menampilkan semua penumpang, bukan hanya 1
     */
    private fun buildETicketHtml(data: BookingData): String {
        val formattedPrice = TicketUtils.formatRupiah(data.totalPrice)
        val seats = data.selectedSeats.sorted().joinToString(", ").ifEmpty { "-" }
        val currentDate = java.text.SimpleDateFormat("dd MMM yyyy, HH.mm", java.util.Locale("id", "ID")).format(java.util.Date())

        // ✅ FIX: Build passenger list HTML untuk menampilkan semua penumpang
        val passengerListHtml = if (data.passengers.isNotEmpty()) {
            val passengerRows = data.passengers.mapIndexed { index, passenger ->
                """
                <tr>
                    <td style="padding:12px 15px; background:${if (index % 2 == 0) "#FAFAFA" else "#FFFFFF"}; border-bottom:1px solid #EEEEEE;">
                        <p style="margin:0;font-size:14px;color:#333;"><strong>${index + 1}. ${passenger.name}</strong></p>
                        <p style="margin:4px 0 0;font-size:12px;color:#666;">ID: ${passenger.identityNo.take(4)}****${passenger.identityNo.takeLast(2)}</p>
                        <p style="margin:3px 0 0;font-size:12px;color:#666;">Seat: ${passenger.seatNumber.ifBlank { "-" }} | ${passenger.passengerType}</p>
                    </td>
                </tr>
                """.trimIndent()
            }.joinToString("\n")
            
            """
            <tr>
                <td style="padding:0 40px 20px;">
                    <h3 style="margin:0;font-size:16px;color:#333;font-weight:600;border-bottom:1px solid #EEE;padding-bottom:10px;">PASSENGER DETAILS (${data.passengers.size} Passenger${if (data.passengers.size > 1) "s" else ""})</h3>
                </td>
            </tr>
            <tr>
                <td style="padding:0 40px 30px;">
                    <table width="100%" cellpadding="0" cellspacing="0" style="border:1px solid #EEEEEE;border-radius:8px;overflow:hidden;">
                        $passengerRows
                    </table>
                </td>
            </tr>
            """.trimIndent()
        } else {
            // Fallback jika passengers kosong
            """
            <tr>
                <td style="padding:0 40px 20px;">
                    <h3 style="margin:0;font-size:16px;color:#333;font-weight:600;border-bottom:1px solid #EEE;padding-bottom:10px;">PASSENGER</h3>
                </td>
            </tr>
            <tr>
                <td style="padding:0 40px 30px;">
                    <table width="100%" cellpadding="0" cellspacing="0" style="background:#F9F9F9;border-radius:8px;padding:15px;">
                        <tr>
                            <td>
                                <p style="margin:0;font-size:14px;color:#333;"><strong>${data.userName}</strong></p>
                                <p style="margin:5px 0 0;font-size:13px;color:#666;">${data.ticketCount} Ticket(s)</p>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            """.trimIndent()
        }

        return buildBaseHtml(
            title = "Ticket Purchase Success",
            userName = data.userName,
            mainMessage = "Your High Speed Train ticket order has been successfully paid",
            content = """
                <tr>
                    <td style="padding:0 40px 20px;">
                        <p style="margin:0;font-size:14px;color:#666;line-height:1.6;">
                            To get your QR e-ticket for Check-In, please access the following link:
                        </p>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 30px;">
                        <a href="https://whoosh.id/ticket/${data.bookingCode}" 
                           style="display:inline-block;background:#D32F2F;color:#FFFFFF;padding:12px 24px;border-radius:6px;font-size:14px;text-decoration:none;font-weight:bold;">
                           GET QR CODE
                        </a>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 20px;">
                        <h3 style="margin:0;font-size:16px;color:#333;font-weight:600;border-bottom:1px solid #EEE;padding-bottom:10px;">ORDER DETAILS</h3>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 30px;">
                        <table width="100%" cellpadding="0" cellspacing="0" style="background:#F5F5F5;border-radius:8px;padding:20px;">
                            <tr>
                                <td>
                                    <p style="margin:0 0 8px 0;font-size:14px;color:#666;"><strong>Total Payment:</strong> $formattedPrice</p>
                                    <p style="margin:0 0 8px 0;font-size:14px;color:#666;"><strong>Booking Channel:</strong> WHOOSH APP</p>
                                    <p style="margin:0 0 8px 0;font-size:14px;color:#666;"><strong>Payment Channel:</strong> Virtual Account</p>
                                    <p style="margin:0;font-size:14px;color:#666;"><strong>Payment Date:</strong> $currentDate</p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 20px;">
                        <h3 style="margin:0;font-size:16px;color:#333;font-weight:600;border-bottom:1px solid #EEE;padding-bottom:10px;">TRAVEL INFORMATION</h3>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 30px;">
                        <table width="100%" cellpadding="0" cellspacing="0">
                            <tr>
                                <td style="padding-bottom:10px;">
                                    <p style="margin:0;font-size:15px;color:#333;font-weight:bold;">KCIC High Speed Train</p>
                                    <p style="margin:5px 0 0;font-size:14px;color:#666;">${data.originStation} → ${data.destinationStation}</p>
                                    <p style="margin:5px 0 0;font-size:16px;color:#D32F2F;font-weight:bold;">${data.departureTime} — ${data.arrivalTime}</p>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding:15px; background:#F9F9F9; border-radius:6px; border-left:4px solid #D32F2F;">
                                    <p style="margin:0;font-size:13px;color:#333;"><strong>Class:</strong> ${data.coachClass.displayName}</p>
                                    <p style="margin:5px 0 0;font-size:13px;color:#333;"><strong>Coach:</strong> ${data.selectedCarriage}</p>
                                    <p style="margin:5px 0 0;font-size:13px;color:#333;"><strong>Seats:</strong> $seats</p>
                                    <p style="margin:5px 0 0;font-size:13px;color:#333;"><strong>Booking Code:</strong> ${data.bookingCode}</p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                $passengerListHtml
                <tr>
                    <td style="padding:0 40px 30px;">
                        <p style="margin:0;font-size:11px;color:#999;line-height:1.4;">
                            * VAT fees are waived based on article 16b of the Tax Harmonization Law.<br>
                            * Please arrive at the station at least 30 minutes before departure.
                        </p>
                    </td>
                </tr>
            """.trimIndent()
        )
    }

    /**
     * Membuat HTML template untuk notifikasi refund.
     */
    private fun buildRefundHtml(data: BookingData, refundAmount: Int, bankName: String, accountNo: String, accountHolder: String): String {
        val formattedAmount = TicketUtils.formatRupiah(refundAmount)
        val formattedOriginalPrice = TicketUtils.formatRupiah(data.totalPrice)
        val seats = data.selectedSeats.sorted().joinToString(", ").ifEmpty { "-" }
        
        return buildBaseHtml(
            title = "Ticket Refund Confirmation",
            userName = data.userName,
            mainMessage = "Your ticket refund request has been processed successfully",
            content = """
                <tr>
                    <td style="padding:0 40px 20px;">
                        <p style="margin:0;font-size:14px;color:#333;line-height:1.6;">
                            We would like to inform you that your refund for <strong>${data.originStation} → ${data.destinationStation}</strong> has been approved. 
                        </p>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 20px;">
                        <h3 style="margin:0;font-size:16px;color:#333;font-weight:600;border-bottom:1px solid #EEE;padding-bottom:10px;">REFUND SUMMARY</h3>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 30px;">
                        <table width="100%" cellpadding="0" cellspacing="0" style="background:#FFF3F3;border-radius:8px;padding:20px;border:1px solid #FFCDD2;">
                            <tr>
                                <td>
                                    <p style="margin:0 0 8px 0;font-size:14px;color:#333;"><strong>Booking Code:</strong> ${data.bookingCode}</p>
                                    <p style="margin:0 0 8px 0;font-size:14px;color:#333;"><strong>Original Price:</strong> $formattedOriginalPrice</p>
                                    <p style="margin:0 0 8px 0;font-size:14px;color:#D32F2F;font-weight:bold;"><strong>Refund Amount:</strong> $formattedAmount</p>
                                    <div style="margin:15px 0; padding-top:15px; border-top:1px dashed #FFCDD2;">
                                        <p style="margin:0 0 5px 0;font-size:13px;color:#333;"><strong>Destination Account:</strong></p>
                                        <p style="margin:0 0 3px 0;font-size:14px;color:#333;">Bank: ${bankName.ifBlank { "-" }}</p>
                                        <p style="margin:0 0 3px 0;font-size:14px;color:#333;">Account No: ${accountNo.ifBlank { "-" }}</p>
                                        <p style="margin:0;font-size:14px;color:#333;">Account Holder: ${accountHolder.ifBlank { "-" }}</p>
                                    </div>
                                    <p style="margin:0;font-size:12px;color:#666;">*The amount will be credited to your account within 7-14 business days.</p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 20px;">
                        <h3 style="margin:0;font-size:16px;color:#333;font-weight:600;border-bottom:1px solid #EEE;padding-bottom:10px;">ORIGINAL TICKET DETAILS</h3>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 30px;">
                        <table width="100%" cellpadding="0" cellspacing="0" style="background:#F9F9F9;border-radius:8px;padding:20px;">
                            <tr>
                                <td>
                                    <p style="margin:0 0 8px 0;font-size:13px;color:#666;"><strong>Route:</strong> ${data.originStation} → ${data.destinationStation}</p>
                                    <p style="margin:0 0 8px 0;font-size:13px;color:#666;"><strong>Departure:</strong> ${data.departureDate} | ${data.departureTime}</p>
                                    <p style="margin:0 0 8px 0;font-size:13px;color:#666;"><strong>Class:</strong> ${data.coachClass.displayName}</p>
                                    <p style="margin:0;font-size:13px;color:#666;"><strong>Seats:</strong> $seats</p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            """.trimIndent()
        )
    }

    /**
     * Membuat HTML template untuk notifikasi reschedule.
     */
    private fun buildRescheduleHtml(
        data: BookingData, 
        newDate: String, 
        newTime: String,
        newArrivalTime: String,
        rescheduleFee: Int
    ): String {
        val formattedFee = TicketUtils.formatRupiah(rescheduleFee)
        val seats = data.selectedSeats.sorted().joinToString(", ").ifEmpty { "-" }
        
        return buildBaseHtml(
            title = "Ticket Reschedule Confirmation",
            userName = data.userName,
            mainMessage = "Your ticket has been rescheduled successfully",
            content = """
                <tr>
                    <td style="padding:0 40px 20px;">
                        <p style="margin:0;font-size:14px;color:#333;line-height:1.6;">
                            Your booking <strong>${data.bookingCode}</strong> has been successfully updated. 
                        </p>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 20px;">
                        <h3 style="margin:0;font-size:16px;color:#333;font-weight:600;border-bottom:1px solid #1976D2;padding-bottom:10px;color:#1976D2;">NEW SCHEDULE</h3>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 30px;">
                        <table width="100%" cellpadding="0" cellspacing="0" style="background:#E3F2FD;border-radius:8px;padding:20px;border:1px solid #BBDEFB;">
                            <tr>
                                <td>
                                    <p style="margin:0 0 10px 0;font-size:14px;color:#333;"><strong>New Departure:</strong></p>
                                    <p style="margin:0 0 5px 0;font-size:18px;color:#1976D2;font-weight:bold;">$newDate</p>
                                    <p style="margin:0;font-size:18px;color:#D32F2F;font-weight:bold;">$newTime — $newArrivalTime</p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 20px;">
                        <h3 style="margin:0;font-size:16px;color:#333;font-weight:600;border-bottom:1px solid #EEE;padding-bottom:10px;">TRANSACTION DETAILS</h3>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 30px;">
                        <table width="100%" cellpadding="0" cellspacing="0" style="background:#F9F9F9;border-radius:8px;padding:20px;">
                            <tr>
                                <td>
                                    <p style="margin:0 0 8px 0;font-size:13px;color:#666;"><strong>Reschedule Fee:</strong> $formattedFee</p>
                                    <p style="margin:0 0 8px 0;font-size:13px;color:#666;"><strong>Class:</strong> ${data.coachClass.displayName}</p>
                                    <p style="margin:0;font-size:13px;color:#666;"><strong>Seats:</strong> $seats</p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 20px;">
                        <h3 style="margin:0;font-size:16px;color:#333;font-weight:600;border-bottom:1px solid #EEE;padding-bottom:10px;">PREVIOUS SCHEDULE</h3>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 30px;">
                        <table width="100%" cellpadding="0" cellspacing="0" style="border:1px dashed #DDD;border-radius:8px;padding:15px;">
                            <tr>
                                <td>
                                    <p style="margin:0 0 5px 0;font-size:13px;color:#999;"><strong>Original Route:</strong> ${data.originStation} → ${data.destinationStation}</p>
                                    <p style="margin:0;font-size:13px;color:#999;"><strong>Original Departure:</strong> ${data.departureDate} | ${data.departureTime}</p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td style="padding:0 40px 30px;">
                        <p style="margin:0;font-size:13px;color:#666;line-height:1.6;">
                            Please check your updated QR code in the Whoosh application.
                        </p>
                    </td>
                </tr>
            """.trimIndent()
        )
    }

    /**
     * Base HTML structure for all Whoosh emails.
     */
    private fun buildBaseHtml(title: String, userName: String, mainMessage: String, content: String): String {
        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>$title - Whoosh</title>
        </head>
        <body style="margin:0;padding:20px;background-color:#f5f5f5;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Arial,sans-serif;">
            <table width="100%" cellpadding="0" cellspacing="0" style="max-width:600px;margin:0 auto;background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 10px rgba(0,0,0,0.05);">
                <!-- HEADER -->
                <tr>
                    <td style="padding:40px 40px 20px; text-align:left;">
                        <h1 style="margin:0;font-size:28px;color:#D32F2F;font-weight:bold;letter-spacing:2px;">whoosh</h1>
                        <div style="height:2px; width:50px; background:#D32F2F; margin-top:10px;"></div>
                    </td>
                </tr>
                <!-- GREETING -->
                <tr>
                    <td style="padding:20px 40px 10px;">
                        <p style="margin:0;font-size:16px;color:#333;line-height:1.5;">Dear <strong>$userName</strong>,</p>
                    </td>
                </tr>
                <!-- MAIN MESSAGE -->
                <tr>
                    <td style="padding:0 40px 20px;">
                        <h2 style="margin:0;font-size:22px;color:#333;font-weight:600;line-height:1.4;">$mainMessage</h2>
                    </td>
                </tr>
                $content
                <!-- FOOTER -->
                <tr>
                    <td style="padding:30px 40px;background:#F9F9F9;border-top:1px solid #EEE;">
                        <p style="margin:0;font-size:12px;color:#999;line-height:1.6;">
                            This is an automated message, please do not reply. 
                            For assistance, contact us at <a href="mailto:csrb@kcic.co.id" style="color:#1976D2;text-decoration:none;">csrb@kcic.co.id</a>
                        </p>
                        <p style="margin:10px 0 0;font-size:12px;color:#BBB;">© ${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)} PT Kereta Cepat Indonesia China</p>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """.trimIndent()
    }
}
