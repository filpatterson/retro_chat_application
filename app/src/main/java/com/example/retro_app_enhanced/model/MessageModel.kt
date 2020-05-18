package com.example.retro_app_enhanced.model

/**
 * Model of message containing payload of message, sender's name and message server registering time
 */
data class MessageModel(val message: String, val senderId: String, val messageTime: String, val senderName: String)