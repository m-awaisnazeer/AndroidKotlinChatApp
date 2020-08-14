package com.communisolve.androidkotlinchatapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.communisolve.androidkotlinchatapp.Adapter.ChatAdapter
import com.communisolve.androidkotlinchatapp.Common.SearchUser
import com.communisolve.androidkotlinchatapp.Common.currentUser
import com.communisolve.androidkotlinchatapp.Fragments.APIService
import com.communisolve.androidkotlinchatapp.Model.Chat
import com.communisolve.androidkotlinchatapp.Model.User
import com.communisolve.androidkotlinchatapp.Notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    lateinit var chats_messages: RecyclerView
    lateinit var profile_image: CircleImageView
    lateinit var user_name: TextView
    lateinit var status: TextView
    lateinit var attachment: CircleImageView
    lateinit var messageEditText: EditText
    lateinit var send: CircleImageView
    var chatsAdapter: ChatAdapter? = null
    var mChatList: List<Chat>? = null
    var reference:DatabaseReference?=null

    var notify = false
    var apiService: APIService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chats_messages = findViewById(R.id.chats_messages)
        chats_messages.setHasFixedSize(true)
        val linearlayoutMannager = LinearLayoutManager(this)
        linearlayoutMannager.stackFromEnd = true
        chats_messages.layoutManager = linearlayoutMannager
        profile_image = findViewById(R.id.profile_image)
        user_name = findViewById(R.id.user_name)
        status = findViewById(R.id.status)
        messageEditText = findViewById(R.id.message)
        send = findViewById(R.id.send)
        attachment = findViewById(R.id.attachment)

        Picasso.get().load(SearchUser.profile).into(profile_image)
        user_name.setText(SearchUser.fullname)
        status.setText(SearchUser.status)

        apiService = Client.client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        seenMessage(SearchUser.uid)
        send.setOnClickListener { view ->
            notify = true
            val message: String = messageEditText.text.toString()
            if (message == "") {

            } else {
                retrieveMessages(
                    FirebaseAuth.getInstance()!!.uid,
                    SearchUser.uid,
                    SearchUser.profile
                )
                sendMessageToUser(
                    FirebaseAuth.getInstance().uid,
                    SearchUser.uid,
                    message
                )
            }
            messageEditText.setText("")


        }

        attachment.setOnClickListener { view ->
            notify = true
            retrieveMessages(FirebaseAuth.getInstance()!!.uid, SearchUser.uid, SearchUser.profile)
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
        }

        retrieveMessages(FirebaseAuth.getInstance()!!.uid, SearchUser.uid, SearchUser.profile)

    }


    private fun sendMessageToUser(senderUid: String?, receiverId: String, message: String) {
        val reference = FirebaseDatabase.getInstance().reference

        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderUid
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val chatsListReference = FirebaseDatabase.getInstance()
                        .reference.child("ChatList")
                        .child(FirebaseAuth.getInstance().uid!!)
                        .child(SearchUser.uid)

                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {


                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                chatsListReference.child("id").setValue(SearchUser.uid)
                            }
                            val chatsListReceiverReference = FirebaseDatabase.getInstance()
                                .reference.child("ChatList")
                                .child(SearchUser.uid)
                                .child(FirebaseAuth.getInstance().uid!!)
                            chatsListReceiverReference.child("id")
                                .setValue(FirebaseAuth.getInstance().uid!!)

                        }
                    })
                }
            }

        //implements the push nogifications using fcm
        val userreference = FirebaseDatabase.getInstance().reference
            .child("Users").child(FirebaseAuth.getInstance().uid!!)

        userreference.addValueEventListener(object  : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (notify){
                    sendNotification(receiverId,user!!.fullname,message)
                }
                notify = false
            }

        })

    }

    private fun sendNotification(receiverId: String, fullname: String, message: String) {

        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")

        val query = ref.orderByKey().equalTo(receiverId)

        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for ( ds in snapshot.children){
                    val token:Token? = ds.getValue(Token::class.java)
                    val data = Data(FirebaseAuth.getInstance().uid!!
                        ,R.mipmap.ic_launcher,
                        "$user_name : $message",
                        "New Message",
                        SearchUser.uid
                    )

                    val sender = Sender(data!!,token!!.getToken().toString())

                    apiService!!.sendNotification(sender)
                        .enqueue(object  : Callback<MyResponse>{
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200){
                                    if (response.body()!!.success !== 1)
                                    {
                                        Toast.makeText(this@ChatActivity,"Failed Nothing Happen",Toast.LENGTH_LONG).show()

                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {


                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data!!.data != null) {
            val loadingBar = ProgressDialog(this@ChatActivity)
            loadingBar.setMessage("image sending")
            loadingBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance()
                .reference.child("Chat Images")

            val ref = FirebaseDatabase.getInstance().reference
            val msgId = ref.push().key
            val filePath = storageReference.child("$msgId.jpg")


            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()


                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = FirebaseAuth.getInstance().uid
                    messageHashMap["message"] = "sent you an image."
                    messageHashMap["receiver"] = SearchUser.uid
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = msgId

                    ref.child("Chats").child(msgId!!)
                        .setValue(messageHashMap)
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful){
                                loadingBar.dismiss()

                                reference!!.addValueEventListener(object  : ValueEventListener{
                                    override fun onCancelled(error: DatabaseError) {


                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val user = snapshot.getValue(User::class.java)
                                        if (notify){
                                            sendNotification(SearchUser.uid,user!!.fullname,"sent you an image.")
                                        }
                                        notify = false
                                    }

                                })
                            }
                        }

                    Log.d("URL", "" + url)

                    Toast.makeText(this@ChatActivity, url, Toast.LENGTH_LONG).show()

                }
            }
        }

    }


    private fun retrieveMessages(senderuid: String?, visitorUid: String, receiverImgUrl: String) {
        mChatList = ArrayList()

         reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()
                for (forlsnapshot: DataSnapshot in snapshot.children) {

                    var chat: Chat? = forlsnapshot.getValue(Chat::class.java)
                    if (chat!!.receiver.equals(senderuid) && chat.sender.equals(visitorUid)
                        || chat.receiver.equals(visitorUid) && chat.sender.equals(senderuid)
                    ) {
                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                    chatsAdapter = ChatAdapter(
                        this@ChatActivity,
                        mChatList as ArrayList<Chat>, receiverImgUrl
                    )
                    chats_messages.adapter = chatsAdapter
                }

            }

        })

    }

    var seenListner: ValueEventListener? = null
    private fun seenMessage(userId: String) {
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListner = reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for (ds in snapshot.children) {
                    val chat = ds.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(FirebaseAuth.getInstance().uid) && chat.sender.equals(
                            userId
                        )
                    ) {
                        val hashMap = HashMap<String,Any>()
                        hashMap["isseen"] = true
                        ds.ref.updateChildren(hashMap)
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()

        reference!!.removeEventListener(seenListner!!)
    }
}