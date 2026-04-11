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
                setFrom(InternetAddress(SENDER_EMAIL, "Whoosh Ticket"))
                setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
                )
                subject = "E-Ticket Whoosh — ${bookingData.bookingCode}"
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
     * Membuat HTML template e-ticket yang profesional.
     */
    private fun buildHtmlBody(data: BookingData): String {
        val formattedPrice = TicketUtils.formatRupiah(data.totalPrice)
        val formattedPricePerTicket = TicketUtils.formatRupiah(data.pricePerTicket)
        val seats = data.selectedSeats.sorted().joinToString(", ").ifEmpty { "-" }

        return """
        <!DOCTYPE html>
        <html lang="id">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>E-Ticket Whoosh</title>
        </head>
        <body style="margin:0;padding:8px;background-color:#f0f0f0;font-family:Arial,sans-serif;">

            <table width="100%" cellpadding="0" cellspacing="0" style="max-width:380px;margin:0 auto;background:#ffffff;border-radius:8px;overflow:hidden;">

                <!-- HEADER -->
                <tr>
                    <td style="background-color:#D32F2F;padding:10px 16px;text-align:center;">
                        <div style="color:#fff;font-size:16px;font-weight:bold;letter-spacing:2px;">WHOOSH</div>
                        <div style="color:rgba(255,255,255,0.8);font-size:9px;margin-top:1px;">Kereta Cepat Indonesia</div>
                    </td>
                </tr>

                <!-- STATUS -->
                <tr>
                    <td style="background:#E8F5E9;padding:5px;text-align:center;border-bottom:1px solid #C8E6C9;">
                        <span style="color:#2E7D32;font-size:10px;font-weight:bold;">&#10003; Pembelian Tiket Berhasil</span>
                    </td>
                </tr>

                <!-- BOOKING CODE -->
                <tr>
                    <td style="padding:10px 16px 8px;">
                        <table width="100%" cellpadding="0" cellspacing="0" style="border:1px dashed #FFB74D;border-radius:4px;background:#FFF8E1;">
                            <tr>
                                <td style="padding:7px;text-align:center;">
                                    <div style="color:#E65100;font-size:8px;text-transform:uppercase;letter-spacing:1px;margin-bottom:2px;">Kode Booking</div>
                                    <div style="color:#BF360C;font-size:18px;font-weight:bold;letter-spacing:2px;">${data.bookingCode}</div>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <!-- JOURNEY -->
                <tr>
                    <td style="padding:0 16px 6px;">
                        <div style="color:#757575;font-size:8px;text-transform:uppercase;letter-spacing:1px;font-weight:bold;margin-bottom:8px;">Detail Perjalanan</div>
                        <table width="100%" cellpadding="0" cellspacing="0">
                            <tr>
                                <td width="35%" style="vertical-align:middle;">
                                    <div style="color:#C62828;font-size:22px;font-weight:bold;line-height:1.1;">${data.departureTime}</div>
                                    <div style="color:#757575;font-size:10px;margin-top:3px;">${data.originStation}</div>
                                </td>
                                <td width="30%" style="text-align:center;vertical-align:middle;padding:0 4px;">
                                    <div style="color:#9E9E9E;font-size:9px;">${data.duration} min</div>
                                    <div style="color:#9E9E9E;font-size:13px;line-height:1;">&#8594;</div>
                                </td>
                                <td width="35%" style="text-align:right;vertical-align:middle;">
                                    <div style="color:#C62828;font-size:22px;font-weight:bold;line-height:1.1;">${data.arrivalTime}</div>
                                    <div style="color:#757575;font-size:10px;margin-top:3px;">${data.destinationStation}</div>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <!-- DIVIDER -->
                <tr>
                    <td style="padding:6px 16px;">
                        <div style="border-top:1px solid #EEEEEE;"></div>
                    </td>
                </tr>

                <!-- INFO TABLE (label left, value right) -->
                <tr>
                    <td style="padding:0 16px;">
                        <table width="100%" cellpadding="0" cellspacing="0" style="font-size:11px;">
                            <tr>
                                <td style="color:#9E9E9E;padding:4px 0;width:45%;">Penumpang</td>
                                <td style="color:#212121;text-align:right;padding:4px 0;">${data.userName}</td>
                            </tr>
                            <tr>
                                <td style="color:#9E9E9E;padding:4px 0;">Tanggal</td>
                                <td style="color:#212121;text-align:right;padding:4px 0;">${data.departureDate}</td>
                            </tr>
                            <tr>
                                <td style="color:#9E9E9E;padding:4px 0;">Kelas</td>
                                <td style="color:#212121;text-align:right;padding:4px 0;">${data.coachClass.displayName}</td>
                            </tr>
                            <tr>
                                <td style="color:#9E9E9E;padding:4px 0;">Gerbong</td>
                                <td style="color:#212121;text-align:right;padding:4px 0;">Gerbong ${data.selectedCarriage}</td>
                            </tr>
                            <tr>
                                <td style="color:#9E9E9E;padding:4px 0;">Kursi</td>
                                <td style="color:#212121;text-align:right;padding:4px 0;">$seats</td>
                            </tr>
                            <tr>
                                <td style="color:#9E9E9E;padding:4px 0;">Jumlah Tiket</td>
                                <td style="color:#212121;text-align:right;padding:4px 0;">${data.ticketCount} tiket</td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <!-- DIVIDER -->
                <tr>
                    <td style="padding:0 16px;">
                        <div style="border-top:1px solid #EEEEEE;"></div>
                    </td>
                </tr>

                <!-- PAYMENT -->
                <tr>
                    <td style="padding:7px 16px;">
                        <table width="100%" cellpadding="0" cellspacing="0" style="font-size:10px;">
                            <tr>
                                <td style="color:#9E9E9E;">Harga per tiket</td>
                                <td style="color:#424242;text-align:right;">$formattedPricePerTicket</td>
                            </tr>
                            <tr>
                                <td style="color:#9E9E9E;padding-top:2px;">Jumlah</td>
                                <td style="color:#424242;text-align:right;padding-top:2px;">&#215;${data.ticketCount}</td>
                            </tr>
                            <tr>
                                <td colspan="2" style="padding-top:5px;">
                                    <div style="border-top:1px solid #EEEEEE;"></div>
                                </td>
                            </tr>
                            <tr>
                                <td style="color:#C62828;font-size:11px;font-weight:700;padding-top:4px;">Total Pembayaran</td>
                                <td style="color:#C62828;font-size:14px;font-weight:800;text-align:right;padding-top:4px;">$formattedPrice</td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <!-- QR NOTICE -->
                <tr>
                    <td style="padding:0 16px 10px;">
                        <div style="background:#F3E5F5;border-radius:4px;padding:7px;text-align:center;">
                            <div style="color:#6A1B9A;font-size:9px;font-weight:700;">Tunjukkan QR Code di aplikasi Whoosh saat boarding</div>
                        </div>
                    </td>
                </tr>

                <!-- DEEP LINK CTA -->
                <tr>
                    <td style="padding:5px 16px 15px;text-align:center;">
                        <a href="https://whoosh.id/ticket/${data.bookingCode}" 
                           style="display:inline-block;padding:12px 24px;background-color:#D32F2F;color:#ffffff;text-decoration:none;font-size:12px;font-weight:bold;border-radius:6px;box-shadow:0 2px 4px rgba(0,0,0,0.1);">
                           Tunjukkan QR di Aplikasi
                        </a>
                    </td>
                </tr>

                <!-- FOOTER -->
                <tr>
                    <td style="background:#FAFAFA;padding:7px 16px;border-top:1px solid #EEEEEE;text-align:center;">
                        <div style="color:#BDBDBD;font-size:8px;">Email otomatis dari sistem Whoosh &bull; &copy; 2026 PT KCIC</div>
                    </td>
                </tr>

            </table>

        </body>
        </html>
        """.trimIndent()
    }
}
