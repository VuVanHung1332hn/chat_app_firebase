package com.example.messageapplication.Activitys

import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.messageapplication.Adapters.ChatAdapter
import com.example.messageapplication.Adapters.UserAdapter
import com.example.messageapplication.Model.Chat
import com.example.messageapplication.Model.User
import com.example.messageapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chats.*
import kotlinx.android.synthetic.main.activity_chats.back
import kotlinx.android.synthetic.main.activity_chats.username
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*

class ChatsActivity : AppCompatActivity() {
    var firebaseUser:FirebaseUser? = null
    var reference:DatabaseReference? = null
    var chatlist = ArrayList<Chat>()
    private val PICK_IMAGE_REQUEST:Int = 2020
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)
        supportActionBar!!.hide()
        chatRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        back.setOnClickListener{
            onBackPressed()
        }
        btn_messageimage.setOnClickListener {
            chooseImage()
        }
        var intent = getIntent()
        var userId = intent.getStringExtra("userId")

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance()
            .getReference("Users").child(userId!!)

        reference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(User::class.java)
                username.text = user!!.username
                if (user.profileImage != ""){
                    Glide.with(this@ChatsActivity).load(user.profileImage).into(usersImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        btn_message.setOnClickListener{
            var message:String = edtmessage.text.toString()

            if (message.isEmpty()){
                Toast.makeText(applicationContext,"Vui lòng nhập tin nhắn...",Toast.LENGTH_SHORT).show()
                edtmessage.setText("")
            }else{
                sendMessage(firebaseUser!!.uid,userId,message)
                edtmessage.setText("")
            }
        }
        readMassage(firebaseUser!!.uid, userId)

    }

    private fun chooseImage(){
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"select Image"),PICK_IMAGE_REQUEST)
    }

    private fun sendMessage (senderId: String, receiverId: String, message: String){
        var reference: DatabaseReference? = FirebaseDatabase.getInstance().getReference()

        var hashMap:HashMap<String,String> = HashMap()
        hashMap.put("senderId",senderId)
        hashMap.put("receiverId",receiverId)
        hashMap.put("message",message)

        reference!!.child("chat").push().setValue(hashMap)

    }
    fun readMassage(senderId: String,receiverId: String){

        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("chat")

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatlist.clear()

                for (dataSnapshot:DataSnapshot in snapshot.children){
                    val chat = dataSnapshot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId)
                        || chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                            ){
                        chatlist.add(chat)
//                        Glide.with(this@MainActivity).load(currentuser.profileImage).into(userImage)
                    }
                }
                val chatAdapter = ChatAdapter(this@ChatsActivity, chatlist)

                chatRecyclerView.adapter = chatAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}