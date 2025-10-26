package com.example.w07.data

data class UserData(
    val userId: String = "user001",
    val userName: String = "Player1",
    val totalScore: Int = 0
)

val INITIAL_USER_DATA = UserData()
