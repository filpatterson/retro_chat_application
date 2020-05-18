package com.example.retro_app_enhanced.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.retro_app_enhanced.R
import com.example.retro_app_enhanced.common.RetrofitInstance
import com.example.retro_app_enhanced.common.Singleton
import com.example.retro_app_enhanced.model.UserModel
import kotlinx.android.synthetic.main.login_view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_view)
        loginButton.setOnClickListener {
            if (editTextUsername.text.isNotEmpty()) {
                if(editPassword.text.isNotEmpty()) {
                    loginFunction(editTextUsername.text.toString(), editPassword.text.toString())
                }
            }
        }
    }

    private fun loginFunction(name: String, password: String) {
        val jsonObject = JSONObject()
        jsonObject.put("name", name)
        jsonObject.put("password", password)

        val jsonBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            jsonObject.toString()
        )

        RetrofitInstance.retrofit.login(jsonBody).enqueue(object:Callback<UserModel> {
            override fun onFailure(call: Call<UserModel>?, t: Throwable?) {
                Log.i("LoginActivity",t!!.localizedMessage)
            }

            override fun onResponse(call: Call<UserModel>?, response: Response<UserModel>?) {
                if (response!!.code() == 200) {
                    Singleton.getInstance().currentUser = response.body()!!
                    startActivity(Intent(this@LoginView,
                        UsersView::class.java))
                    finish()
                }
            }
        })
    }
}