package com.example.krishimitra

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.krishimitra.databinding.ActivityEditProfileBinding
import com.example.krishimitra.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var authEmail : String
    private lateinit var authName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences =getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val searchvalue = sharedPreferences.getString("email", "User")

        auth = FirebaseAuth.getInstance()

        if(searchvalue!!.isEmpty()){
            authEmail = auth.currentUser!!.email.toString()
            authName = auth.currentUser!!.displayName.toString()
            binding.editTextEmail.setText(authEmail)
            binding.editTextEmail.isEnabled = false
            binding.editTextName.setText(authName)
            binding.editTextName.isEnabled = false
            Log.d("userEmail",authEmail)
        }else{
            binding.editTextEmail.setText(searchvalue)
            binding.editTextEmail.isEnabled = false

        }



        binding.saveinfo.setOnClickListener {
            saveData()
        }


    }

    private fun saveData() {
        val db = FirebaseDatabase.getInstance().reference
        val database = Firebase.firestore
      //  binding.editTextEmail.inputType = InputType.TYPE_NULL



        if(binding.editTextEmail.text!!.isEmpty() || binding.editTextName.text!!.isEmpty() || binding.editTextMobileNumber.text!!.isEmpty() || binding.editTextLocation.text!!.isEmpty()){
            Toast.makeText(this@EditProfileActivity,"Fill all the fields",Toast.LENGTH_SHORT).show()
            return
        }
        val User = User(binding.editTextName.text.toString(),binding.editTextMobileNumber.text.toString(),binding.editTextEmail.text.toString(),binding.editTextLocation.text.toString())

        val userData = hashMapOf(
            "email" to binding.editTextEmail.text.toString(),
            "location" to binding.editTextLocation.text.toString(),
            "mobileNumber" to binding.editTextMobileNumber.text.toString(),
            "name" to binding.editTextName.text.toString()
        )

        database.collection("User").document(binding.editTextEmail.text.toString()).set(userData)
            .addOnSuccessListener {
                Toast.makeText(this@EditProfileActivity,"Details added successfully",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@EditProfileActivity,HomeActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {err ->
                Toast.makeText(this,"Error ${err.message}",Toast.LENGTH_SHORT).show()

            }
//        db.child("User").setValue(User)
//            .addOnSuccessListener {
//                Toast.makeText(this@EditProfileActivity,"Details added successfully",Toast.LENGTH_SHORT).show()
//                val intent = Intent(this@EditProfileActivity,HomeActivity::class.java)
//                startActivity(intent)
//                finish()
//
//            }.addOnFailureListener { err ->
//                Toast.makeText(this,"Error ${err.message}",Toast.LENGTH_SHORT).show()
//            }

    }

}