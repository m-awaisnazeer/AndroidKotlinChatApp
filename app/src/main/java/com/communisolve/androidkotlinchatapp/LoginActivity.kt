package com.communisolve.androidkotlinchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var login_btn: Button
    lateinit var sign_up: TextView
    lateinit var PB_login: ProgressBar

    lateinit var mAuth: FirebaseAuth


    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        login_btn = findViewById(R.id.login_btn)
        sign_up = findViewById(R.id.sign_up)
        PB_login = findViewById(R.id.PB_login)

        mAuth = FirebaseAuth.getInstance()
        sign_up.setOnClickListener { view ->
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
            finish()
        }


        login_btn.setOnClickListener {

            PB_login.visibility = View.VISIBLE
            signIn()
        }
    }

    private fun signIn() {
        var emails: String = email.text.toString()
        var passwords: String = password.text.toString()
        if (TextUtils.isEmpty(emails) || TextUtils.isEmpty(passwords)) {
            PB_login.visibility = View.GONE
            Toast.makeText(
                applicationContext,
                "Enter All Fields", Toast.LENGTH_SHORT
            ).show()
        } else {
            mAuth.signInWithEmailAndPassword(emails, passwords)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        PB_login.visibility = View.GONE
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    } else {
                        PB_login.visibility = View.GONE
                        Toast.makeText(
                            applicationContext,
                            "Error:\n" + task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}