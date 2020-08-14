package com.communisolve.androidkotlinchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.communisolve.androidkotlinchatapp.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    lateinit var username_edittext: EditText
    lateinit var email_edittext: EditText
    lateinit var fullname_edittext: EditText
    lateinit var password_edittext: EditText
    lateinit var signUp_PB: ProgressBar

    lateinit var SignUp_btn: Button
    lateinit var sign_txt: TextView

    lateinit var mAuth: FirebaseAuth
    lateinit var daatabaseRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        username_edittext = findViewById(R.id.user_name_su)
        email_edittext = findViewById(R.id.email_su)
        fullname_edittext = findViewById(R.id.fullname_su)
        password_edittext = findViewById(R.id.password_signUp)
        SignUp_btn = findViewById(R.id.signup_btn)
        sign_txt = findViewById(R.id.sign_txt)
        signUp_PB = findViewById(R.id.signUp_PB)

        mAuth = FirebaseAuth.getInstance()
        daatabaseRef = FirebaseDatabase.getInstance().reference



        SignUp_btn.setOnClickListener {
            signUp_PB.visibility = View.VISIBLE
            signUp()
        }
    }

    private fun signUp() {
        var email: String = email_edittext.text.toString()
        var password: String = password_edittext.text.toString()
        var username: String = username_edittext.text.toString()
        var fullname: String = fullname_edittext.text.toString()


        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username) || TextUtils.isEmpty(
                password
            )
        ) {
            signUp_PB.visibility = View.GONE
            Toast.makeText(applicationContext, "Enter All Information", Toast.LENGTH_SHORT).show()
        } else {

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var user: User = User(
                        FirebaseAuth.getInstance().uid!!,
                        fullname,
                        username,
                        "https://pbs.twimg.com/profile_images/1055263632861343745/vIqzOHXj.jpg",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRMti-9yi9IgVgWl6lOGlTfnNdHiIceEnJ80Q&usqp=CAU",
                        "offline",
                        username,
                        "https://m.facebook.com",
                        "https://m.instagram.com",
                        "https://www.google.com"
                    )
                    daatabaseRef.child("Users").child(FirebaseAuth.getInstance().uid!!)
                        .setValue(user).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signUp_PB.visibility = View.GONE
                                Toast.makeText(
                                    applicationContext,
                                    "Successfully created",
                                    Toast.LENGTH_SHORT
                                ).show()

                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish()
                            }
                        }.addOnFailureListener { exception ->
                            signUp_PB.visibility = View.GONE

                            Toast.makeText(
                                applicationContext,
                                "Error:\n" + exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    "Error:\n" + exception.message,
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }
}