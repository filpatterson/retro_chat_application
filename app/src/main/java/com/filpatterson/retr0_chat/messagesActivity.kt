package com.filpatterson.retr0_chat

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_message_form.*

class messagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_form)

        val users: ArrayList<String> = ArrayList()

        for(i in 1..100) {
            users.add("#retr0 @$i>:\n Here's example of my message")
        }

        messages_recyclerView.layoutManager = LinearLayoutManager(this)
        messages_recyclerView.adapter = UsersAdapter(users)
    }
}