package com.communisolve.androidkotlinchatapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.communisolve.androidkotlinchatapp.ChatActivity
import com.communisolve.androidkotlinchatapp.Common.SearchUser
import com.communisolve.androidkotlinchatapp.R
import com.communisolve.androidkotlinchatapp.Model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class SearchAdapter(mContext: Context, allUserArrayList: ArrayList<User>) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    lateinit var mContext: Context
    lateinit var allUserArrayList: ArrayList<User>

    init {
        this.mContext = mContext
        this.allUserArrayList = allUserArrayList
    }

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var user_item_image: CircleImageView
        lateinit var item_username: TextView
        lateinit var online_txt: TextView
        lateinit var itemview: View

        init {
            user_item_image = itemView.findViewById(R.id.user_item_image)
            item_username = itemView.findViewById(R.id.item_username)
            online_txt = itemView.findViewById(R.id.online_txt)
            itemview = itemView.findViewById<RelativeLayout>(R.id.itemView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return allUserArrayList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

        var user: User = allUserArrayList.get(position)
        Picasso.get().load(user.profile).into(holder.user_item_image)
        holder.item_username.text = user.fullname
        holder.online_txt.text = user.status

        holder.itemview.setOnClickListener {
            SearchUser.fullname = allUserArrayList.get(position).fullname
            SearchUser.profile = allUserArrayList.get(position).profile
            SearchUser.uid = allUserArrayList.get(position).uid
            SearchUser.status = allUserArrayList.get(position).status

            mContext.startActivity(Intent(mContext,ChatActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}