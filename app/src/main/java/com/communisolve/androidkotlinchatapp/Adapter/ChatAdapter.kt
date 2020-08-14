package com.communisolve.androidkotlinchatapp.Adapter

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.communisolve.androidkotlinchatapp.Model.Chat
import com.communisolve.androidkotlinchatapp.Model.Chatlist
import com.communisolve.androidkotlinchatapp.R
import com.communisolve.androidkotlinchatapp.ViewImageActivity
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(
    mContext: Context,
    mChatlist: List<Chat>,
    imageUrl: String
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    lateinit var mContext: Context
    lateinit var mChatList: List<Chat>
    lateinit var imageUrl: String
    lateinit var currentUserId: String

    init {
        this.mChatList = mChatlist
        this.mContext = mContext
        this.imageUrl = imageUrl
        currentUserId = FirebaseAuth.getInstance().uid!!

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        //1 for message item right
        return if (viewType == 1) {
            ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_item_right, parent, false)
            )
        } else {
            ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_item_left, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chat: Chat = mChatList.get(position)


        Picasso.get().load(imageUrl).into(holder.profile_image_left)
        //Images Messages
        if (chat.message.equals("sent you an image.") && !chat.url.equals("")) {
            //image message -right side
            if (chat.sender.equals(currentUserId)) {
                holder.show_msg!!.visibility = View.GONE
                holder.msg_image_right!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.msg_image_right)
                holder.msg_image_right.setOnClickListener {
                    var intent = Intent(mContext,ViewImageActivity::class.java)
                    intent.putExtra("link",chat.url)
                    mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }
            //image message -left side
            if (!chat.sender.equals(currentUserId)) {
                holder.show_msg!!.visibility = View.GONE
                holder.msg_image_left!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.msg_image_left)
                holder.profile_image_left.setOnClickListener {
                    var intent = Intent(mContext,ViewImageActivity::class.java)
                    intent.putExtra("link",chat.url)
                    mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }
        }
        //text Messagess
        else {
            holder.show_msg.text = chat.message
        }

        // sent and seen message
        if (position == mChatList.size - 1) {
           if (chat.isseen){
               holder.msg_seen!!.text = "seen"

               if (chat.message.equals("sent you an image.") && !chat.url.equals("")) {
                   val lp: RelativeLayout.LayoutParams? = holder.msg_seen!!.layoutParams as
                           RelativeLayout.LayoutParams
                   lp!!.setMargins(0, 345, 0, 0)
                   holder.msg_seen!!.layoutParams == lp
               }
           }else{
               holder.msg_seen!!.text = "sent"

               if (chat.message.equals("sent you an image.") && !chat.url.equals("")) {
                   val lp: RelativeLayout.LayoutParams? = holder.msg_seen!!.layoutParams as
                           RelativeLayout.LayoutParams
                   lp!!.setMargins(0, 245, 0, 0)
                   holder.msg_seen!!.layoutParams == lp
               }
           }
        } else {
            holder.msg_seen!!.visibility = View.GONE
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var profile_image_left: CircleImageView
        lateinit var show_msg: TextView
        lateinit var msg_image_left: ImageView
        lateinit var msg_seen: TextView
        lateinit var msg_image_right: ImageView

        init {
            msg_seen = itemView.findViewById(R.id.msg_seen)
            msg_image_right = itemView.findViewById(R.id.msg_image)
            msg_image_left = itemView.findViewById(R.id.msg_image)
            show_msg = itemView.findViewById(R.id.show_msg)
            profile_image_left = itemView.findViewById(R.id.profile_image)

        }
    }

    override fun getItemViewType(position: Int): Int {

        return if (mChatList.get(position).sender.equals(currentUserId)) {
            1
        } else {
            0
        }
    }
}