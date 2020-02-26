package com.filpatterson.retr0_chat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  Set layout of authorizing into the system
        setContentView(R.layout.activity_login)

        //  Set action if button is pressed
        login_button.setOnClickListener{
            val email = login_user_name.text.toString()
            val password = login_user_password.text.toString()

            Log.d("LoginActivity", "Attempt to login using $email e-mail.")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        }

        //  If user needs to come back to the register activity, then finish current one
        login_back_to_register.setOnClickListener{
            finish()
        }
    }
}