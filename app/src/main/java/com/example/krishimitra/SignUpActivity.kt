package com.example.krishimitra

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.krishimitra.databinding.ActivitySignUpBinding
import com.example.krishimitra.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var binding:ActivitySignUpBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var authEmail : String
    private lateinit var authName : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_up)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        auth = FirebaseAuth.getInstance()
        authEmail = intent.getStringExtra("Gemail").toString()
        val sharedPreferences = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        val email = sharedPreferences.getString("email","email")
        authEmail = email.toString()

//        if (authEmail==null){
//          startActivity(Intent(this,HomeActivity::class.java))
//        }else{
//            authEmail = auth.currentUser!!.email.toString()
//            authName = auth.currentUser!!.displayName.toString()
//            binding.emailTxt.setText(authEmail)
//            binding.emailTxt.isEnabled = false
//            binding.nameTxt.setText(authName)
//            binding.nameTxt.isEnabled = false
//        }
//        if(auth.currentUser != null){
//            startActivity(Intent(this,HomeActivity::class.java))
//        }else{
//            authEmail = auth.currentUser!!.email.toString()
//            authName = auth.currentUser!!.displayName.toString()
//            binding.emailTxt.setText(authEmail)
//            binding.emailTxt.isEnabled = false
//            binding.nameTxt.setText(authName)
//            binding.nameTxt.isEnabled = false
//        }

//        val loginTxt = findViewById<TextView>(R.id.login_txt)
        binding.loginTxt.setOnClickListener {
            val intent = Intent(this,UserLoginActivity::class.java)
            startActivity(intent)
        }


//        val registerBtn = findViewById<Button>(R.id.register_btn)
        binding.registerBtn.setOnClickListener {
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

        db.child("Users").child(binding.usernameTxt.text.toString()).setValue(userData)
            .addOnSuccessListener {
                Toast.makeText(this,"Details added successfully",Toast.LENGTH_SHORT).show()
//                intent.putExtra("username", binding.usernameTxt.text.toString())
//                intent.putExtra("password", binding.passwordTxt.text.toString())
                val intent = Intent(this,UserLoginActivity::class.java)
//                FirebaseMessaging.getInstance().subscribeToTopic("tasks")

                startActivity(intent)
                finish()
            }.addOnFailureListener {err ->
                Toast.makeText(this,"Error ${err.message}",Toast.LENGTH_SHORT).show()

            }

        database.collection("User").document(binding.emailTxt.text.toString()).set(userData)
            .addOnSuccessListener {
//                Toast.makeText(this,"Details added successfully",Toast.LENGTH_SHORT).show()
//                intent.putExtra("username", binding.usernameTxt.text.toString())
//                intent.putExtra("password", binding.passwordTxt.text.toString())
 //               val intent = Intent(this,UserLoginActivity::class.java)
//                FirebaseMessaging.getInstance().subscribeToTopic("tasks")

//                startActivity(intent)
//                finish()
                Toast.makeText(this,"User created successfully",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {err ->
                Toast.makeText(this,"Error ${err.message}",Toast.LENGTH_SHORT).show()

            }
    }
}