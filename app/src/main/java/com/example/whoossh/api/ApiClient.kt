package com.example.whoossh.api

import com.example.whoossh.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit singleton client for Whoossh API
 *
 * BASE_URL: Automatically configured based on build variant
 * - Debug: Uses local development server
 * - Release: Uses production server
 */
object ApiClient {

    // Base URL configuration - should be moved to BuildConfig in production
    private const val BASE_URL_DEV = "http://192.168.1.2/whoossh_api/"
    private const val BASE_URL_PROD = "https://api.whoosh.id/v1/" // Example production URL
    
    private val BASE_URL = if (BuildConfig.DEBUG) BASE_URL_DEV else BASE_URL_PROD

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Only log in debug builds for security
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(loggingInterceptor)
            }
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        // Add retry on connection failure
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
