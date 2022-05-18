package com.example.messageapplication.Adapters

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messageapplication.Activitys.ChatsActivity
import com.example.messageapplication.Model.User
import com.example.messageapplication.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_profile.*

class UserAdapter(private val context:Context,private val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_users,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.txtUserName.text = user.username
        Glide.with(context).load(user.profileImage).into(holder.imageUser)

        holder.layoutUser.setOnClickListener{
            val intent = Intent(context, ChatsActivity::class.java)
            intent.putExtra("userId",user.userId)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return userList.size
    }
    class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val txtUserName:TextView = view.findViewById(R.id.username)
//        val txtDummy:TextView = view.findViewById(R.id.username)
        val imageUser:CircleImageView = view.findViewById(R.id.usersImage)
        val layoutUser:LinearLayout = view.findViewById(R.id.layoutUser)

    }
}