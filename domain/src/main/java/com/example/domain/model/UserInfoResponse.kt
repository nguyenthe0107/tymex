package com.example.domain.model

/**
 User info response
 */
data class UserInfoResponse(
    val id: Int,
    val userName: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val location: String? = null,
    val followers: Int? = 0,
    val following: Int? = 0
)