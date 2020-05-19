package com.example.retro_app_enhanced

import com.example.retro_app_enhanced.common.ApiService
import com.example.retro_app_enhanced.common.RetrofitInstance
import com.example.retro_app_enhanced.common.Singleton
import com.example.retro_app_enhanced.model.UserModel
import com.pusher.client.channel.User
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Call

class MockApiTesting {

    @Test
    fun jsonTest() {
        val jsonObject = JSONObject("{ \"hello\" : \"test\"}")
        assertNotNull(jsonObject.getString("hello"))
    }

    @Test
    fun jsonGetStringTest() {
        val mock = Mockito.mock(JSONObject::class.java)
        Mockito.`when`(mock.getString("name")).thenReturn("Ezio")

        val user = User("1234","Ezio")
        mock.put("name", user.info)

        assertEquals("Ezio", mock.getString("name"))
    }

    @Test
    fun singletonTest() {
        Singleton.getInstance().currentUser = UserModel("12", "Ezio", "0000", 12, true)

        assertEquals("12", Singleton.getInstance().currentUser.id)
        assertEquals("Ezio", Singleton.getInstance().currentUser.name)
        assertEquals("0000", Singleton.getInstance().currentUser.password)
        assertEquals(12, Singleton.getInstance().currentUser.count)
        assertEquals(true, Singleton.getInstance().currentUser.online)
    }

    @Test
    fun RetrofitInstanceTest() {
        assertNotNull(RetrofitInstance)
        assertNotNull(RetrofitInstance.retrofit)
        assertNotNull(RetrofitInstance.Companion)

        val ApiConnectionService = Mockito.mock(ApiService::class.java)
        val mockedListUserModelCall : Call<List<UserModel>> = Mockito.mock(Call::class.java) as Call<List<UserModel>>
        val mockedUserModelCall : Call<UserModel> = Mockito.mock(Call::class.java) as Call<UserModel>
        val mockedMessage : Call<String> = Mockito.mock(Call::class.java) as Call<String>

        Mockito.`when`(ApiConnectionService.getUsers()).thenReturn(mockedListUserModelCall)

        Mockito.`when`(ApiConnectionService.login(RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            "login"))).
        thenReturn(mockedUserModelCall)

        Mockito.`when`(ApiConnectionService.sendMessage(RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            "message"))).
        thenReturn(mockedMessage)

        assertEquals(false, ApiConnectionService.getUsers().isExecuted)
        assertEquals(null, ApiConnectionService.sendMessage(RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            "login")))
        assertEquals(null, ApiConnectionService.login(RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            "login")))
    }
}