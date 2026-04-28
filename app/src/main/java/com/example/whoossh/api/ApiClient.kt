package com.example.whoossh.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit singleton client for Whoossh API
 *
 * BASE_URL: Gunakan IP komputer lokal Anda.
 * - Emulator Android: gunakan 10.0.2.2 (maps ke localhost PC)
 * - Device fisik: gunakan IP WiFi PC (misal 192.168.x.x)
 */
object ApiClient {

    // Untuk emulator Android Studio (10.0.2.2 = localhost PC)
    private const val BASE_URL = "http://10.129.214.16/whoossh_api/"

    // Untuk device fisik, ganti dengan IP WiFi PC:
    // private const val BASE_URL = "http://192.168.1.100/whoossh_api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
