package com.example.mygamecurator.api

import retrofit2.http.GET
import retrofit2.http.Query

data class RawgResponse(
    val results: List<RawgGame>
)

data class RawgGame(
    val id: Int,
    val name: String,
    val background_image: String?,
    val rating: Float,
    val genres: List<RawgGenre>?,
    val tags: List<RawgTag>?
)

data class RawgGenre(
    val id: Int,
    val name: String
)

data class RawgTag(
    val id: Int,
    val name: String
)

interface RawgApi {

    // ★ pageSize 기본값을 40에서 100으로 증가
    @GET("games/lists/popular")
    suspend fun getPopularGames(
        @Query("discover") discover: Boolean = true,
        @Query("page_size") pageSize: Int = 100, // 기본값 100으로 수정
        @Query("include") include: String = "tags,genres",
        @Query("key") key: String = Keys.RAWG_KEY
    ): RawgResponse

    // ★ pageSize 기본값을 40에서 100으로 증가
    @GET("games")
    suspend fun searchGames(
        @Query("search") search: String,
        @Query("page_size") pageSize: Int = 100, // 기본값 100으로 수정
        @Query("search_precise") precise: Boolean = true,
        @Query("ordering") ordering: String = "-rating",
        @Query("include") include: String = "tags,genres",
        @Query("key") key: String = Keys.RAWG_KEY
    ): RawgResponse
}