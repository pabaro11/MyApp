package com.example.gamecurator.network

import com.example.gamecurator.data.model.RecommendationResponse
import retrofit2.http.GET

interface GameService {

    @GET("recommend")
    suspend fun getRecommend(): RecommendationResponse
}
