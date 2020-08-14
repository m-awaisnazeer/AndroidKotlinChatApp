package com.communisolve.androidkotlinchatapp.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.communisolve.androidkotlinchatapp.Adapter.SearchAdapter
import com.communisolve.androidkotlinchatapp.Model.User
import com.communisolve.androidkotlinchatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class SearchFragment : Fragment() {

    lateinit var databaseRef: DatabaseReference
    lateinit var allUsers: ArrayList<User>
    lateinit var adapter: SearchAdapter
    lateinit var all_user_RV: RecyclerView
    lateinit var search_ET: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_search, container, false)
        all_user_RV = view.findViewById(R.id.all_user_RV)
        search_ET = view.findViewById(R.id.search_ET)
        all_user_RV.setHasFixedSize(true)
        all_user_RV.layoutManager = LinearLayoutManager(activity)
        Log.d(TAG, "onCreateView: ")

        databaseRef = FirebaseDatabase.getInstance().reference
        listAllUsers()


        search_ET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchForUsers(p0.toString().toLowerCase())

            }

        })

        return view
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
                            allUsers.add(user!!)

                        }

                        adapter = SearchAdapter(activity!!, allUsers)
                        all_user_RV.adapter = adapter
                    } else {
                        Toast.makeText(activity, "No Data to show", Toast.LENGTH_SHORT).show()

                    }

                }

            })
    }

    private fun searchForUsers(str: String) {

        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("Users").orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                allUsers = ArrayList<User>()

                for (snap in snapshot.children) {
                    val user: User? = snap.getValue(User::class.java)

                    allUsers.add(user!!)

                }
                adapter = SearchAdapter(activity!!, allUsers)
                all_user_RV.adapter = adapter


            }

        })
    }

    companion object {
        private const val TAG = "SearchFragment"
    }

}