package com.example.retro_app_enhanced.common

import android.util.Log
import com.example.retro_app_enhanced.model.UserModel
import com.pusher.client.channel.User
import org.json.JSONObject

/**
 * function that adapts json-formatted string data into User Model instance
 */
fun User.toUserModel(): UserModel {
    val jsonObject = JSONObject(this.info)
    val name = jsonObject.getString("name")
    Log.i("userView-----------", this.toString())
    return UserModel(this.id, name, "0000", 0)
}