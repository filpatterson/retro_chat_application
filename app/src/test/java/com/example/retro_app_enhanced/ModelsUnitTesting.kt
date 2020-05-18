package com.example.retro_app_enhanced

import com.example.retro_app_enhanced.model.MessageModel
import com.example.retro_app_enhanced.model.UserModel
import org.junit.Assert.assertEquals
import org.junit.Test


class ModelsUnitTesting {

    /**
     * Make check that values that are inserted inside object are equal to object values
     */
    @Test
    fun testingSingleMessageModel() {
        val message = MessageModel(
            "this is my first unit test",
            "1ghBasdT_asdlkj213",
            "Mon, 12, 2020 12:11:23",
            "Ezio"
        )
        assertEquals("this is my first unit test", message.message)
        assertEquals("1ghBasdT_asdlkj213", message.senderId)
        assertEquals("Mon, 12, 2020 12:11:23", message.messageTime)
        assertEquals("Ezio", message.senderName)
    }

    /**
     * Make check that values inserted into user object are equal to the original values
     */
    @Test
    fun testingSingleUserModel() {
        val user = UserModel("1234", "Ezio", "1234", 12, false)

        assertEquals("1234", user.id)
        assertEquals("Ezio", user.name)
        assertEquals("1234", user.password)
        assertEquals(12, user.count)
        assertEquals(false, user.online)
    }

    /**
     * Create list of messages and check their values to be equal to the original values
     */
    @Test
    fun testingMultipleMessageModels() {
        val listOfMessages = ArrayList<MessageModel>()
        for(i in 0..100) {
            val message = MessageModel(
                "message___$i",
                "id___$i",
                "Time of sending message___$i",
                "name of sender___$i"
            )
            listOfMessages.add(message)
        }

        assertEquals(101, listOfMessages.size)
        for(i in 0..100) {
            assertEquals("message___$i", listOfMessages[i].message)
            assertEquals("id___$i", listOfMessages[i].senderId)
            assertEquals("Time of sending message___$i", listOfMessages[i].messageTime)
            assertEquals("name of sender___$i", listOfMessages[i].senderName)
        }
    }

    /**
     * Create list of all users for checking if inserted values are equal to the values from object
     */
    @Test
    fun testingMultipleUserModels() {
        val listOfUsers = ArrayList<UserModel>()
        for(i in 0..100) {
            val user = UserModel(
                "user's id___$i",
                "name___$i",
                "password___$i",
                i,
                true
            )
            listOfUsers.add(user)
        }

        assertEquals(101, listOfUsers.size)
        for(i in 0..100) {
            assertEquals("user's id___$i", listOfUsers[i].id)
            assertEquals("name___$i", listOfUsers[i].name)
            assertEquals("password___$i", listOfUsers[i].password)
            assertEquals(i, listOfUsers[i].count)
            assertEquals(true, listOfUsers[i].online)
        }
    }
}