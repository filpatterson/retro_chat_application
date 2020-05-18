package com.example.retro_app_enhanced.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserModel(@SerializedName("_id") @Expose var id: String,
                     @SerializedName("name") @Expose var name: String,
                     @SerializedName("password") @Expose var password: String,
                     @SerializedName("count") @Expose var count: Int,
                     var online:Boolean = false)