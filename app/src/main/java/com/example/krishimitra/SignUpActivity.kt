package com.example.krishimitra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.krishimitra.databinding.ActivitySignUpBinding
import com.example.krishimitra.databinding.ActivityUserLoginBinding
import com.example.krishimitra.models.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var binding:ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_up)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val loginTxt = findViewById<TextView>(R.id.login_txt)
        loginTxt.setOnClickListener {
            val intent = Intent(this,UserLoginActivity::class.java)
            startActivity(intent)
        }


        val registerBtn = findViewById<Button>(R.id.register_btn)
        registerBtn.setOnClickListener {


            saveData()
        }

//            val nameTxt = findViewById<EditText>(R.id.name_txt)
//            val usernameTxt = findViewById<EditText>(R.id.username_txt)
//            val phoneTxt = findViewById<EditText>(R.id.phone_txt)
//            val emailTxt = findViewById<EditText>(R.id.email_txt)
//            val locationTxt = findViewById<EditText>(R.id.location_txt)
//            val passwordTxt = findViewById<EditText>(R.id.password_txt)
//
//            val name = nameTxt.text.trim().toString()
//            val username = usernameTxt.text.trim().toString()
//            val phone = phoneTxt.text.trim().toString()
//            val email = emailTxt.text.trim().toString()
//            val location = locationTxt.text.trim().toString()
//            val password = passwordTxt.text.trim().toString()
//
//            database = FirebaseDatabase.getInstance().getReference("Users")
//            val User = User(name,username,phone,email, location, password)
//            database.child(username).setValue(User).addOnSuccessListener {
//                nameTxt.text?.clear()
//                phoneTxt.text?.clear()
//                emailTxt.text?.clear()
//                usernameTxt.text?.clear()
//                locationTxt.text?.clear()
//                passwordTxt.text?.clear()
//
//                Toast.makeText(
//                    this,
//                    "Successfully Subscribed to the academy",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//
//            }.addOnFailureListener {
//                Toast.makeText(this, "😥 Wrong credentials", Toast.LENGTH_SHORT).show()
//
//            }


    }

    private fun saveData() {
        val db = FirebaseDatabase.getInstance().reference
        val database = Firebase.firestore
        //  binding.editTextEmail.inputType = InputType.TYPE_NULL



        if(binding.emailTxt.text!!.isEmpty() || binding.nameTxt.text!!.isEmpty() || binding.phoneTxt.text!!.isEmpty() || binding.locationTxt.text!!.isEmpty()){
            Toast.makeText(this,"Fill all the fields",Toast.LENGTH_SHORT).show()
            return
        }
        val User = User(binding.nameTxt.text.toString(),binding.phoneTxt.text.toString(),binding.emailTxt.text.toString(),binding.locationTxt.text.toString())

        val userData = hashMapOf(
            "email" to binding.emailTxt.text.toString(),
            "location" to binding.locationTxt.text.toString(),
            "mobileNumber" to binding.phoneTxt.text.toString(),
            "name" to binding.nameTxt.text.toString(),
            "username" to binding.usernameTxt.text.toString(),
            "password" to binding.passwordTxt.text.toString()
        )

        database.collection("User").document(binding.emailTxt.text.toString()).set(userData)
            .addOnSuccessListener {
                Toast.makeText(this,"Details added successfully",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,HomeActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {err ->
                Toast.makeText(this,"Error ${err.message}",Toast.LENGTH_SHORT).show()

            }
    }
}