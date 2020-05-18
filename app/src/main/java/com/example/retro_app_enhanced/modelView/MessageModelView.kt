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

class MessageModelView (private var list: ArrayList<MessageModel>)
    : RecyclerView.Adapter<MessageModelView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.message_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int = list.size

    fun add(message: MessageModel) {
        list.add(message)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.text)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        fun bind(message: MessageModel) = with(itemView) {
            val messageToShow = ">" + message.senderName + ":\ncontent: " + message.message + "\n" +
                    "unx_tm: " + message.messageTime
            messageTextView.text = messageToShow

            val params = cardView.layoutParams as RelativeLayout.LayoutParams
            if (message.senderId == Singleton.getInstance().currentUser.id) {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            }
        }
    }
}