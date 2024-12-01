package com.tymex.data.model

import com.example.domain.model.UserInfoResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserInfoDTO(
    @Json(name = "id")
    val id: Int,
    @Json(name = "login")
    val userName: String,
    @Json(name = "avatar_url")
    val avatarUrl: String,
    @Json(name = "html_url")
    val htmlUrl: String,

    val location: String?,
    val followers: Int?,
    val following: Int?,
)

fun UserInfoDTO.toUserInfo(): UserInfoResponse {
    return UserInfoResponse(
        id = this.id, userName = this.userName, avatarUrl = this.avatarUrl, htmlUrl = this.htmlUrl,
        location = this.location,
        followers = this.followers,
        following = this.following
    )
}
