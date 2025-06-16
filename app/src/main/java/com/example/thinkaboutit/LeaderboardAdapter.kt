package com.example.thinkaboutit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LeaderboardAdapter(
    private val users: List<User>,
    private val imageClickListener: OnUserImageClick
) : RecyclerView.Adapter<LeaderboardAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rank: TextView = itemView.findViewById(R.id.user_rank)
        val image: ImageView = itemView.findViewById(R.id.user_image)
        val name: TextView = itemView.findViewById(R.id.user_name)
        val votes: TextView = itemView.findViewById(R.id.user_votes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.rank.text = (position + 1).toString()
        holder.name.text = user.name
        holder.votes.text = user.votes.toString()
        if (user.image != null) {
            holder.image.setImageBitmap(user.image)
            holder.image.setOnClickListener { imageClickListener.onUserImageClick(user.image) }
        } else {
            holder.image.setImageResource(R.drawable.sample_drawing1)
            holder.image.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = users.size
}