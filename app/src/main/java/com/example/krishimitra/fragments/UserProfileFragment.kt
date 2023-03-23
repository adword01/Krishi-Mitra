package com.example.krishimitra.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.krishimitra.EditProfileActivity
import com.example.krishimitra.LoginActivity
import com.example.krishimitra.R
import com.example.krishimitra.databinding.FragmentGreetBinding
import com.example.krishimitra.databinding.FragmentUserProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserProfileFragment : Fragment() {


    private lateinit var Editbutton : Button
    private lateinit var binding : FragmentUserProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var authName : String
    private lateinit var authEmail : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentUserProfileBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_user_profile, container, false)

        auth = FirebaseAuth.getInstance()
        authName = auth.currentUser!!.displayName.toString()
        authEmail = auth.currentUser!!.email.toString()

        binding.profileName.text = authName
        binding.mobNumber.text =  authEmail

        getData()
        binding.logoutbtn.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val gsc = GoogleSignIn.getClient(requireActivity(),gso)
            gsc.signOut()
            val intent = Intent(activity,LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
           // auth.signOut()
        }
        binding.editprofilebtn.setOnClickListener {
           val intent = Intent(activity,EditProfileActivity::class.java)
            startActivity(intent)


        }
        return binding.root
    }

    private fun getData() {
    //    val db = FirebaseDatabase.getInstance().reference

        val db = Firebase.firestore
        val docRef = db.collection("User").document(authEmail)

        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val email = documentSnapshot.getString("email")
                    val location = documentSnapshot.getString("location")
                    val mobNumber = documentSnapshot.getString("mobileNumber")
                    val name = documentSnapshot.getString("name")
                    binding.Nametxt.text = name
                    binding.emailtxt.text = email
                    binding.phonetxt.text = mobNumber
                    binding.addresstxt.text = location

                    Log.d(TAG, "Email: $email")
                } else {
                    Log.d(TAG, "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting document", exception)
            }

//        db.child("User").get().addOnSuccessListener {
//            if(it.exists()){
//                binding.Nametxt.text = it.child("name").value.toString()
//                binding.emailtxt.text = it.child("email").value.toString()
//                binding.phonetxt.text = it.child("mobileNumber").value.toString()
//                binding.addresstxt.text = it.child("location").value.toString()
//            }
//        }
    }


}