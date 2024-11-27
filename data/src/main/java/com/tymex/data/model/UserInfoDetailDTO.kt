package com.tymex.data.model

data class UserInfoDetailDTO(
    val userName: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val location: String,
    val followers: Int,
    val following: Int,
)
