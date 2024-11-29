package com.learning.android.wallet.network

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.learning.android.BuildConfig
import com.learning.android.utils.PrefUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object CommonProvider {
    fun providesSharedPreference(context: Context): PrefUtils = PrefUtils(context)

    fun gson(): Gson = GsonBuilder().setLenient().create()

    private fun providesOkHttpClient(
        context: Context, sharedPref: PrefUtils
    ): OkHttpClient = OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).addInterceptor(
            if (BuildConfig.DEBUG) HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            else HttpLoggingInterceptor()
        ).addInterceptor(NetworkStatusInterceptor(context, sharedPref))
        .connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS).build()

    fun providesApiService(
        context: Context, sharedPref: PrefUtils
    ): ApiInterface {
        return providesRetrofit(
            context, sharedPref
        ).create(ApiInterface::class.java)
    }

    private fun providesRetrofit(
        context: Context, sharedPref: PrefUtils
    ): Retrofit {
        return Retrofit.Builder().baseUrl(NetworkConstants.ApiUrls.BASE_URL).client(
            providesOkHttpClient(
                context, sharedPref
            )
        ).addConverterFactory(GsonConverterFactory.create(gson())).build()
    }
}