package com.example.messageapplication.Activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.messageapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.btn_SignUp
import kotlinx.android.synthetic.main.activity_sign_up.edtemail
import kotlinx.android.synthetic.main.activity_sign_up.edtpassword

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar!!.hide()
        auth = FirebaseAuth.getInstance()
        btn_SignUp.setOnClickListener{
            val userName = edtname.text.toString()
            val email = edtemail.text.toString()
            val password = edtpassword.text.toString()
            val confirmPassword = edtpasswordconfirm.text.toString()

            if (TextUtils.isEmpty(userName)){
                Toast.makeText(applicationContext, "tên người dùng là bắt buộc", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(email)){
                Toast.makeText(applicationContext, "Email là bắt buộc", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(password)){
                Toast.makeText(applicationContext, "Mật khẩu là bắt buộc", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(confirmPassword)){
                Toast.makeText(applicationContext, "Nhập lại mật khẩu là bắt buộc", Toast.LENGTH_SHORT).show()
            }
            if (password != confirmPassword){
                Toast.makeText(applicationContext, "Không khớp mật khẩu", Toast.LENGTH_SHORT).show()
            }
            registerUser(userName,email,password)

        }
        btn_Login.setOnClickListener{
            val intent = Intent(this@SignUpActivity,LoginActivity::class.java)
            startActivity(intent)
        }
    }
    private fun registerUser(userName:String,email:String,password:String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    val user: FirebaseUser? = auth.currentUser
                    val userId:String = user!!.uid

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashMap:HashMap<String,String> = HashMap()
                    hashMap.put("userId",userId)
                    hashMap.put("username",userName)
                    hashMap.put("profileImage","https://firebasestorage.googleapis.com/v0/b/messageapp-84113.appspot.com/o/image%2Fprofile.png?alt=media&token=8945747e-d2ad-418c-859b-fcc132ec51ae")
                    databaseReference.setValue(hashMap).addOnCompleteListener(this){
                        if (it.isSuccessful){
                            edtname.setText("")
                            edtemail.setText("")
                            edtpassword.setText("")
                            edtpasswordconfirm.setText("")
//                            Mở file chủ
                            var intent = Intent(this@SignUpActivity,MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
    }
}