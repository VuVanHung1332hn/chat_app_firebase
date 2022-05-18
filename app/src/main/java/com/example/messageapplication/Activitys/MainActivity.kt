package com.example.messageapplication.Activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messageapplication.Adapters.UserAdapter
import com.example.messageapplication.Model.User
import com.example.messageapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.activity_profile.*

class MainActivity : AppCompatActivity() {
    var userlist = ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()

        userRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)


        img_profile.setOnClickListener{
            val intent = Intent(this@MainActivity,ProfileActivity::class.java)
            startActivity(intent)
        }
        getUsersList()

    }
    fun getUsersList(){
        var firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var databaseReference:DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userlist.clear()
                val currentuser = snapshot.getValue(User::class.java)
                if (currentuser!!.profileImage == ""){
                    img_profile.setImageResource(R.drawable.profile)
                }else{
                    Glide.with(this@MainActivity).load(currentuser.profileImage).into(img_profile)
                }
                for (dataSnapshot:DataSnapshot in snapshot.children){
                    val user: User? = dataSnapshot.getValue(User::class.java)

                    if (!user!!.userId.equals(firebase.uid)){
                        userlist.add(user)
//                        Glide.with(this@MainActivity).load(currentuser.profileImage).into(userImage)
                    }
                }
                val userAdapter = UserAdapter(this@MainActivity,userlist)
                userRecyclerView.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}