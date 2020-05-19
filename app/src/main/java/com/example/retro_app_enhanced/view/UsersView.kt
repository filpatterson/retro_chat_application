package com.example.retro_app_enhanced.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retro_app_enhanced.modelView.UserModelView
import com.example.retro_app_enhanced.R
import com.example.retro_app_enhanced.common.RetrofitInstance
import com.example.retro_app_enhanced.common.Singleton
import com.example.retro_app_enhanced.common.toUserModel
import com.example.retro_app_enhanced.model.UserModel
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.User
import com.pusher.client.util.HttpAuthorizer
import kotlinx.android.synthetic.main.user_list_view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Users view class defining front-end user interaction
 */
class UsersView : AppCompatActivity(),
    UserModelView.UserClickListener {

    //  setting model view with listener for clicks
    private val mAdapter = UserModelView(ArrayList(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_list_view)
        setupRecyclerView()
        fetchUsers()
        subscribeToChannel()
    }

    /**
     * setup recycler that will show all users in system
     */
    private fun setupRecyclerView() {
        with(recyclerViewUserList) {
            layoutManager = LinearLayoutManager(this@UsersView)
            adapter = mAdapter
        }
    }

    /**
     * form request to server for getting list of all users in system
     */
    private fun fetchUsers() {
        //  form request to server via retrofit
        RetrofitInstance.retrofit.getUsers().enqueue(object : Callback<List<UserModel>> {
            override fun onFailure(call: Call<List<UserModel>>?, t: Throwable?) {}

            //  if there was successful request, then append all users to the list
            override fun onResponse(call: Call<List<UserModel>>?, response: Response<List<UserModel>>?) {
                for (user in response!!.body()!!) {
                    if (user.id != Singleton.getInstance().currentUser.id) {
                        mAdapter.add(user)
                    }
                }
            }
        })
    }

    /**
     *  function that subscribes current user to the channel that informs about presence of other users
     */
    private fun subscribeToChannel() {

        //  try to authorize to the presence channel
        val authorizer = HttpAuthorizer("http://10.0.2.2:5000/pusher/auth/presence")

        //  set channel parameters of connection
        val options = PusherOptions().setAuthorizer(authorizer)
        options.setCluster("eu")
        val pusher = Pusher("8b63e6c6448bbe805172", options)
        pusher.connect()

        //  subscribe to the channel and get updates
        pusher.subscribePresence("presence-channel", object : PresenceChannelEventListener {

            //  this function appends "is online" view to those users who are logged inside system
            override fun onUsersInformationReceived(p0: String?, users: MutableSet<User>?) {
                for (user in users!!) {
                    Log.i("usersView----", user.toString())
                    if (user.id != Singleton.getInstance().currentUser.id){
                        runOnUiThread {
                            Log.i("usersView", user.toString())
                            mAdapter.showUserOnline(user.toUserModel())
                        }
                    }
                }
            }

            override fun onEvent(p0: String?, p1: String?, p2: String?) { }
            override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
            override fun onSubscriptionSucceeded(p0: String?) {}

            override fun userSubscribed(channelName: String, user: User) {
                runOnUiThread {
                    mAdapter.showUserOnline(user.toUserModel())
                }
            }

            override fun userUnsubscribed(channelName: String, user: User) {
                runOnUiThread {
                    mAdapter.showUserOffline(user.toUserModel())
                }
            }
        })
    }

    /**
     * behavior of clicking on another user icon
     */
    override fun onUserClicked(user: UserModel) {
        val intent = Intent(this, MessagesView::class.java)

        //  set data about further communication and connect it with message view
        intent.putExtra(MessagesView.EXTRA_ID, user.id)
        intent.putExtra(MessagesView.EXTRA_NAME, user.name)
        intent.putExtra(MessagesView.EXTRA_COUNT, user.count)

        //  start activity of messaging with user
        startActivity(intent)
    }

}