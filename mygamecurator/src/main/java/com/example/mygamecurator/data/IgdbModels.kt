package com.example.mygamecurator.data

data class IgdbAccessToken(
    val access_token: String,
    val expires_in: Int,
    val token_type: String
)

data class IgdbGameResponse(
    val id: Int?,
    val name: String?,
    val rating: Double?,
    val cover: IgdbCover?,
    val genres: List<IgdbGenre>?,
    val first_release_date: Long?
)

data class IgdbCover(
    val id: Int?,
    val image_id: String?
)

data class IgdbGenre(
    val id: Int?,
    val name: String?
)
