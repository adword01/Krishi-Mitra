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
import android.widget.Toast
import com.example.krishimitra.EditProfileActivity
import com.example.krishimitra.LoginActivity
import com.example.krishimitra.R
import com.example.krishimitra.databinding.FragmentGreetBinding
import com.example.krishimitra.databinding.FragmentUserProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlin.concurrent.fixedRateTimer

class UserProfileFragment : Fragment() {


    private lateinit var Editbutton : Button
    private lateinit var binding : FragmentUserProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var authName : String
    private lateinit var authEmail : String
    private lateinit var authnumber : String
    private lateinit var phoneNumberWithoutCountryCode : String
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentUserProfileBinding.inflate(inflater,container,false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())

        if (account?.photoUrl != null) {
            Picasso.get()
                .load(account.photoUrl)
                .into(binding.profileImage)
        }
        auth = FirebaseAuth.getInstance()
        authName = auth.currentUser!!.displayName.toString()
        authEmail = auth.currentUser!!.email.toString()
        authnumber = auth.currentUser!!.phoneNumber.toString()
        phoneNumberWithoutCountryCode = authnumber?.replace("^\\+\\d{1,2}".toRegex(), "").toString()


        if(auth.currentUser!!.email != null){
            EmailData()
            binding.profileName.text = authName
            binding.mobNumber.text =  authEmail
        }else{
            phoneData()
        }

        binding.logoutbtn.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val gsc = GoogleSignIn.getClient(requireActivity(),gso)
            gsc.signOut()
            auth.signOut()
            val intent = Intent(activity,LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        binding.editprofilebtn.setOnClickListener {
           val intent = Intent(activity,EditProfileActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }


    private fun EmailData(){
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
    }
    private fun phoneData(){
        val db = Firebase.firestore
        val docRef = db.collection("User")
        val searchvalue = phoneNumberWithoutCountryCode

        docRef.whereEqualTo("mobileNumber",searchvalue)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents){
                    val email = document.getString("email")
                    val location = document.getString("location")
                    val mobNumber = document.getString("mobileNumber")
                    val name = document.getString("name")

                    binding.Nametxt.text = name
                    binding.emailtxt.text = email
                    binding.phonetxt.text = mobNumber
                    binding.addresstxt.text = location
                    binding.profileName.text = name
                    binding.mobNumber.text =  email
                    Log.d(TAG, "Email: $email")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting document", exception)
            }
    }

}