package com.example.krishimitra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.krishimitra.databinding.ActivityEditProfileBinding
import com.example.krishimitra.models.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveinfo.setOnClickListener {
            saveData()
        }


    }

    private fun saveData() {
        val db = FirebaseDatabase.getInstance().reference

        if(binding.editTextEmail.text!!.isEmpty() || binding.editTextName.text!!.isEmpty() || binding.editTextMobileNumber.text!!.isEmpty() || binding.editTextLocation.text!!.isEmpty()){
            Toast.makeText(this@EditProfileActivity,"Fill all the fields",Toast.LENGTH_SHORT).show()
            return
        }
        val User = User(binding.editTextName.text.toString(),binding.editTextMobileNumber.text.toString(),binding.editTextEmail.text.toString(),binding.editTextLocation.text.toString())

        db.child("User").setValue(User)
            .addOnSuccessListener {
                Toast.makeText(this@EditProfileActivity,"Details added successfully",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@EditProfileActivity,HomeActivity::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener { err ->
                Toast.makeText(this,"Error ${err.message}",Toast.LENGTH_SHORT).show()
            }

    }

}