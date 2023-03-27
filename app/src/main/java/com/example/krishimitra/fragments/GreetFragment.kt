package com.example.krishimitra.fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.krishimitra.databinding.FragmentGreetBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class GreetFragment : Fragment() {

    private lateinit var binding : FragmentGreetBinding
    private lateinit var phoneNumberWithoutCountryCode : String
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth : FirebaseAuth
    private lateinit var authName : String
    private lateinit var authEmail : String
    private lateinit var authnumber : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGreetBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment

//        val args = this.arguments
//        val inputData = args?.get("name")
//        binding.profileName.text = inputData.toString()
//



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        auth = FirebaseAuth.getInstance()
        authName = auth.currentUser!!.displayName.toString()
        authEmail = auth.currentUser!!.email.toString()
        authnumber = auth.currentUser!!.phoneNumber.toString()
        phoneNumberWithoutCountryCode = authnumber?.replace("^\\+\\d{1,2}".toRegex(), "").toString()


        if(auth.currentUser!!.email != null){
          //  EmailData()
           binding.username.text  = authName
        }else{
            phoneData()
        }

        val date = Date()
        val cal: Calendar = Calendar.getInstance()
        cal.time = date
        when(cal.get(Calendar.HOUR_OF_DAY)){
            in 0..12 ->{
                binding.greettext.text = "Good Morning!!"
            }
            in 12..17 ->{
                binding.greettext.text = "Good Afternoon!!"
            }
            in 17..21 ->{
                binding.greettext.text = "Good evening!!"
            }
            else -> {
                binding.greettext.text = "Good Night!!"
            }
        }

        //    return inflater.inflate(R.layout.fragment_greet, container, false)
        return binding.root
    }



    private fun phoneData(){
        val db = Firebase.firestore
        val docRef = db.collection("User")
        val searchvalue = phoneNumberWithoutCountryCode

        docRef.whereEqualTo("mobileNumber",searchvalue)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents){
                    val name = document.getString("name")

                    binding.username.text = name
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting document", exception)
            }
    }


}