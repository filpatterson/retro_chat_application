package com.filpatterson.retr0_chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

//  Main class for handling registration of user inside application's database
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  Set action for button "Register" from the main activity layout
        button_register.setOnClickListener{
            //  Read values from fields "user_name" and "user_password"
            val username = user_name_register.text.toString()
            val password = user_password_register.text.toString()

            Log.d("MainActivity", "username is : $username")
            Log.d("MainActivity", "password: $password")
        }

        //  Set new activity if the user is already registered in system
        already_has_account.setOnClickListener{
            Log.d("MainActivity", "Try to show login activity")

            //  Launch new activity on click
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
