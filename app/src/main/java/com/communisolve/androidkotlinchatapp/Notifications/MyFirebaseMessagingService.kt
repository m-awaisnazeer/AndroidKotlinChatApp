package com.communisolve.androidkotlinchatapp.Notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.communisolve.androidkotlinchatapp.ChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val sented = p0.data["sented"]
        val user = p0.data["user"]
        val sharePref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        val currentOnlineUser = sharePref.getString("currentUser", null)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null && sented == firebaseUser.uid) {
            if (currentOnlineUser != user) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(p0)
                }else{
                    sendNotification(p0)
                }
            }
        }


    }

    private fun sendNotification(p0: RemoteMessage) {

        val user = p0.data["user"]
        val icon = p0.data["icon"]
        val title = p0.data["title"]
        val body = p0.data["body"]

        val notification = p0.notification

        val j = user!!.replace("[\\D]".toRegex(),"").toInt()
        val intent = Intent(this,ChatActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userid",user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder :NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(icon!!.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var i =0
        if (j>0){
            i=j
        }
        noti.notify(i,builder.build())




    }

    private fun sendOreoNotification(p0: RemoteMessage) {
        val user = p0.data["user"]
        val icon = p0.data["icon"]
        val title = p0.data["title"]
        val body = p0.data["body"]

        val notification = p0.notification

        val j = user!!.replace("[\\D]".toRegex(),"").toInt()
        val intent = Intent(this,ChatActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userid",user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val oreoNotification = OreoNotification(this)
        val builder:Notification.Builder = oreoNotification.getOreoNotification(
            title,
            body,
            pendingIntent,
            defaultSound,
            icon
        )

        var i =0
        if (j>0){
            i=j
        }
        oreoNotification.getManager!!.notify(i,builder.build())
    }
}