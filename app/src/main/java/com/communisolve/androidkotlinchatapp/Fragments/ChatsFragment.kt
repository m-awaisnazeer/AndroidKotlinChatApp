package com.communisolve.androidkotlinchatapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.communisolve.androidkotlinchatapp.Adapter.SearchAdapter
import com.communisolve.androidkotlinchatapp.Model.ChatListJ
import com.communisolve.androidkotlinchatapp.Model.Chatlist
import com.communisolve.androidkotlinchatapp.Model.User
import com.communisolve.androidkotlinchatapp.Notifications.Token
import com.communisolve.androidkotlinchatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_chats.*


class ChatsFragment : Fragment() {
    lateinit var ChatListRV: RecyclerView
    lateinit var allUsers: ArrayList<User>
    lateinit var allChatList: ArrayList<String>
    lateinit var adapter: SearchAdapter
    lateinit var databaseRef: DatabaseReference
    lateinit var mChatUsers: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_chats, container, false)
        databaseRef = FirebaseDatabase.getInstance().reference
        ChatListRV = view.findViewById(R.id.ChatList)
        val layoutManagaer = LinearLayoutManager(activity)
        ChatListRV.layoutManager = layoutManagaer
        ChatListRV.setHasFixedSize(true)

        updateToken(FirebaseInstanceId.getInstance().token)
        fetchChatList()

        return view
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(FirebaseAuth.getInstance().uid!!).setValue(token1)

    }

    private fun fetchChatList() {
        databaseRef.child("ChatList").child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        allChatList = ArrayList<String>()

                        for (ds: DataSnapshot in snapshot.children) {
                            var mChatList:String = ds.child("id").value.toString()
                            allChatList.add(mChatList!!)

                        }
                        listAllUsers()

                    } else {
                        Toast.makeText(activity, "No Data to show", Toast.LENGTH_SHORT).show()

                    }

                }

            })
    }

    private fun listAllUsers() {
        databaseRef.child("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        allUsers = ArrayList<User>()

                        for (ds: DataSnapshot in snapshot.children) {
                            var user: User? = ds.getValue(User::class.java)

                            for (each in 0 until  allChatList.size){
                                if (user!!.uid.equals(allChatList.get(each))){
                                    allUsers.add(user!!)
                                }

                            }
                        }

                        adapter = SearchAdapter(activity!!, allUsers)
                        ChatListRV.adapter = adapter



                    } else {
                        Toast.makeText(activity, "No Data to show", Toast.LENGTH_SHORT).show()

                    }

                }

            })
    }



}

