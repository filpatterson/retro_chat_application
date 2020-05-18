package com.example.retro_app_enhanced.common

import com.example.retro_app_enhanced.model.UserModel
import com.pusher.client.channel.User
import org.json.JSONObject

fun User.toUserModel(): UserModel {
    val jsonObject = JSONObject(this.info)
    val name = jsonObject.getString("name")
    val password = jsonObject.getString("password")
    val numb = jsonObject.getInt("count")
    return UserModel(this.id, name, password, numb)
}