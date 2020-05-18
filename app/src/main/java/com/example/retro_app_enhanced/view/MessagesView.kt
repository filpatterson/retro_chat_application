package com.example.retro_app_enhanced.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retro_app_enhanced.modelView.MessageModelView
import com.example.retro_app_enhanced.R
import com.example.retro_app_enhanced.common.RetrofitInstance
import com.example.retro_app_enhanced.common.Singleton
import com.example.retro_app_enhanced.model.MessageModel
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.util.HttpAuthorizer
import kotlinx.android.synthetic.main.chat_view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MessagesView : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_NAME = "name"
        const val EXTRA_COUNT = "numb"
    }

    private lateinit var contactName: String
    private lateinit var contactId: String
    private var contactNumb: Int = -1
    lateinit var nameOfChannel: String
    val mAdapter = MessageModelView(ArrayList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_view)
        fetchExtras()
        setupRecyclerView()
        subscribeToChannel()
        setupClickListener()
    }


    private fun fetchExtras() {
        contactName = intent.extras!!.getString(EXTRA_NAME)!!
        contactId = intent.extras!!.getString(EXTRA_ID)!!
        contactNumb = intent.extras!!.getInt(EXTRA_COUNT)
    }


    private fun setupRecyclerView() {
        with(recyclerViewChat) {
            layoutManager = LinearLayoutManager(this@MessagesView)
            adapter = mAdapter
        }
    }


    private fun subscribeToChannel() {
        val authorizer = HttpAuthorizer("http://10.0.2.2:5000/pusher/auth/private")
        val options = PusherOptions().setAuthorizer(authorizer)
        options.setCluster("eu")

        val pusher = Pusher("8b63e6c6448bbe805172", options)
        pusher.connect()

        nameOfChannel = if (Singleton.getInstance().currentUser.id.compareTo(contactId) < 0) {
            "private-" + Singleton.getInstance().currentUser.id + "-" + contactId
        } else {
            "private-" + contactId + "-" + Singleton.getInstance().currentUser.id
        }

        Log.i("ChatRoom", nameOfChannel)

        pusher.subscribePrivate(nameOfChannel, object : PrivateChannelEventListener {
            override fun onEvent(channelName: String?, eventName: String?, data: String?) {

                val jsonObject = JSONObject(data)
                val messageModel = MessageModel(
                        jsonObject.getString("message"),
                        jsonObject.getString("sender_id"),
                        jsonObject.getString("message_time"),
                        jsonObject.getString("sender_name")
                )

                Log.e("ChatRoom", messageModel.toString())

                runOnUiThread {
                    mAdapter.add(messageModel)
                }

            }

            override fun onAuthenticationFailure(p0: String?, p1: Exception?) {
                Log.e("ChatRoom", p1!!.localizedMessage)
            }

            override fun onSubscriptionSucceeded(p0: String?) {
                Log.i("ChatRoom", "Successful subscription")
            }

        }, "new-message")

    }


    private fun setupClickListener() {
        sendButton.setOnClickListener{
            if (editText.text.isNotEmpty()){
                val jsonObject = JSONObject()

                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                val formatted = current.format(formatter)

                jsonObject.put("message_time", formatted.toString())
                jsonObject.put("sender_name", Singleton.getInstance().currentUser.name)
                jsonObject.put("message",editText.text.toString())
                jsonObject.put("channel_name",nameOfChannel)
                jsonObject.put("sender_id",
                    Singleton.getInstance().currentUser.id)
                val jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                    jsonObject.toString())

                RetrofitInstance.retrofit.sendMessage(jsonBody).enqueue(object: Callback<String>{
                    override fun onFailure(call: Call<String>?, t: Throwable?) {
                        Log.e("ChatRoom",t!!.localizedMessage)

                    }

                    override fun onResponse(call: Call<String>?, response: Response<String>?) {
                        Log.e("ChatRoom",response!!.body())
                    }

                })
                editText.text.clear()
                hideKeyBoard()
            }

        }
    }

    private fun hideKeyBoard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


}