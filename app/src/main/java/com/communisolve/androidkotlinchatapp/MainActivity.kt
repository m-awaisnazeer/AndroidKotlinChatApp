package com.communisolve.androidkotlinchatapp

import android.os.Bundle
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.communisolve.androidkotlinchatapp.Common.currentUser
import com.communisolve.androidkotlinchatapp.Fragments.ChatsFragment
import com.communisolve.androidkotlinchatapp.Fragments.SearchFragment
import com.communisolve.androidkotlinchatapp.Fragments.SettingsFragment
import com.communisolve.androidkotlinchatapp.Model.Chat
import com.communisolve.androidkotlinchatapp.Model.User
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : AppCompatActivity() {

    lateinit var tab_layout: TabLayout
    lateinit var view_pager: ViewPager
    lateinit var profile_image: CircleImageView
    lateinit var user_name: TextView

    lateinit var databaseRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tab_layout = findViewById(R.id.tab_layout)
        view_pager = findViewById(R.id.view_pager)
        user_name = findViewById(R.id.user_name)
        profile_image = findViewById(R.id.profile_image)

//        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
//        viewPagerAdapter.addFragment(ChatsFragment(), "Chats")


        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var counterUnreadMessages = 0
                for (datasnapshot in snapshot.children) {
                    var chat: Chat? = datasnapshot.getValue(Chat::class.java)
                    if (chat!!.receiver.equals(FirebaseAuth.getInstance().uid) && !chat.isseen) {
                        counterUnreadMessages++
                    }
                }
                if (counterUnreadMessages == 0) {
                    viewPagerAdapter.addFragment(ChatsFragment(), "Chats")

                } else {
                    viewPagerAdapter.addFragment(ChatsFragment(), "($counterUnreadMessages) Chats")

                }
                viewPagerAdapter.addFragment(SearchFragment(), "Search")
                viewPagerAdapter.addFragment(SettingsFragment(), "Settings")

                view_pager.setAdapter(viewPagerAdapter)
                tab_layout.setupWithViewPager(view_pager)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }

    internal class ViewPagerAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!) {
        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        @Nullable
        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

        init {
            fragments = ArrayList()
            titles = ArrayList()
        }
    }


    override fun onStart() {
        super.onStart()

        databaseRef = FirebaseDatabase.getInstance().reference

        databaseRef.child("Users").child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    var user: User? = snapshot.getValue(User::class.java)

                    currentUser.fullname = user!!.fullname
                    currentUser.profile = user!!.profile

                    Picasso.get().load(currentUser.profile).into(profile_image)
                    user_name.text = currentUser.fullname


                }
            })

    }

}