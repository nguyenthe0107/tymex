package com.example.domain.model

data class UserDetailInfo(
    val userName: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val location: String,
    val followers: Int,
    val following: Int,
)
