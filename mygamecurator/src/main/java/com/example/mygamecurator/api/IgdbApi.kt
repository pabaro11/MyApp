package com.example.mygamecurator.api

import com.example.mygamecurator.data.IgdbAccessToken
import com.example.mygamecurator.data.IgdbGameResponse
import com.example.mygamecurator.data.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface IgdbApi {

    @POST("oauth2/token")
    suspend fun getToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("grant_type") grantType: String = "client_credentials"
    ): IgdbAccessToken

    @Headers("Content-Type: text/plain")
    @POST("v4/games")
    suspend fun searchGames(
        @Header("Client-ID") clientId: String,
        @Header("Authorization") auth: String,
        @Body rawQuery: String
    ): List<IgdbGameResponse>
}

fun createIgdbTokenApi(): IgdbApi =
    Retrofit.Builder()
        .baseUrl("https://id.twitch.tv/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(IgdbApi::class.java)

fun createIgdbApi(): IgdbApi =
    Retrofit.Builder()
        .baseUrl("https://api.igdb.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(IgdbApi::class.java)
