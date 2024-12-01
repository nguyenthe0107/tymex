package com.tymex.data.model

import com.example.domain.model.UserInfo
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
)

fun UserInfoDTO.toUserInfo(): UserInfo {
    return UserInfo(id = this.id,userName = this.userName, avatarUrl = this.avatarUrl, htmlUrl = this.htmlUrl)
}
