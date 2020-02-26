package com.filpatterson.retr0_chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

//  Main class for handling registration of user inside application's database
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  Set call of action-function that performs registration of user
        register_button.setOnClickListener{
            performUserRegistration()
        }

        //  Set and start login activity if the user is already registered in system
        register_already_has_account.setOnClickListener{
            Log.d("MainActivity", "Try to show login activity")

            //  Initialize login activity and start it
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    /*
            Function for button "Register" from the main activity layout. On click function gets
        user's input from 'register_user_email' and 'register_user_password' fields and makes
        Firebase registration of user with inserted data.
     */
    private fun performUserRegistration(){
        //  Read values from fields
        val email = register_user_email.text.toString()
        val password = register_user_password.text.toString()

        //  If there is no input in either of fields, then pop message and come back to listening
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "#error: no email or password",
                Toast.LENGTH_SHORT)
                .show()
            return
        }

        //  Show inserted data in log
        Log.d("MainActivity", "email is : $email")
        Log.d("MainActivity", "password: $password")

        /*
            Firebase registration of new user in system using inserted email and password
            Here is set two listeners: one for successful registration and one for failure.
        First one shows that everything is fine, another one shows error that appeared during
        registration process.
         */
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                //  If there is error in registering new user, then come back to the listener
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d("MainActivity",
                    "User was successfully created with id: ${it.result!!.user!!.uid}")
            }

            .addOnFailureListener{
                Log.d("MainActivity",
                    "Failed to create user ${it.message}")

                Toast.makeText(this, "#error: check your input",
                    Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
