package com.example.whoossh.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val LocalLanguage = compositionLocalOf { "Bahasa Indonesia" }

val enTranslations = mapOf(
    "Pilih Bahasa" to "Choose Language",
    "Pilih bahasa yang ingin digunakan pada aplikasi" to "Choose the language you want to use in the application",
    "Perubahan bahasa akan diterapkan pada seluruh halaman aplikasi." to "Language changes will be applied to all pages of the application.",
    "Bahasa" to "Language",
    "Pilih" to "Select",
    "Simpan" to "Save",
    "Batal" to "Cancel",
    "Keluar" to "Logout",
    "Konfirmasi Keluar" to "Confirm Logout",
    "Apakah Anda yakin ingin keluar dari akun Anda?" to "Are you sure you want to log out of your account?",
    "Ya, Batalkan" to "Yes, Cancel",
    "Tidak" to "No",
    "Tutup" to "Close",
    "Selesai" to "Done",
    "OK" to "OK",
    "Beranda" to "Home",
    "Tiket Saya" to "My Tickets",
    "Akun" to "Account",
    "Cari Tiket" to "Search Ticket",
    "Dari" to "From",
    "Ke" to "To",
    "Tanggal Berangkat" to "Departure Date",
    "Penumpang" to "Passenger",
    "Pilih tanggal" to "Select date",
    "Pilih Penumpang" to "Select Passenger",
    "Dewasa" to "Adult",
    "Bayi" to "Infant",
    "Jadwal Perjalanan" to "Travel Schedule",
    "Tersedia" to "Available",
    "Tidak ada jadwal tersedia" to "No schedule available",
    "Mulai dari" to "Starting from",
    "Pilih Jenis Gerbong" to "Select Coach Type",
    "Gerbong" to "Coach",
    "Kursi Terpilih:" to "Selected Seats:",
    "Selanjutnya" to "Next",
    "Sebelumnya" to "Previous",
    "Total Pembayaran" to "Total Payment",
    "Lanjutkan" to "Continue",
    "Detail Perjalanan" to "Trip Details",
    "Stasiun Pemberhentian" to "Stop Stations",
    "Tiba" to "Arrive",
    "Rincian Pembayaran" to "Payment Details",
    "Diskon" to "Discount",
    "Total" to "Total",
    "Bayar" to "Pay",
    "Memuat tiket Anda..." to "Loading your tickets...",
    "Belum ada riwayat perjalanan" to "No travel history yet",
    "Riwayat perjalanan Anda akan\nmuncul di sini" to "Your travel history will\nappear here",
    "Detail Pembayaran" to "Payment Details",
    "Konfirmasi Pembayaran" to "Confirm Payment",
    "Pembelian Berhasil!" to "Purchase Successful!",
    "Tiket Anda telah berhasil dipesan." to "Your ticket has been successfully booked.",
    "Lihat E-Ticket" to "View E-Ticket",
    "Tiket Dibatalkan" to "Ticket Cancelled",
    "Batalkan Tiket?" to "Cancel Ticket?",
    "Apakah Anda yakin ingin membatalkan tiket ini?" to "Are you sure you want to cancel this ticket?",
    "Informasi Tambahan" to "Additional Information",
    "Informasi Refund" to "Refund Information",
    "Refund" to "Refund",
    "Pengaturan Akun" to "Account Settings",
    "Edit Profil" to "Edit Profile",
    "Ubah Password" to "Change Password",
    "Tiket ini tidak dapat di refund" to "This ticket cannot be refunded",
    "Tiket ini tidak dapat di reschedule" to "This ticket cannot be rescheduled",
    "Pengaturan Notifikasi" to "Notification Settings",
    "Pengaturan Keamanan" to "Security Settings",
    "Bantuan & Dukungan" to "Help & Support",
    "Pertanyaan Umum (FAQ)" to "Frequently Asked Questions (FAQ)",
    "Hubungi Kami" to "Contact Us",
    "Kebijakan Privasi" to "Privacy Policy",
    "Ketentuan Layanan" to "Terms of Service",
    "Versi Aplikasi" to "App Version",
    "Masuk" to "Login",
    "Daftar" to "Register",
    "Email" to "Email",
    "Password" to "Password",
    "Belum punya akun? Daftar di sini" to "Don't have an account? Register here",
    "Sudah punya akun? Masuk di sini" to "Already have an account? Login here",
    "Nama Lengkap" to "Full Name",
    "Nomor HP" to "Phone Number",
    "Konfirmasi Password" to "Confirm Password",
    "Buat Akun Baru" to "Create New Account",
    "Daftar untuk mulai memesan tiket" to "Register to start booking tickets",
    "Selamat Datang!" to "Welcome!",
    "Masuk untuk melanjutkan" to "Login to continue",
    "Data penumpang pertama akan digunakan untuk membuat akun Anda" to "The first passenger's data will be used to create your account",
    "Tambahkan penumpang (maks. 15)" to "Add passengers (max 15)",
    "Penumpang Terpilih" to "Selected Passengers",
    "Lebih banyak" to "More",
    "Layanan" to "Services",
    "Promo" to "Promo",
    "Belum ada promo tersedia" to "No promo available yet",
    "Kode Promo:" to "Promo Code:",
    "Gunakan" to "Use",
    "Peta Rute Whoosh" to "Whoosh Route Map",
    "Kereta Cepat Indonesia" to "Indonesian High-Speed Railway",
    "Beroperasi pada hari Senin-Sabtu" to "Operates Monday-Saturday",
    "Waktu pembayaran telah habis" to "Payment time has expired",
    "Mohon selesaikan pembayaran sebelum waktu habis." to "Please complete the payment before the time expires.",
    "Tiket ini telah berhasil direfund." to "This ticket has been successfully refunded.",
    "Data Rekening Refund" to "Refund Account Data",
    "Nama Bank" to "Bank Name",
    "Nomor Rekening" to "Account Number",
    "Nama Pemilik Rekening" to "Account Holder Name",
    "Simpan data rekening Anda untuk mempercepat proses refund tiket." to "Save your account data to speed up the ticket refund process.",
    "Kelola notifikasi yang ingin Anda terima" to "Manage notifications you want to receive",
    "Password Lama" to "Old Password",
    "Password Baru" to "New Password",
    "Konfirmasi Password Baru" to "Confirm New Password",
    "Ubah Foto Profil" to "Change Profile Picture",
    "Temukan jawaban dari pertanyaan yang sering diajukan atau hubungi tim kami" to "Find answers to frequently asked questions or contact our team",
    "Ada yang bisa kami bantu?" to "How can we help you?",
    "Berhenti" to "Stop",
    "Kondisi Deteksi & Pembatalan Tiket" to "Ticket Detection & Cancel Condition",
    "Biaya Pembatalan (10%)" to "Cancellation Fee (10%)",
    "Tarif" to "Fare",
    "Password (min. 6 karakter)" to "Password (min. 6 characters)",
    "No. Identitas " to "Identity No. ",
    "Batas Waktu Pembayaran : " to "Payment Countdown : ",
    "Tambahkan penumpang (maks. 15)" to "Add passengers (max. 15)",
    "Pertanyaan Umum (FAQ)" to "Frequently Asked Questions (FAQ)",
    "Biaya Reschedule (25%)" to "Reschedule Fee (25%)",
    "Gagal memperbarui profil. Periksa data Anda." to "Failed to update profile. Please check your data.",
    "Password berhasil diubah" to "Password successfully changed",
    "Please fill all required fields" to "Please fill all required fields",
    "Please select a new schedule" to "Please select a new schedule",
    "Profil berhasil diperbarui" to "Profile successfully updated",
    "Tekan sekali lagi untuk keluar" to "Press again to exit",
    "Email dan password tidak boleh kosong" to "Email and password cannot be empty",
    "Format email tidak valid" to "Invalid email format",
    "Nomor HP minimal 10 digit" to "Phone number must be at least 10 digits",
    "Password minimal 6 karakter" to "Password must be at least 6 characters",
    "Semua field harus diisi" to "All fields must be filled",
    "Konfirmasi password tidak cocok" to "Password confirmation does not match",
    "Password baru minimal 6 karakter" to "New password must be at least 6 characters",
    "Password baru harus berbeda" to "New password must be different",
    "Password lama salah" to "Old password incorrect",
    "Cancellation Fee (25%):" to "Biaya Pembatalan (25%):",
    "Refund will be sent to:" to "Refund akan dikirimkan ke:",
    "Warning: No refund account registered!" to "Peringatan: Belum ada rekening refund terdaftar!",
    "Amount will be returned to your registered refund account" to "Dana akan dikembalikan ke rekening refund terdaftar Anda",
    "a/n" to "a/n",
    "• Refund will be processed within 3-7 business days" to "• Refund akan diproses dalam 3-7 hari kerja",
    "• This action cannot be undone" to "• Tindakan ini tidak dapat dibatalkan",
    "Belum ada tiket yang belum dibayar" to "No unpaid tickets yet",
    "Belum ada tiket aktif" to "No active tickets yet",
    "Belum ada riwayat tiket" to "No ticket history yet",
    "Selesaikan pembayaran untuk\nmendapatkan tiket Anda" to "Complete payment to\nget your tickets",
    "Tiket yang sudah dibayar\nakan muncul di sini" to "Paid tickets will\nappear here",
    "Riwayat tiket yang sudah\ndigunakan akan muncul di sini" to "Used ticket history will\nappear here",
    "Stasiun" to "Station",
    "Berangkat" to "Depart",
    "WIB" to "WIB",
    "Refunded" to "Refund Berhasil",
    "Cancelled" to "Dibatalkan",
    "Paid" to "Sudah Dibayar",
    "Used" to "Sudah Digunakan",
    "Unpaid" to "Belum Dibayar",
    "Pengembalian dana (Refund) telah berhasil" to "Refund has been successful",
    "Tiket ini telah dibatalkan" to "This ticket has been cancelled",
    "Tiket ini telah berhasil direfund. Dana sebesar 75% dari total bayar (setelah biaya administrasi 25%) telah dikirimkan ke rekening yang Anda daftarkan." to "This ticket has been successfully refunded. 75% of the total amount (after 25% admin fee) has been sent to your registered account.",
    "• Timer pembayaran akan dihentikan\n• Kursi akan dilepas" to "• Payment timer will be stopped\n• Seats will be released",
    "Penumpang" to "Passenger",
    "Tanggal" to "Date",
    "Durasi" to "Duration",
    "Jenis Gerbong" to "Coach Type",
    "Nomor Gerbong" to "Coach Number",
    "Nomor Kursi" to "Seat Number",
    "Jumlah Tiket" to "Ticket Count",
    "Harga per tiket" to "Price per ticket",
    "Detail Perjalanan" to "Trip Details",
    "Rincian Pembayaran" to "Payment Details",
    "Total Pembayaran" to "Total Payment",
    "Konfirmasi Pembelian" to "Confirm Purchase",
    "Total Perjalanan" to "Total Trips",
    "Tiket Aktif" to "Active Tickets",
    "Pengaturan" to "Settings",
    "Lainnya" to "Others",
    "Pengguna Tamu" to "Guest User",
    "Silakan login untuk akses penuh" to "Please login for full access",
    "Kelola Penumpang" to "Manage Passengers",
    "Promo & Diskon" to "Promo & Discount",
    "Ubah Kata Sandi" to "Change Password",
    "Pusat Bantuan" to "Help Center",
    "Menyimpan..." to "Saving...",
    "Simpan Perubahan" to "Save Changes",
    "Pastikan password baru Anda minimal 6 karakter dan berbeda dari password sebelumnya." to "Make sure your new password is at least 6 characters and different from the previous one.",
    "Memproses..." to "Processing...",
    "Promo & Penawaran" to "Promo & Offers",
    "Dapatkan info promo dan diskon terbaru" to "Get the latest promo and discount info",
    "Info Perjalanan" to "Travel Info",
    "Pengingat jadwal dan perubahan perjalanan" to "Schedule reminders and travel changes",
    "Update Aplikasi" to "App Updates",
    "Informasi fitur baru dan pembaruan app" to "New features and app updates info",
    "Email Notifikasi" to "Email Notifications",
    "Terima notifikasi melalui email" to "Receive notifications via email",
    "Login Biometrik" to "Biometric Login",
    "Gunakan sidik jari atau face ID untuk login" to "Use fingerprint or face ID to login",
    "Simpan Data Login" to "Save Login Data",
    "Tetap login meskipun aplikasi ditutup" to "Stay logged in even when the app is closed",
    "Perlindungan Data" to "Data Protection",
    "Data pribadi Anda dilindungi dengan enkripsi end-to-end dan disimpan secara aman di server kami." to "Your personal data is protected with end-to-end encryption and stored securely on our servers.",
    "Keamanan Transaksi" to "Transaction Security",
    "Setiap transaksi dilindungi oleh protokol keamanan standar industri perbankan." to "Every transaction is protected by banking industry standard security protocols.",
    "Hak Akses Data" to "Data Access Rights",
    "Anda memiliki hak penuh untuk mengakses, mengubah, atau menghapus data pribadi Anda kapan saja." to "You have full rights to access, modify, or delete your personal data at any time.",
    "Versi Aplikasi: 1.0.0\nTerakhir diperbarui: April 2026" to "App Version: 1.0.0\nLast updated: April 2026",
    "Bagaimana cara memesan tiket?" to "How to book a ticket?",
    "Pilih stasiun asal dan tujuan di halaman utama, tentukan tanggal keberangkatan dan jumlah tiket, lalu tekan \"Cari Jadwal\". Pilih jadwal yang diinginkan, pilih kelas gerbong dan kursi, lalu konfirmasi pemesanan Anda." to "Select origin and destination stations on the main page, set departure date and number of tickets, then press \"Search Schedule\". Choose your desired schedule, select coach class and seats, then confirm your booking.",
    "Apakah bisa membatalkan tiket?" to "Can I cancel my ticket?",
    "Pembatalan tiket dapat dilakukan maksimal 3 jam sebelum keberangkatan. Silakan hubungi customer service kami untuk proses pembatalan dan pengembalian dana." to "Ticket cancellation can be done up to 3 hours before departure. Please contact our customer service for cancellation and refund process.",
    "Berapa lama tiket berlaku?" to "How long is the ticket valid?",
    "Tiket berlaku hanya untuk tanggal dan jadwal yang dipilih saat pemesanan. Pastikan Anda datang minimal 30 menit sebelum keberangkatan." to "Tickets are valid only for the date and schedule selected during booking. Make sure you arrive at least 30 minutes before departure.",
    "Bagaimana cara melihat e-ticket?" to "How to view e-ticket?",
    "Setelah pemesanan berhasil, e-ticket akan tersedia di menu \"Tiket Saya\". Anda bisa menunjukkan QR code pada e-ticket saat boarding." to "After successful booking, e-ticket will be available in \"My Tickets\" menu. You can show the QR code on the e-ticket when boarding.",
    "Metode pembayaran apa saja yang tersedia?" to "What payment methods are available?",
    "Saat ini Whoosh mendukung pembayaran melalui transfer bank, e-wallet (GoPay, OVO, DANA), dan kartu kredit/debit." to "Currently Whoosh supports payment via bank transfer, e-wallet (GoPay, OVO, DANA), and credit/debit cards.",
    "Apakah bisa reschedule tiket?" to "Can I reschedule my ticket?",
    "Reschedule dapat dilakukan maksimal 24 jam sebelum keberangkatan melalui menu \"Tiket Saya\" atau menghubungi customer service." to "Reschedule can be done up to 24 hours before departure through \"My Tickets\" menu or by contacting customer service.",
    "Telepon" to "Phone",
    "Senin - Jumat, 08:00 - 21:00 WIB" to "Monday - Friday, 08:00 - 21:00 WIB",
    "Respons dalam 1x24 jam" to "Response within 24 hours",
    "Website" to "Website",
    "Kunjungi website resmi kami" to "Visit our official website",
    "Chat dengan Customer Service" to "Chat with Customer Service",
    "Edit" to "Edit",
    "Delete" to "Delete",
    "Tiket" to "Ticket(s)",
    "Tersedia" to "Available",
    "Terpilih" to "Selected",
    "Terisi" to "Occupied",
    "Diskon" to "Discount",
    "Kembali" to "Back",
    "Jadwal Whoosh & KA Feeder" to "Whoosh & Feeder Train Schedule",
    "Jadwal perjalanan Kereta Cepat Whoos..." to "High-Speed Train travel schedule...",
    "Pengembalian Dana / Perubahan Jad..." to "Refund / Schedule Change...",
    "Ketentuan Boarding" to "Boarding Terms",
    "Proses memberikan izin kepada pelangg..." to "Process of granting permission to passengers...",
    "Syarat & Ketentuan KA Feeder" to "Feeder Train Terms & Conditions",
    "Beranda" to "Home",
    "Tiket" to "Tickets",
    "Jadwal" to "Schedule",
    "Lihat Jadwal" to "View Schedule"

)


val idTranslations = mapOf(
    "Add Infant" to "Tambah Bayi",
    "Add Passenger" to "Tambah Penumpang",
    "Add an infant passenger (under 3 years old) to this booking." to "Tambahkan penumpang bayi (di bawah 3 tahun) pada pemesanan ini.",
    "Adult ticket" to "Tiket Dewasa",
    "Coach" to "Kelas",
    "Done" to "Selesai",
    "Seat" to "Kursi",
    "Identity No." to "No. Identitas",
    "Total payment amount : " to "Total pembayaran : ",
    "Are you sure you want to request a refund for this ticket?" to "Apakah Anda yakin ingin meminta pengembalian dana untuk tiket ini?",
    "Available Schedules:" to "Jadwal Tersedia:",
    "Birth Certificate No. *" to "No. Akta Kelahiran *",
    "Booking No : " to "No Pesanan : ",
    "Cancellation Fee (10%):" to "Biaya Pembatalan (10%):",
    "Cannot Refund" to "Tidak Bisa Refund",
    "Cannot Reschedule" to "Tidak Bisa Reschedule",
    "Click to Refresh Status" to "Klik untuk Muat Ulang Status",
    "Complete Payment" to "Selesaikan Pembayaran",
    "Confirm Password" to "Konfirmasi Password",
    "Confirm Refund" to "Konfirmasi Refund",
    "Confirm Reschedule" to "Konfirmasi Reschedule",
    "Current Schedule:" to "Jadwal Saat Ini:",
    "Date of Birth *" to "Tanggal Lahir *",
    "Email Address" to "Alamat Email",
    "Enter birth certificate number" to "Masukkan nomor akta kelahiran",
    "Enter infant's full name" to "Masukkan nama lengkap bayi",
    "Enter password" to "Masukkan password",
    "Enter your name on your ID Card" to "Masukkan nama sesuai KTP",
    "Full Name" to "Nama Lengkap",
    "Important Notes:" to "Catatan Penting:",
    "Infant Name *" to "Nama Bayi *",
    "Note: Reschedule is subject to seat availability." to "Catatan: Reschedule bergantung pada ketersediaan kursi.",
    "Order Number: " to "Nomor Pesanan: ",
    "Order Time: " to "Waktu Pesanan: ",
    "Original Amount:" to "Jumlah Awal:",
    "Passenger" to "Penumpang",
    "Password" to "Password",
    "Pay" to "Bayar",
    "Payment Succeeded" to "Pembayaran Berhasil",
    "Refund" to "Refund",
    "Refund Amount:" to "Jumlah Refund:",
    "Reschedule must be done at least 2 hours before departure" to "Reschedule harus dilakukan minimal 2 jam sebelum waktu keberangkatan",
    "Refund must be requested at least 2 hours before departure" to "Refund harus dilakukan minimal 2 jam sebelum waktu keberangkatan",
    "Refund Ticket" to "Refund Tiket",
    "Remaining payment time: " to "Sisa waktu pembayaran: ",
    "Reminder" to "Pengingat",
    "Reschedule" to "Ubah Jadwal",
    "Reschedule Fee" to "Biaya Reschedule",
    "Reschedule Ticket" to "Reschedule Tiket",
    "Return Trip" to "Perjalanan",
    "Rules" to "Aturan",
    "Choose Date" to "Pilih Tanggal",
    "Reschedule Fee (25%):" to "Biaya Reschedule (25%):",
    "Same day reschedule: No fee" to "Reschedule di hari yang sama: Gratis",
    "Save this passenger for future bookings" to "Simpan penumpang ini untuk pemesanan berikutnya",
    "Search country..." to "Cari negara...",
    "Select New Date:" to "Pilih Tanggal Baru:",
    "Total Price" to "Total Harga",
    "Virtual Account" to "Virtual Account",
    "Ticket Detection & Cancel Condition" to "Kondisi Deteksi & Pembatalan Tiket",
    "1. BA baby under three\n2. Adults 17 years of age or older\n3. The name and identity number must be in accordance with that contained in the identity certificate (KTP/ Passport), when the passenger age below 17 years can be filled in with the date of birth of" to "1. Bayi di bawah tiga tahun\n2. Dewasa berusia 17 tahun ke atas\n3. Nama dan nomor identitas harus sesuai dengan KTP/Paspor, apabila penumpang di bawah 17 tahun dapat diisi dengan tanggal lahir",
    "1. Please complete the online payment within the specified time.\n2. In case of late payment, the system will cancel the transaction.\n3. You will not be able to purchase additional tickets until you complete payment or cancel this order." to "1. Harap selesaikan pembayaran online dalam waktu yang ditentukan.\n2. Jika terjadi keterlambatan pembayaran, sistem akan membatalkan transaksi.\n3. Anda tidak dapat membeli tiket tambahan sampai Anda menyelesaikan pembayaran atau membatalkan pesanan ini.",
    "1. The ticket you purchased this time has been issued, You can enter the station with the QR code of the ticket, or enter the station after exchanging the paper ticket at the station window." to "1. Tiket yang Anda beli telah diterbitkan, Anda dapat memasuki stasiun dengan kode QR tiket, atau memasuki stasiun setelah menukarkan tiket fisik.",
    "2. After exchanging for a paper ticket, the ticket cannot be refunded or Rescheduled on the APP." to "2. Setelah ditukar dengan tiket fisik, tiket tidak dapat di-refund atau di-Reschedule di APP.",
    "3. You can save a screenshot of the current order details interface so that you can view the seat position when taking the bus." to "3. Anda dapat menyimpan tangkapan layar halaman detail pesanan ini untuk melihat posisi kursi Anda.",
    "Refund request submitted. You will receive " to "Permintaan refund terkirim. Anda akan menerima ",
    "Cancellation Fee (10%)" to "Biaya Pembatalan (10%)",
    "Cancellation Fee (10%):" to "Biaya Pembatalan (10%):",
    "Total Price" to "Total Harga",
    "Fare" to "Tarif",
    "Password (min. 6 characters)" to "Password (min. 6 karakter)",
    "Add an infant passenger (under 3 years old)" to "Tambahkan penumpang bayi (di bawah 3 tahun)",
    "Identity No. " to "No. Identitas ",
    "Payment Countdown : " to "Batas Waktu Pembayaran : ",
    "Tambahkan penumpang (maks. 15)" to "Tambahkan penumpang (maks. 15)",
    "Pertanyaan Umum (FAQ)" to "Pertanyaan Umum (FAQ)",
    "Reschedule Fee (25%)" to "Biaya Reschedule (25%)",
    "• Amount will be returned to your original payment method\n• Refund will be processed within 3-7 business days\n" to "• Dana akan dikembalikan ke metode pembayaran awal Anda\n• Refund akan diproses dalam waktu 3-7 hari kerja\n",
    "Please fill all required fields" to "Harap isi semua kolom yang diperlukan",
    "Please select a new schedule" to "Silakan pilih jadwal baru",
    "Failed to connect to server" to "Gagal terhubung ke server",
    "Refunded" to "Refund Berhasil",
    "Cancelled" to "Dibatalkan",
    "Paid" to "Sudah Dibayar",
    "Used" to "Sudah Digunakan",
    "Unpaid" to "Belum Dibayar",
    "Select Passenger Type" to "Pilih Jenis Penumpang",
    "Cancellation Fee (25%):" to "Biaya Pembatalan (25%):",
    "Refund will be sent to:" to "Refund akan dikirimkan ke:",
    "Warning: No refund account registered!" to "Peringatan: Belum ada rekening refund terdaftar!",
    "Amount will be returned to your registered refund account" to "Dana akan dikembalikan ke rekening refund terdaftar Anda",
    "a/n" to "a/n",
    "• Refund will be processed within 3-7 business days" to "• Refund akan diproses dalam 3-7 hari kerja",
    "• This action cannot be undone" to "• Tindakan ini tidak dapat dibatalkan",
    "Select Discount Type" to "Pilih Jenis Diskon",
    "Select Document Type" to "Pilih Jenis Dokumen",
    "Date of birth" to "Tanggal Lahir",
    "Passenger Type" to "Jenis Penumpang",
    "Discount type" to "Jenis Diskon",
    "Country/Region" to "Negara/Wilayah",
    "Document Type" to "Jenis Dokumen",
    "Expiry Date" to "Tanggal Kedaluwarsa",
    "Personal Information" to "Informasi Pribadi",
    "Certificate Information" to "Informasi Sertifikat",
    "Contact Information" to "Informasi Kontak",
    "Account Password" to "Password Akun",
    "Male" to "Laki-laki",
    "Female" to "Perempuan",
    "Please select a date of birth" to "Silakan pilih tanggal lahir",
    "Departure Time" to "Waktu Keberangkatan",
    "Seat Info" to "Informasi Kursi",
    "Class" to "Kelas",
    "Name" to "Nama",
    "Identity No." to "No. Identitas",
    "Infant Name *" to "Nama Bayi *",
    "Birth Certificate No. *" to "No. Akta Kelahiran *",
    "Date of Birth *" to "Tanggal Lahir *",
    "Important Notes:" to "Catatan Penting:",
    "Infants travel free of charge" to "Bayi bepergian gratis",
    "No separate seat required" to "Tidak perlu kursi terpisah",
    "Must be accompanied by an adult" to "Harus didampingi orang dewasa",
    "Age must be under 3 years old" to "Usia harus di bawah 3 tahun",
    "Add Infant" to "Tambah Bayi",
    "Add Passenger" to "Tambah Penumpang",
    "Submit" to "Kirim",
    "Create Account & Add" to "Buat Akun & Tambah",
    "Select" to "Pilih",
    "Cancel" to "Batal"

)

@Composable
fun String.tr(): String {
    val lang = LocalLanguage.current
    if (lang == "English") {
        enTranslations[this]?.let { return it }
        var translated = this
        if (translated.contains(" Penumpang")) {
            translated = translated.replace(" Penumpang", " Passenger(s)")
        }
        if (translated.contains(" penumpang")) {
            translated = translated.replace(" penumpang", " passenger(s)")
        }
        if (translated.contains(" Tiket")) {
            translated = translated.replace(" Tiket", " Ticket(s)")
        }
        if (translated.contains("Diskon ")) {
            translated = translated.replace("Diskon ", "Discount ")
        }
        if (translated.contains(" lainnya")) {
            translated = translated.replace(" lainnya", " others")
        }
        if (translated.contains(" tiket • ")) {
            translated = translated.replace(" tiket • ", " ticket(s) • ")
        }
        if (translated.contains(" • Gerbong ")) {
            translated = translated.replace(" • Gerbong ", " • Coach ")
        }
        if (translated.contains(" jadwal tersedia")) {
            translated = translated.replace(" jadwal tersedia", " schedule(s) available")
        }
        if (translated.contains("menit") || translated.contains("mnt")) {
            translated = translated.replace("menit", "mins").replace("mnt", "m")
        }
        if (translated.contains("Gerbong ")) {
            translated = translated.replace("Gerbong ", "Coach ")
        }
        if (translated.contains("Berlaku sampai")) {
            translated = translated.replace("Berlaku sampai", "Valid until")
        }
        return translated
    } else {
        idTranslations[this]?.let { return it }
        return this
    }
}


fun String.trStr(lang: String): String {
    if (lang == "English") {
        enTranslations[this]?.let { return it }
        var translated = this
        if (translated.contains(" Penumpang")) {
            translated = translated.replace(" Penumpang", " Passenger(s)")
        }
        if (translated.contains(" penumpang")) {
            translated = translated.replace(" penumpang", " passenger(s)")
        }
        if (translated.contains(" Tiket")) {
            translated = translated.replace(" Tiket", " Ticket(s)")
        }
        if (translated.contains("Diskon ")) {
            translated = translated.replace("Diskon ", "Discount ")
        }
        if (translated.contains(" lainnya")) {
            translated = translated.replace(" lainnya", " others")
        }
        if (translated.contains(" tiket • ")) {
            translated = translated.replace(" tiket • ", " ticket(s) • ")
        }
        if (translated.contains(" • Gerbong ")) {
            translated = translated.replace(" • Gerbong ", " • Coach ")
        }
        if (translated.contains(" jadwal tersedia")) {
            translated = translated.replace(" jadwal tersedia", " schedule(s) available")
        }
        if (translated.contains("menit") || translated.contains("mnt")) {
            translated = translated.replace("menit", "mins").replace("mnt", "m")
        }
        if (translated.contains("Gerbong ")) {
            translated = translated.replace("Gerbong ", "Coach ")
        }
        if (translated.contains("Berlaku sampai")) {
            translated = translated.replace("Berlaku sampai", "Valid until")
        }
        return translated
    } else {
        idTranslations[this]?.let { return it }
        return this
    }
}