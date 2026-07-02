# Whoosh Ticket

Whoosh Ticket adalah aplikasi mobile Android modern yang dirancang untuk memberikan pengalaman pemesanan tiket kereta cepat Whoosh (PT KCIC) secara cepat, aman, dan intuitif. Proyek ini dibangun sepenuhnya menggunakan Jetpack Compose dengan arsitektur MVVM (Model-View-ViewModel) yang terintegrasi secara langsung dengan Backend API berbasis PHP untuk seluruh operasi data.

---

## Daftar Isi
1. [Fitur Utama & Logika Bisnis](#-fitur-utama--logika-bisnis)
2. [Aturan Tarif & Rute (100% Factual)](#-aturan-tarif--rute-100-factual)
3. [Alur & Kebijakan Pasca-Pembelian](#-alur--kebijakan-pasca-pembelian)
4. [Teknologi & Libs (Tech Stack)](#-teknologi--libs-tech-stack)
5. [Arsitektur Proyek](#%EF%B8%8F-arsitektur-proyek)
6. [Struktur Folder](#-struktur-folder)
7. [Getting Started & Instalasi](#-getting-started--instalasi)
8. [Konfigurasi API & Email](#%EF%B8%8F-konfigurasi-api--email)

---

## Fitur Utama & Logika Bisnis

Aplikasi ini mencakup modul-modul fungsional lengkap yang disinkronkan secara real-time dengan backend:

### 1. Sistem Autentikasi & Profil Pengguna
*   **Login & Register**: Pendaftaran dan masuk akun dengan validasi data yang aman di sisi klien dan server.
*   **Autentikasi Biometrik**: Menggunakan `androidx.biometric` untuk login cepat berbasis sidik jari atau pemindaian wajah.
*   **Manajemen Profil**: Edit informasi pribadi (nama, email, nomor telepon) dan ubah kata sandi dengan verifikasi kata sandi lama.
*   **Keamanan Sesi**: Sesi pengguna disimpan secara lokal melalui [UserPreferences](file:///c:/Users/HYPE%20AMD/AndroidStudioProjects/Whoossh/app/src/main/java/com/example/whoossh/data/UserPreferences.kt) menggunakan `SharedPreferences` terenkripsi sederhana untuk menghindari login berulang.

### 2. Pemesanan Tiket (Booking)
*   **Pencarian Jadwal**: Cari kereta cepat berdasarkan stasiun asal, tujuan, tanggal keberangkatan, dan jumlah penumpang.
*   **Manajemen Penumpang Terintegrasi**:
    *   Mendukung tipe penumpang: **Dewasa (Adult)**, **Anak-anak (Child)**, dan **Bayi (Infant)**.
    *   Pengisian formulir lengkap dengan kewarganegaraan, tipe dokumen (KTP, Paspor, dll.), masa berlaku, email, dan nomor WhatsApp.
    *   Fitur simpan data penumpang untuk pemesanan cepat di masa mendatang (fitur CRUD Penumpang tersimpan).
    *   **Batas Pembelian**: Maksimal **10 tiket** per transaksi.
*   **Visual Seat Selection**: Pemilihan gerbong kereta (Carriage) dan kursi secara interaktif berdasarkan ketersediaan real-time yang diambil dari database (`get_occupied_seats.php`).

### 3. E-Ticket & Riwayat Perjalanan
*   **Tiket Menunggu Pembayaran (Unpaid)**: Dilengkapi dengan countdown timer pembayaran dan simulasi konfirmasi pembayaran.
*   **Boarding QR Code**: Tiket yang berhasil dibayar akan memuat QR Code boarding dinamis yang dihasilkan langsung menggunakan pustaka ZXing.
*   **Riwayat Perjalanan (Travel History)**: Menyimpan daftar tiket yang sudah digunakan (*Used*), dibatalkan (*Cancelled*), ataupun di-refund (*Refunded*).

### 4. Pusat Bantuan & Dukungan
*   Daftar FAQ (Pertanyaan Umum), kontak layanan pelanggan, serta dokumen Kebijakan Privasi dan Syarat Ketentuan yang dapat diakses langsung dari menu Akun.

### 5. Pengaturan Aplikasi
*   **Dual-Language**: Mendukung Bahasa Indonesia dan English yang diimplementasikan melalui sistem translasi runtime dinamis ([Translator.kt](file:///c:/Users/HYPE%20AMD/AndroidStudioProjects/Whoossh/app/src/main/java/com/example/whoossh/utils/Translator.kt)).
*   **Pengaturan Notifikasi**: Pengaktifan notifikasi promo, perjalanan, pembaruan aplikasi, dan notifikasi email secara modular.

---

## Aturan Tarif & Rute (100% Factual)

Sistem pemesanan tiket menggunakan aturan tarif dinamis dan pembatasan kelas yang disesuaikan dengan rute perjalanan:

### 1. Rute & Ketersediaan Kelas
Aplikasi melayani 4 stasiun utama: **Halim (Jakarta)**, **Karawang**, **Padalarang**, dan **Tegalluar (Bandung)**.
*   **Rute Pendek Khusus**: Perjalanan rute pendek **Halim ↔ Karawang** dan **Padalarang ↔ Tegalluar** hanya menyediakan kelas **Ekonomi Premium**.
*   **Rute Standar/Lainnya**: Menyediakan opsi kelas lengkap: **Ekonomi Premium**, **Bisnis**, dan **First Class/VIP**.

### 2. Struktur Tarif Tiket (Per Kursi)
*   **First Class / VIP**: **Rp 600.000** (Flat untuk seluruh rute yang menyediakannya)
*   **Bisnis**: **Rp 450.000** (Flat untuk seluruh rute yang menyediakannya)
*   **Ekonomi Premium (Tarif Dinamis)**:
    *   *Jarak 1 Stasiun (Rute Terpendek)*:
        *   Halim ↔ Karawang: **Rp 100.000**
        *   Padalarang ↔ Tegalluar: **Rp 75.000**
        *   Karawang ↔ Padalarang: **Rp 150.000**
    *   *Jarak 2 Stasiun (Rute Menengah)*:
        *   Tegalluar ↔ Karawang: **Rp 150.000**
        *   Halim ↔ Padalarang: **Rp 200.000**
    *   *Jarak 3 Stasiun (Rute Jauh)*:
        *   Halim → Tegalluar (Eastbound): **Rp 350.000** (Semua jam keberangkatan)
        *   Tegalluar → Halim (Westbound):
            *   Jam Keberangkatan sibuk (07:00 - 08:59 WIB): **Rp 325.000**
            *   Jam Keberangkatan biasa: **Rp 300.000**

---

## Alur & Kebijakan Pasca-Pembelian

Aplikasi menerapkan logika ketat untuk transaksi pasca-pembelian tiket aktif:

### 1. Penambahan Bayi (Add Infant)
*   Bayi (usia di bawah 3 tahun) dapat ditambahkan ke tiket dewasa utama secara gratis tanpa dikenakan biaya tambahan dan tidak mendapatkan kursi sendiri.
*   Hanya dapat ditambahkan pada tiket utama yang **sudah dibayar (Paid)**, belum digunakan (*isUsed = false*), dan belum dibatalkan (*isCancelled = false*).
*   Membutuhkan pengisian nama lengkap dan nomor akta kelahiran bayi.

### 2. Pengubahan Jadwal (Reschedule)
*   **Batas Waktu**: Reschedule wajib diajukan paling lambat **2 jam sebelum jadwal keberangkatan**.
*   **Biaya Administrasi**:
    *   Reschedule ke tanggal/hari yang **sama**: **Bebas Biaya (Rp 0)**.
    *   Reschedule ke tanggal/hari yang **berbeda**: Dikenakan biaya administrasi sebesar **25%** dari harga tiket asli.
*   Membutuhkan verifikasi keamanan biometrik pengguna saat konfirmasi reschedule.

### 3. Pengembalian Dana (Refund)
*   **Batas Waktu**: Pembatalan dan pengajuan refund wajib diajukan paling lambat **2 jam sebelum jadwal keberangkatan**.
*   **Denda Refund**: Sesuai dengan aturan resmi KCIC, pengembalian dana dikenakan potongan denda sebesar **25%** (dana yang dikembalikan ke penumpang adalah **75%** dari total harga tiket).
*   **Data Rekening**: Pengguna wajib mengisi Nama Bank, Nomor Rekening, dan Nama Pemilik Rekening (dapat disimpan di profil/akun).
*   Membutuhkan otorisasi keamanan biometrik pengguna untuk memproses transaksi refund.

---

## Teknologi & Libs (Tech Stack)

Aplikasi dibangun dengan pustaka Android modern:
*   **Bahasa Utama**: [Kotlin](https://kotlinlang.org/) (JVM Target 11, Kotlin 2.0.21)
*   **UI Toolkit**: [Jetpack Compose](https://developer.android.com/compose) dengan **Material Design 3** & **Compose Animation**.
*   **State Management**: Kotlin Coroutines & Flow (`StateFlow` & `SharedFlow`).
*   **Networking**: [Retrofit 2.9.0](https://square.github.io/retrofit/) & [OkHttp 4.12.0](https://square.github.io/okhttp/) (dengan `HttpLoggingInterceptor` di build varian Debug).
*   **Serialization**: Gson (2.10.1) untuk parsing request/response API.
*   **QR Code Generator**: [ZXing Core 3.5.3](https://github.com/zxing/zxing) untuk pembuatan boarding pass secara lokal.
*   **Email Notification**: [JavaMail API (Android Mail) 1.6.7](https://javaee.github.io/javamail/) untuk pengiriman e-ticket, info refund, dan info reschedule dalam format HTML langsung dari aplikasi.
*   **Keamanan Biometrik**: `androidx.biometric:biometric:1.1.0` untuk otorisasi lokal sidik jari/wajah.
*   **Gradle Configuration**: Gradle Kotlin DSL (`build.gradle.kts`) dengan **Gradle Version Catalog** (`libs.versions.toml`).

---

## Arsitektur Proyek

Aplikasi ini menggunakan pola arsitektur **MVVM (Model-View-ViewModel)** dengan struktur terpusat:
*   **Single Shared ViewModel**: Seluruh state navigasi, data booking, penumpang, dan status transaksi dikelola dalam satu viewmodel utama yaitu [BookingViewModel](file:///c:/Users/HYPE%20AMD/AndroidStudioProjects/Whoossh/app/src/main/java/com/example/whoossh/viewmodel/BookingViewModel.kt). Hal ini mempermudah sinkronisasi data antar langkah-langkah pemesanan yang bertahap tanpa dependensi database lokal yang kompleks.
*   **Direct API Integration**: ViewModel berkomunikasi langsung dengan `ApiClient.apiService` (Retrofit) untuk transaksi online, serta [UserPreferences](file:///c:/Users/HYPE%20AMD/AndroidStudioProjects/Whoossh/app/src/main/java/com/example/whoossh/data/UserPreferences.kt) untuk caching local session dan bank data rekening.

---

## 📂 Struktur Folder

```text
app/src/main/java/com/example/whoossh/
├── api/            # Retrofit Clients, Endpoint Interfaces, dan request/response API models
├── data/           # Caching SharedPreferences (UserPreferences) & Static data stasiun
├── model/          # Representasi domain model UI (Schedule, BookingData, Passenger, dll.)
├── navigation/     # NavHost, NavGraph, dan rute navigasi Compose (Screen)
├── ui/             # Representasi antarmuka pengguna
│   ├── screens/    # Seluruh 25 layar modular (Dashboard, Login, Seat Selection, E-Ticket, dll.)
│   ├── components/ # Reusable Compose UI elements (Card, Button, Dialog)
│   └── theme/      # Konfigurasi material tema Whoosh (Warna, Tipografi, Shape)
├── utils/          # Helper (EmailSender, QrCodeUtils, BiometricHelper, Translator)
└── viewmodel/      # Pusat logika bisnis & state management (BookingViewModel)
```

---

## Getting Started & Instalasi

### Prasyarat Pengembangan
*   **Android Studio**: Ladybug (2024.2.1) atau versi di atasnya.
*   **JDK**: Version 11.
*   **Minimum SDK**: API Level 24 (Android 7.0 Nougat).
*   **Target/Compile SDK**: API Level 35.

### Langkah Instalasi
1. Clone repositori ini:
   ```bash
   git clone https://github.com/rasyakt/Whoosh.git
   ```
2. Jalankan backend API PHP lokal Anda pada web server (Apache/Nginx).
3. Buka folder proyek menggunakan Android Studio.
4. Lakukan Gradle Sync dan tunggu hingga selesai.
5. Jalankan aplikasi pada emulator dengan sensor sidik jari aktif atau perangkat fisik Anda.

---

## Konfigurasi API & Email

### 1. Konfigurasi Endpoint Backend
Endpoint dikonfigurasi secara dinamis di [ApiClient.kt](file:///c:/Users/HYPE%20AMD/AndroidStudioProjects/Whoossh/app/src/main/java/com/example/whoossh/api/ApiClient.kt) berdasarkan build varian aplikasi:
```kotlin
private const val BASE_URL_DEV = "http://your_computer_ip/whoossh_api/"
private const val BASE_URL_PROD = "https://api.whoosh.id/v1/"
```
*Ganti IP `your_computer_ip` dengan alamat IP server lokal/hosting Anda.*

### 2. Deep Linking Integration
Aplikasi menangkap URI deep link secara langsung untuk membuka halaman detail E-ticket dengan format link:
*   `whoossh://ticket/{bookingCode}`
*   `https://whoosh.id/ticket/{bookingCode}`

Konfigurasi intent filter telah terdaftar di [AndroidManifest.xml](file:///c:/Users/HYPE%20AMD/AndroidStudioProjects/Whoossh/app/src/main/AndroidManifest.xml).

### 3. Konfigurasi SMTP Email
Fitur pengiriman notifikasi email memanfaatkan kredensial SMTP yang terintegrasi di `build.gradle.kts`:
```kotlin
debug {
    buildConfigField("String", "SMTP_SENDER_EMAIL", "\"emailanda@gmail.com\"")
    buildConfigField("String", "SMTP_SENDER_PASSWORD", "\"abcd efgh ijkl mnop\"")
}
```
*Catatan: SMTP_SENDER_PASSWORD menggunakan Gmail App Password (16 digit kode autentikasi) untuk bypass keamanan Google OAuth.*

---
```
CopyRight©2026 Rasya Project, All Rights Reserved
```
