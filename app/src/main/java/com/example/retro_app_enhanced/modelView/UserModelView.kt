package com.example.retro_app_enhanced.modelView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.retro_app_enhanced.R
import com.example.retro_app_enhanced.model.UserModel
import java.util.*

class UserModelView(private var list: ArrayList<UserModel>, private var listener: UserClickListener)
    : RecyclerView.Adapter<UserModelView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.user_unit_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int = list.size

    fun showUserOnline(updatedUser: UserModel) {
        list.forEachIndexed { index, element ->
            if (updatedUser.id == element.id) {
                updatedUser.online = true
                list[index] = updatedUser
                notifyItemChanged(index)
            }

        }
    }

    fun showUserOffline(updatedUser: UserModel) {
        list.forEachIndexed { index, element ->
            if (updatedUser.id == element.id) {
                updatedUser.online = false
                list[index] = updatedUser
                notifyItemChanged(index)
            }
        }
    }

    fun add(user: UserModel) {
        list.add(user)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        private val presenceImageView: ImageView = itemView.findViewById(R.id.presenceImageView)

        fun bind(currentValue: UserModel) = with(itemView) {
            this.setOnClickListener {
                listener.onUserClicked(currentValue)
            }
            nameTextView.text = currentValue.name
            if (currentValue.online){
                presenceImageView.setImageDrawable(this.context.resources.getDrawable(
                    R.drawable.presence_icon_online
                ))
            } else {
                presenceImageView.setImageDrawable(this.context.resources.getDrawable(
                    R.drawable.presence_icon
                ))

            }

        }
    }

    interface UserClickListener {
        fun onUserClicked(user: UserModel)
    }
}