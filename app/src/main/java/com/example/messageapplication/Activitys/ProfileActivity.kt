package com.example.messageapplication.Activitys

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import com.example.messageapplication.Model.User
import com.example.messageapplication.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private var filePath: Uri? = null

    private val PICK_IMAGE_REQUEST:Int = 2020
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar!!.hide()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: User? = snapshot.getValue(User::class.java)
                username.setText(user!!.username)
                if (user.profileImage == ""){
//                    userImage.setImageResource(R.drawable.profile)
                }else{
                    Glide.with(this@ProfileActivity).load(user.profileImage).into(userImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message,Toast.LENGTH_SHORT).show()
            }
        })



        back.setOnClickListener{
            onBackPressed()
        }
        Log_out.setOnClickListener{
            if (firebaseUser != databaseReference) {
                val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        userImage.setOnClickListener{
            chooseImage()
        }
        btn_save.setOnClickListener{
            uploadImage()
            progressBar.visibility = View.VISIBLE
        }
    }
//    tạo chức năng chọn hình ảnh
    private fun chooseImage(){
        val intent:Intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_GET_CONTENT
    startActivityForResult(Intent.createChooser(intent,"select Image"),PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode != null){
            filePath = data!!.data
            try {
                var bitmap:Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filePath)
                userImage.setImageBitmap(bitmap)
                btn_save.visibility = View.VISIBLE
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }
    private fun uploadImage(){
        if (filePath != null){


            var ref:StorageReference = storageRef.child("image/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    Log.d("ProfileActivity", "Successfully uploaded image: ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("ProfileActivity","File Location: $it")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                    val hashMap:HashMap<String,String> = HashMap()
                    hashMap.put("username",username.text.toString())
                    hashMap.put("profileImage",filePath.toString())
                    databaseReference.updateChildren(hashMap as Map<String, String>)
                        progressBar.visibility = View.GONE
                        Toast.makeText(applicationContext,"Đã Tải Lên",Toast.LENGTH_SHORT).show()
                        btn_save.visibility = View.GONE

                }.addOnFailureListener{

//                        progressBar.visibility = View.GONE
//                        Toast.makeText(applicationContext,"Lỗi" + it.message,Toast.LENGTH_SHORT).show()

                }


        }
    }

    private fun saveUserToFirebaseDatabase(profileImage: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        val user = User(uid, username.text.toString() ,profileImage)

        ref.setValue(user).addOnSuccessListener {
            Log.d("ProfileActivity", "Finally we saved the user to Firebase Database")
        }
    }
//    class User(val userId:String,val username:String, val profileImage:String)
}