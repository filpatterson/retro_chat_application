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

/**
 * Message view class defining front-end user interaction
 */
class MessagesView : AppCompatActivity() {

    //  constants that are used for correct getting data
    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_NAME = "name"
        const val EXTRA_COUNT = "numb"
    }

    //  name of contact on the other side of channel and his ID
    private lateinit var contactName: String
    private lateinit var contactId: String
    private var contactNumb: Int = -1

    //  name of channel through which communication is established
    lateinit var nameOfChannel: String

    //  holder of views for messages
    val mAdapter = MessageModelView(ArrayList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_view)
        fetchExtras()
        setupRecyclerView()
        subscribeToChannel()
        setupClickListener()
    }

    /**
     * get all required incoming data about connection and communication
     */
    private fun fetchExtras() {
        contactName = intent.extras!!.getString(EXTRA_NAME)!!
        contactId = intent.extras!!.getString(EXTRA_ID)!!
        contactNumb = intent.extras!!.getInt(EXTRA_COUNT)
    }

    /**
     * init recycler view that will show all messages
     */
    private fun setupRecyclerView() {
        with(recyclerViewChat) {
            layoutManager = LinearLayoutManager(this@MessagesView)
            adapter = mAdapter
        }
    }

    /**
     * establishing connection to the server and setting channel for sending messages
     */
    private fun subscribeToChannel() {
        //  try to authorize to server that contains Pusher module
        val authorizer = HttpAuthorizer("http://10.0.2.2:5000/pusher/auth/private")
        val options = PusherOptions().setAuthorizer(authorizer)
        options.setCluster("eu")

        //  establish Pusher connection
        val pusher = Pusher("8b63e6c6448bbe805172", options)
        pusher.connect()

        //  setting name of channel based on id of local user and contact id
        nameOfChannel = if (Singleton.getInstance().currentUser.id.compareTo(contactId) < 0) {
            "private-" + Singleton.getInstance().currentUser.id + "-" + contactId
        } else {
            "private-" + contactId + "-" + Singleton.getInstance().currentUser.id
        }

        //  logging channel name
        Log.i("MessagesView", nameOfChannel)

        //  subscribe to the private communication channel with defined name and listen for events
        pusher.subscribePrivate(nameOfChannel, object : PrivateChannelEventListener {
            //  if there is message coming from server
            override fun onEvent(channelName: String?, eventName: String?, data: String?) {

                //  start data deserialization
                val jsonObject = JSONObject(data)

                //  form new message entity based on incoming data
                val messageModel = MessageModel(
                        jsonObject.getString("message"),
                        jsonObject.getString("sender_id"),
                        jsonObject.getString("message_time"),
                        jsonObject.getString("sender_name")
                )

                Log.e("MessagesView", messageModel.toString())

                //  add message to the view
                runOnUiThread {
                    mAdapter.add(messageModel)
                }

            }

            override fun onAuthenticationFailure(p0: String?, p1: Exception?) {
                Log.e("MessagesView", p1!!.localizedMessage)
            }

            override fun onSubscriptionSucceeded(p0: String?) {
                Log.i("MessagesView", "Successful subscription")
            }

        }, "new-message")

    }

    /**
     * set click listening behavior for sendMessage icon
     */
    private fun setupClickListener() {
        //  append listener to button
        sendButton.setOnClickListener{
            //  check text not to be empty
            if (editText.text.isNotEmpty()){
                //  init JSON-formatted message object
                val jsonObject = JSONObject()

                //  get message sending time
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                val formatted = current.format(formatter)

                //  put all required data inside JSON-formatted message
                jsonObject.put("message_time", formatted.toString())
                jsonObject.put("sender_name", Singleton.getInstance().currentUser.name)
                jsonObject.put("message",editText.text.toString())
                jsonObject.put("channel_name",nameOfChannel)
                jsonObject.put("sender_id", Singleton.getInstance().currentUser.id)

                //  set encoding of data inside JSON
                val jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                    jsonObject.toString())

                //  try sending message via retrofit instance and show output in log field
                RetrofitInstance.retrofit.sendMessage(jsonBody).enqueue(object: Callback<String>{
                    override fun onFailure(call: Call<String>?, t: Throwable?) {
                        Log.e("MessagesView",t!!.localizedMessage)

                    }

                    override fun onResponse(call: Call<String>?, response: Response<String>?) {
                        Log.e("MessagesView",response!!.body())
                    }

                })

                //  clear input from screen and hide keyboard
                editText.text.clear()
                hideKeyBoard()
            }

        }
    }

    /**
     * Hide keyboard from screen
     */
    private fun hideKeyBoard() {
        //  get information about current keyboard's status
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }

        //  hide this keyboard from screen
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


}