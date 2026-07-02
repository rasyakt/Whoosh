# Whoossh - High-Speed Rail Ticket Booking System

Whoossh adalah aplikasi mobile Android modern yang dirancang untuk memberikan pengalaman pemesanan tiket kereta cepat Whoosh yang mulus, cepat, dan intuitif. Dibangun dengan teknologi terbaru dari ekosistem Android, aplikasi ini menawarkan antarmuka yang bersih dan performa tinggi untuk pengguna.

## Fitur Utama

Aplikasi ini mencakup berbagai modul fungsional yang lengkap:

### 1. **Sistem Autentikasi & Profil**
- **Login & Register**: Masuk ke akun Anda atau buat akun baru dengan validasi data yang aman.
- **Profil Pengguna**: Kelola informasi pribadi Anda, termasuk foto profil dan detail kontak.
- **Keamanan**: Fitur ganti kata sandi dan pengaturan privasi.
- **Biometric Login**: Dukungan login cepat menggunakan sidik jari atau wajah (Biometric Auth).

### 2. **Pemesanan Tiket (Booking)**
- **Pencarian Jadwal**: Cari jadwal kereta berdasarkan rute asal, tujuan, dan tanggal keberangkatan.
- **Pemilihan Kursi (Seat Selection)**: Pilih gerbong dan kursi secara visual dan real-time.
- **Manajemen Penumpang**: Tambahkan data penumpang dengan mudah dari daftar tersimpan.
- **Ringkasan Pesanan**: Tinjau detail perjalanan sebelum melakukan pembayaran.

### 3. **E-Ticket & Riwayat Perjalanan**
- **Tiket Aktif**: Lihat tiket yang sudah dibayar lengkap dengan QR Code untuk boarding.
- **Riwayat Perjalanan**: Pantau semua perjalanan yang telah dilakukan sebelumnya.
- **Tiket Belum Dibayar**: Kelola pesanan yang masih menunggu pembayaran (Unpaid Tickets).

### 4. **Fitur Pendukung**
- **Promo & Diskon**: Lihat daftar promo terbaru yang tersedia untuk perjalanan Anda.
- **Pusat Bantuan**: Akses panduan dan bantuan langsung dari aplikasi.
- **Pengaturan Bahasa & Notifikasi**: Sesuaikan pengalaman aplikasi sesuai preferensi Anda.
- **Notifikasi Email**: Integrasi pengiriman konfirmasi tiket melalui email (SMTP).

## Teknologi (Tech Stack)

Aplikasi Whoossh dikembangkan menggunakan standar industri modern:

- **Bahasa Pemrograman**: [Kotlin](https://kotlinlang.org/) (100%)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/compose) (Material 3)
- **Arsitektur**: MVVM (Model-View-ViewModel) dengan Clean Architecture
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & OkHttp untuk komunikasi API
- **Data Parsing**: Gson untuk serialisasi JSON
- **Dependency Management**: Gradle Version Catalog (libs.versions.toml)
- **Email System**: JavaMail/Android Mail API untuk pengiriman notifikasi
- **Keamanan**: Android Biometric Library

## Struktur Proyek

```text
app/src/main/java/com/example/whoossh/
├── api/            # Konfigurasi Retrofit & Endpoint API
├── data/           # Layer data (Local & Remote)
├── model/          # Data classes (Request/Response)
├── navigation/     # Navigasi antar layar menggunakan Compose Nav
├── repository/     # Abstraksi sumber data
├── ui/             # Komponen UI
│   ├── screens/    # Seluruh layar aplikasi (Dashboard, Login, dll)
│   ├── components/ # Komponen UI yang reusable
│   └── theme/      # Definisi warna, tipografi, dan tema Whoosh
├── viewmodel/      # Logika bisnis dan state management
└── utils/          # Fungsi pembantu (Email, Validator, QR Generator)
```

## Memulai (Getting Started)

### Prasyarat
- Android Studio Ladybug (2024.2.1) atau versi lebih baru.
- JDK 11 atau 17.
- API Level minimal 24 (Android 7.0 Nougat).

### Instalasi
1. Clone repositori ini:
   ```bash
   git clone https://github.com/username/Whoossh.git
   ```
2. Buka proyek di Android Studio.
3. Tunggu proses Sinkronisasi Gradle selesai.
4. Pastikan backend API (PHP) Anda sudah berjalan.
5. Jalankan aplikasi pada Emulator atau Perangkat Fisik.

## Konfigurasi API

Aplikasi ini terhubung ke backend PHP. Anda dapat mengatur URL API di `ApiClient.kt`:

```kotlin
private const val BASE_URL_DEV = "http://YOUR_LOCAL_IP/whoossh_api/"
private const val BASE_URL_PROD = "https://api.whoosh.id/v1/"
```

## Notifikasi Email

Untuk fitur pengiriman email, aplikasi menggunakan kredensial SMTP yang dikonfigurasi di `build.gradle.kts` (Build Config). Pastikan untuk menggunakan App Password jika menggunakan Gmail.

## Lisensi

Proyek ini dilisensikan di bawah [MIT License](LICENSE).