package com.example.retro_app_enhanced.common

import com.example.retro_app_enhanced.model.UserModel

class Singleton {
    companion object {
        private val ourInstance =
            Singleton()
        fun getInstance(): Singleton {
            return ourInstance
        }
    }
    lateinit var currentUser: UserModel
}