package com.communisolve.androidkotlinchatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jsibbold.zoomage.ZoomageView
import com.squareup.picasso.Picasso

class ViewImageActivity : AppCompatActivity() {
    lateinit var  zoomimage: ZoomageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        zoomimage = findViewById(R.id.myZoomageView)

        val link = intent.getStringExtra("link")
        Picasso.get().load(link).into(zoomimage)
    }
}