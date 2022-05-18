package com.example.messageapplication.Activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.messageapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()
        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!

//        kiểm tra xem đăng nhập người dùng sau đó dẫn hướng đến màn hình người dùng
//        if (firebaseUser != null){
//            val intent = Intent(this@LoginActivity,MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

//        xử lý nút login
        btn_LogIn.setOnClickListener{
//            gọi email và password tại xml
            val email = edtemail.text.toString()
            val password = edtpassword.text.toString()
//            yêu cầu nhập tài khoản mật khẩu
            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                Toast.makeText(applicationContext,"Yêu cầu nhập tài khoản, mật khẩu",Toast.LENGTH_SHORT).show()
            } else {
//                gọi đến cơ sở dữ liệu check email và password
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
                    if(it.isSuccessful){
//                        nhập đúng dẫn tới file Main
                        edtemail.setText("")
                        edtpassword.setText("")
                        val intent = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
//                        nhập sai hiện thông báo
                        Toast.makeText(applicationContext,"Tài khoản và mật khẩu không đúng",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
        btn_SignUp.setOnClickListener{
            val intent = Intent(this@LoginActivity,SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}