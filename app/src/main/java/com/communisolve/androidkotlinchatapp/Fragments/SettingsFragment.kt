package com.communisolve.androidkotlinchatapp.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.communisolve.androidkotlinchatapp.LoginActivity
import com.communisolve.androidkotlinchatapp.R
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : Fragment() {

    lateinit var sign_out_btn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        sign_out_btn = rootView.findViewById(R.id.sign_out_btn)


        sign_out_btn.setOnClickListener { view ->
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity!!.finish()
        }
        return rootView
    }


}