package com.filpatterson.retr0_chat

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

//  Main class for handling registration of user inside application's database
class MainActivity : AppCompatActivity() {

    var selectedPhotoPath: Uri? = null

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

        //  Set action of choosing photo for user
        register_photo_button.setOnClickListener{
            Log.d("MainActivity", "Try to show photo selector")

            //  Initialize activity of choosing file from the system
            val intent = Intent(Intent.ACTION_PICK)
            //  Pick only files ofr image type (img, jpg, png and so on)
            intent.type = "image/*"
            //  Start activity
            startActivityForResult(intent, 0)
        }
    }

    /*
        This function overrides standard behaviour after choosing any element using
     'startActivityForResult' method. Without it, 'startActivityForResult' will do nothing.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //  Check if call, result aren't with errors and if any data was chosen
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("MainActivity", "Photo was selected")

            //  Where image is stored inside device
            selectedPhotoPath = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoPath)

            Log.d("MainActivity", "$selectedPhotoPath")

            val bitmapDrawable = BitmapDrawable(bitmap)
            register_photo_button.setBackgroundDrawable(bitmapDrawable)
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
        if (email.isNotEmpty() && password.isNotEmpty()) {
            //  Show inserted data in log
            Log.d("MainActivity", "email is : $email")
            Log.d("MainActivity", "password: $password")

            /*
                Firebase registration of new user in system using inserted email and password
                Here is set two listeners: one for successful registration and one for failure.
            First one shows that everything is fine, another one shows error that appeared during
            registration process. For successful result algorithm creates new user in Firebase and
            adds new image for user in the Firebase storage
             */
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{
                    //  If there is error in registering new user, then come back to the listener
                    if(!it.isSuccessful) return@addOnCompleteListener

                    Log.d(
                        "MainActivity",
                        "User was successfully created with id: ${it.result!!.user!!.uid}")
                    //uploadPhotoToFirebaseStorage()
                    val intent = Intent(this, messagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

                .addOnFailureListener{
                    Log.d(
                        "MainActivity",
                        "Failed to create user ${it.message}")
                    Toast.makeText(this, "#error: check your input",
                        Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            Toast.makeText(this, "#error: no email or password",
                Toast.LENGTH_SHORT)
                .show()
            return
        }
    }

    //  Add photo to the Firebase storage
    private fun uploadPhotoToFirebaseStorage() {
        Log.d("MainActivity", "works 1")
        if(selectedPhotoPath == null) return
        Log.d("MainActivity", "works 2")

        //  pick random name for file to store and set reference where file needs to be located
        val filename = UUID.randomUUID().toString()
        val referenceToStorage = FirebaseStorage.getInstance().getReference("images/$filename")

        Log.d("MainActivity", "works 3")
        Log.d("MainActivity", "$selectedPhotoPath")
        referenceToStorage.putFile(selectedPhotoPath!!)
            .addOnSuccessListener {
                Log.d("MainActivity", "Successful upload of the image: ${it.metadata?.path}")

                referenceToStorage.downloadUrl.addOnSuccessListener {
                    Log.d("MainActivity", "File location: $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(
                    "MainActivity",
                    "Failed to send photo ${it.message}"
                )
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val refToDatabase = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, register_user_name.text.toString(), profileImageUrl)

        refToDatabase.setValue(user)
            .addOnSuccessListener {
                Log.d("MainActivity", "Saved user to the Firebase database")
            }

    }

    class User(val uid: String, val username: String, val profileImageUrl: String)
}
