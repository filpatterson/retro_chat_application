package com.example.retro_app_enhanced.view

import android.content.Intent
import android.os.Bundle
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

class UsersView : AppCompatActivity(),
    UserModelView.UserClickListener {

    private val mAdapter = UserModelView(ArrayList(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_list_view)
        setupRecyclerView()
        fetchUsers()
        subscribeToChannel()
    }

    private fun setupRecyclerView() {
        with(recyclerViewUserList) {
            layoutManager = LinearLayoutManager(this@UsersView)
            adapter = mAdapter
        }
    }

    private fun fetchUsers() {
        RetrofitInstance.retrofit.getUsers().enqueue(object : Callback<List<UserModel>> {
            override fun onFailure(call: Call<List<UserModel>>?, t: Throwable?) {}
            override fun onResponse(call: Call<List<UserModel>>?, response: Response<List<UserModel>>?) {
                for (user in response!!.body()!!) {
                    if (user.id != Singleton.getInstance().currentUser.id) {
                        mAdapter.add(user)
                    }
                }
            }
        })
    }

    private fun subscribeToChannel() {

        val authorizer = HttpAuthorizer("http://10.0.2.2:5000/pusher/auth/presence")
        val options = PusherOptions().setAuthorizer(authorizer)
        options.setCluster("PUSHER_APP_CLUSTER")

        val pusher = Pusher("PUSHER_APP_KEY", options)
        pusher.connect()

        pusher.subscribePresence("presence-channel", object : PresenceChannelEventListener {
            override fun onUsersInformationReceived(p0: String?, users: MutableSet<User>?) {
                for (user in users!!) {
                    if (user.id!= Singleton.getInstance().currentUser.id){
                        runOnUiThread {
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

    override fun onUserClicked(user: UserModel) {
        val intent = Intent(this, MessagesView::class.java)
        intent.putExtra(MessagesView.EXTRA_ID,user.id)
        intent.putExtra(MessagesView.EXTRA_NAME,user.name)
        intent.putExtra(MessagesView.EXTRA_COUNT,user.count)
        startActivity(intent)
    }

}