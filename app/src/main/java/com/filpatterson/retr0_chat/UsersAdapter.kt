package com.filpatterson.retr0_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter(val users: ArrayList<String>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.message_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.firstName.text = users[position]
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val firstName: TextView = itemView.findViewById(R.id.firstName)
    }
}