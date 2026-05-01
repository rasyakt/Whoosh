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
    // ║  KONFIGURASI SMTP — Ganti dengan akun Gmail pengirim Anda   ║
    // ║  Aktifkan "App Password" di https://myaccount.google.com    ║
    // ╚═══════════════════════════════════════════════════════════════╝
    private const val SMTP_HOST = "smtp.gmail.com"
    private const val SMTP_PORT = "587"
    private const val SENDER_EMAIL = "alifslebew800@gmail.com"   // Ganti dengan email pengirim
    private const val SENDER_PASSWORD = "moyc amjg xnlc jbfq"     // Ganti dengan App Password Gmail

    private const val TAG = "EmailSender"

    /**
     * Mengirim e-ticket ke email penerima secara asinkron.
     * Fungsi ini harus dipanggil dari coroutine (Dispatchers.IO).
     */
    suspend fun sendETicket(
        recipientEmail: String,
        bookingData: BookingData
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val props = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", SMTP_HOST)
                put("mail.smtp.port", SMTP_PORT)
                put("mail.smtp.ssl.trust", SMTP_HOST)
            }

            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD)
                }
            })

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(SENDER_EMAIL, "KCIC Ticketing System"))
                setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
                )
                subject = "Buy Ticket"
                setContent(buildHtmlBody(bookingData), "text/html; charset=utf-8")
            }

            Transport.send(message)
            Log.i(TAG, "E-ticket berhasil dikirim ke $recipientEmail")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengirim e-ticket: ${e.message}", e)
            false
        }
    }

    /**
     * Membuat HTML template e-ticket yang profesional sesuai format KCIC.
     */
    private fun buildHtmlBody(data: BookingData): String {
        val formattedPrice = TicketUtils.formatRupiah(data.totalPrice)
        val formattedPricePerTicket = TicketUtils.formatRupiah(data.pricePerTicket)
        val seats = data.selectedSeats.sorted().joinToString(", ").ifEmpty { "-" }
        
        // Format tanggal dan waktu pembayaran
        val currentDate = java.text.SimpleDateFormat("dd MMM yyyy, HH.mm", java.util.Locale("id", "ID"))
            .format(java.util.Date())

        return """
        <!DOCTYPE html>
        <html lang="id">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Buy Ticket - Whoosh</title>
        </head>
        <body style="margin:0;padding:20px;background-color:#f5f5f5;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;">

            <table width="100%" cellpadding="0" cellspacing="0" style="max-width:600px;margin:0 auto;background:#ffffff;">

                <!-- LOGO HEADER -->
                <tr>
                    <td style="padding:40px 40px 30px;">
                        <svg width="120" height="40" viewBox="0 0 120 40" xmlns="http://www.w3.org/2000/svg">
                            <text x="0" y="30" font-family="Arial, sans-serif" font-size="28" font-weight="bold" fill="#D32F2F" letter-spacing="2">whoosh</text>
                        </svg>
                    </td>
                </tr>

                <!-- GREETING -->
                <tr>
                    <td style="padding:0 40px 20px;">
                        <p style="margin:0;font-size:16px;color:#333;line-height:1.5;">
                            Hi, <strong>${data.userName}</strong>
                        </p>
                    </td>
                </tr>

                <!-- SUCCESS MESSAGE -->
                <tr>
                    <td style="padding:0 40px 30px;">
                        <h2 style="margin:0;font-size:24px;color:#333;font-weight:600;line-height:1.4;">
                            Your High Speed Train ticket order has been successfully paid
                        </h2>
                    </td>
                </tr>

                <!-- QR CODE INSTRUCTION -->
                <tr>
                    <td style="padding:0 40px 20px;">
                        <p style="margin:0;font-size:14px;color:#666;line-height:1.6;">
                            To get a QR digital ticket (QR e-ticket) for Check-In no later than 30 minutes before the scheduled departure of the High Speed Train, at the following link:
                        </p>
                    </td>
                </tr>

                <!-- QR CODE LINK -->
                <tr>
                    <td style="padding:0 40px 30px;">
                        <a href="https://whoosh.id/ticket/${data.bookingCode}" 
                           style="display:inline-block;color:#1976D2;font-size:14px;text-decoration:underline;">
                           Please click here for QR code
                        </a>
                    </td>
                </tr>

                <!-- ORDER DETAILS INTRO -->
                <tr>
                    <td style="padding:0 40px 20px;">
                        <p style="margin:0;font-size:14px;color:#333;line-height:1.6;">
                            Following are the details order for your high speed train ticket:
                        </p>
                    </td>
                </tr>

                <!-- PAYMENT INFO BOX -->
                <tr>
                    <td style="padding:0 40px 30px;">
                        <table width="100%" cellpadding="0" cellspacing="0" style="background:#F5F5F5;border-radius:8px;padding:20px;">
                            <tr>
                                <td>
                                    <p style="margin:0 0 8px 0;font-size:14px;color:#666;">
                                        <strong>Total Payment:</strong> $formattedPrice
                                    </p>
                                    <p style="margin:0 0 8px 0;font-size:14px;color:#666;">
                                        <strong>Booking Channel:</strong> APP
                                    </p>
                                    <p style="margin:0 0 8px 0;font-size:14px;color:#666;">
                                        <strong>Payment Channel:</strong> Virtual Account
                                    </p>
                                    <p style="margin:0;font-size:14px;color:#666;">
                                        <strong>Payment Date and Time:</strong> $currentDate
                                    </p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <!-- PAYMENT DETAIL HEADER -->
                <tr>
                    <td style="padding:0 40px 20px;">
                        <h3 style="margin:0;font-size:18px;color:#333;font-weight:600;">
                            PAYMENT DETAIL
                        </h3>
                        <div style="border-bottom:2px dashed #E0E0E0;margin-top:15px;"></div>
                    </td>
                </tr>

                <!-- TRAIN INFO -->
                <tr>
                    <td style="padding:0 40px 20px;">
                        <p style="margin:0 0 10px 0;font-size:16px;color:#333;font-weight:600;">
                            KCIC ( G1063 )
                        </p>
                    </td>
                </tr>

                <!-- ROUTE AND TIME -->
                <tr>
                    <td style="padding:0 40px 10px;">
                        <table width="100%" cellpadding="0" cellspacing="0">
                            <tr>
                                <td style="font-size:14px;color:#666;">
                                    ${data.originStation} → ${data.destinationStation}
                                </td>
                            </tr>
                            <tr>
                                <td style="padding-top:5px;">
                                    <span style="font-size:18px;color:#D32F2F;font-weight:600;">${data.departureTime}</span>
                                    <span style="font-size:14px;color:#999;margin:0 10px;">→</span>
                                    <span style="font-size:18px;color:#D32F2F;font-weight:600;">${data.arrivalTime}</span>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <!-- BOOKING ORDER -->
                <tr>
                    <td style="padding:15px 40px 10px;">
                        <p style="margin:0;font-size:13px;color:#666;">
                            PT Kereta Cepat Indonesia China Booking order: <strong>${data.bookingCode}</strong>
                        </p>
                    </td>
                </tr>

                <!-- LOGO SMALL -->
                <tr>
                    <td style="padding:0 40px 15px;text-align:right;">
                        <svg width="80" height="26" viewBox="0 0 80 26" xmlns="http://www.w3.org/2000/svg">
                            <text x="0" y="20" font-family="Arial, sans-serif" font-size="18" font-weight="bold" fill="#D32F2F" letter-spacing="1.5">whoosh</text>
                        </svg>
                    </td>
                </tr>

                <!-- PASSENGER AND CLASS INFO -->
                <tr>
                    <td style="padding:0 40px 10px;">
                        <p style="margin:0;font-size:14px;color:#333;line-height:1.6;">
                            <strong>${data.userName}</strong> | ${data.coachClass.displayName} ${seats} <strong>$formattedPrice</strong>
                        </p>
                        <p style="margin:5px 0 0 0;font-size:13px;color:#666;">
                            Tiket type: Adult ticket
                        </p>
                    </td>
                </tr>

                <!-- DIVIDER -->
                <tr>
                    <td style="padding:15px 40px;">
                        <div style="border-bottom:1px dashed #E0E0E0;"></div>
                    </td>
                </tr>

                <!-- TOTAL PAYMENT -->
                <tr>
                    <td style="padding:0 40px 20px;">
                        <p style="margin:0;font-size:16px;color:#333;">
                            <strong>Total Payment: $formattedPrice</strong>
                        </p>
                    </td>
                </tr>

                <!-- VAT INFO -->
                <tr>
                    <td style="padding:0 40px 30px;">
                        <p style="margin:0 0 5px 0;font-size:12px;color:#999;">
                            VAT not included
                        </p>
                        <p style="margin:0;font-size:12px;color:#999;">
                            VAT fees are waived based on article 16b of the Tax Harmonization Law
                        </p>
                    </td>
                </tr>

                <!-- FOOTER -->
                <tr>
                    <td style="padding:30px 40px;background:#F9F9F9;border-top:1px solid #E0E0E0;">
                        <p style="margin:0;font-size:12px;color:#999;line-height:1.6;">
                            This email was generated automatically, please do not reply, if you have questions or need help please contact e-mail <a href="mailto:csrb@kcic.co.id" style="color:#1976D2;text-decoration:none;">csrb@kcic.co.id</a>
                        </p>
                    </td>
                </tr>

            </table>

        </body>
        </html>
        """.trimIndent()
    }
}
