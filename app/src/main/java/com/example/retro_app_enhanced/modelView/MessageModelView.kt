package com.example.retro_app_enhanced.modelView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.retro_app_enhanced.R
import com.example.retro_app_enhanced.common.Singleton
import com.example.retro_app_enhanced.model.MessageModel
import java.util.*

/**
 *  Class defining communication between message units and view all messages that are shown on chat
 * window
 */
class MessageModelView (private var list: ArrayList<MessageModel>)
    : RecyclerView.Adapter<MessageModelView.ViewHolder>() {

    /**
     * create view holder for message
     * @arg parent view group that contains information about how message needs to be displayed
     * @arg viewType type of views that can be appended
     * @return holder of views for message
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.message_view, parent, false))
    }

    /**
     * bind view holder to the message
     * @arg holder viewHolder that must be appended to the message
     * @arg position index of message in local storage
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    /**
     * get size of message list
     * @return size of message list
     */
    override fun getItemCount(): Int = list.size

    /**
     * add new message to the list of all messages
     * @arg message message that must be appended
     */
    fun add(message: MessageModel) {
        list.add(message)
        notifyDataSetChanged()
    }

    /**
     * inner view holder class that defines how message can be shown
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //  setting text showing
        private val messageTextView: TextView = itemView.findViewById(R.id.text)

        //  setting general message object showing
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        //  bind view to the holder
        fun bind(message: MessageModel) = with(itemView) {
            //  how message content needs to be displayed
            val messageToShow = ">" + message.senderName + ":\ncontent: " + message.message + "\n" +
                    "unx_tm: " + message.messageTime

            //  append message content to the view
            messageTextView.text = messageToShow

            //  set parameters of showing message depending on its sender id
            val params = cardView.layoutParams as RelativeLayout.LayoutParams
            if (message.senderId == Singleton.getInstance().currentUser.id) {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                params.marginStart = 130
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                params.marginEnd = 130
            }
        }
    }
}