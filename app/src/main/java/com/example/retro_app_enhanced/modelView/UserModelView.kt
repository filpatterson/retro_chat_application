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

/**
 *  Class defining connection between User Model and User view entities. The last one is used to show
 * all users that are in system
 */
class UserModelView(private var list: ArrayList<UserModel>, private var listener: UserClickListener)
    : RecyclerView.Adapter<UserModelView.ViewHolder>() {

    /**
     * create view holder for user entity
     * @arg parent view group that contains information about how message must be displayed
     * @arg viewType type of showing user info
     * @return holder of view for user info
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.user_unit_view, parent, false))
    }

    /**
     * bind view holder to the element of user list
     * @arg holder viewHolder that must be appended to the element
     * @arg position index of element in list to which holder needs to be appended
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    /**
     * get size of user list
     * @return size of user list
     */
    override fun getItemCount(): Int = list.size

    /**
     * set view of icon defining that user is online
     * @arg updatedUser updated information about user defining that he is present
     */
    fun showUserOnline(updatedUser: UserModel) {
        list.forEachIndexed { index, element ->
            if (updatedUser.id == element.id) {
                updatedUser.online = true
                list[index] = updatedUser
                notifyItemChanged(index)
            }
        }
    }

    /**
     * set view of icon defining that user is offline
     * @arg updatedUser updated information about user defining that he is not present
     */
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

    /**
     * inner class defining how information about user must be displayed in system
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //  setting text field to print
        private val nameTextView: TextView = itemView.findViewById(R.id.usernameTextView)

        //  setting image defining is user present or not
        private val presenceImageView: ImageView = itemView.findViewById(R.id.presenceImageView)

        /**
         * bind required view depending on user info
         * @arg currentValue current status of user
         */
        fun bind(currentValue: UserModel) = with(itemView) {
            //  append listener for clicking on user unit
            this.setOnClickListener {
                listener.onUserClicked(currentValue)
            }

            //  print user name
            nameTextView.text = currentValue.name

            //  set image defining is user online or not depending on his current status
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

    /**
     * interface defining click listening behavior that can be rewritten basing on needs
     */
    interface UserClickListener {
        fun onUserClicked(user: UserModel)
    }
}