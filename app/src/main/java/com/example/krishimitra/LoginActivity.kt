package com.example.krishimitra

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.krishimitra.databinding.ActivityLoginBinding
import com.example.krishimitra.fragments.UserProfileFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jakewharton.threetenabp.AndroidThreeTen.init
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding
    private lateinit var sendOTPBtn : Button
    private lateinit var auth : FirebaseAuth
    private var currentuser : FirebaseUser?=null
    private lateinit var phoneNumberET : EditText
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc : GoogleSignInClient
    private lateinit var authEmail : String

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mProgressBar : ProgressBar

    companion object {
        private const val RC_SIGN_IN = 123
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("USER_PREF", MODE_PRIVATE)


        auth = FirebaseAuth.getInstance()
        currentuser = auth.currentUser
//        auth = FirebaseAuth.getInstance()


        binding.otpbtn.setOnClickListener {
            val intent=Intent(this,UserLoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        init()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)



        binding.signIn.setOnClickListener {
            //signIn()
            signInGoogle()

        }

    }

    private fun signInGoogle(){
        val signInIntent = gsc.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                authEmail = auth.currentUser!!.email.toString()
                val db = Firebase.firestore
                val docRef = db.collection("User").document(authEmail)
//                val intent = Intent(this,HomeActivity::class.java)
//                intent.putExtra("email", account.email)
//                intent.putExtra("name", account.displayName)


                    sharedPreferences=applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
                    val editor=sharedPreferences!!.edit()
                    editor.putBoolean("isFirstTimeRun",true)
                    editor.apply()

//
//                startActivity(intent)
//                finish()

                docRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val email = documentSnapshot.getString("email")
                            if (email == authEmail){
                                val intent = Intent(this,HomeActivity::class.java)
                                intent.putExtra("email", account.email)
                                intent.putExtra("name", account.displayName)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            val intent = Intent(this, EditProfileActivity::class.java)
                            //  EmailData()
                            intent.putExtra("email", account.email)
                            intent.putExtra("name", account.displayName)
                            startActivity(intent)
                            Log.d("Success","Successful")
                            Log.d(TAG, "Document does not exist")
                            finish()
                        }
                    }

            } else {
                Log.d("Error",it.exception.toString())
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

            }
        }

    }



    private fun init(){
        mProgressBar = findViewById(R.id.phoneProgressBar)
        mProgressBar.visibility = View.INVISIBLE
        sendOTPBtn = binding.otpbtn
        phoneNumberET = binding.phoneNumber
    }




        }











