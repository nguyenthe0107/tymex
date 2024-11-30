package com.tymex.data.model

import com.example.domain.model.UserDetailInfo

data class UserInfoDetailDTO(
    val userName: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val location: String,
    val followers: Int,
    val following: Int,
)

fun UserInfoDetailDTO.toUserDetailInfo(): UserDetailInfo {
    return UserDetailInfo(
        userName = this.userName,
        avatarUrl = this.avatarUrl,
        htmlUrl = this.htmlUrl,
        location = this.location,
        followers = this.followers,
        following = this.following
    )
}
