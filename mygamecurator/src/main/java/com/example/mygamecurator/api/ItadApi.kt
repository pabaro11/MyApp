package com.example.mygamecurator.api

import com.example.mygamecurator.data.ItadPriceResponse
import com.example.mygamecurator.data.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ItadApi {
    @GET("v01/game/prices/")
    suspend fun getPrices(
        @Query("key") apiKey: String,
        @Query("title") title: String,
        @Query("country") country: String = "KR"
    ): ItadPriceResponse
}

fun createItadApi(): ItadApi =
    Retrofit.Builder()
        .baseUrl("https://api.isthereanydeal.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ItadApi::class.java)
