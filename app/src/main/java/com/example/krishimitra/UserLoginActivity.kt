package com.example.krishimitra

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.krishimitra.databinding.ActivityUserLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserLoginActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding:ActivityUserLoginBinding
    private lateinit var gsc : GoogleSignInClient
    private lateinit var authEmail : String

    private lateinit var auth : FirebaseAuth
    private var currentuser : FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(R.layout.activity_login)
        binding = ActivityUserLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("USER_PREF", MODE_PRIVATE)

        val logBtn = findViewById<Button>(R.id.log_btn)
        val loginUsername = findViewById<TextView>(R.id.login_username)
        val loginPassword = findViewById<TextView>(R.id.login_password)

        val strUser = intent.getStringExtra("username")
        val strPassword = intent.getStringExtra("password")

        loginUsername.text=strUser
        loginPassword.text=strPassword

        auth = FirebaseAuth.getInstance()
        currentuser = auth.currentUser

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)

        binding.signIn.setOnClickListener {
            signInGoogle()
        }


        logBtn.setOnClickListener {
            val usernameLogin: String = loginUsername.text.toString().trim()
            val passwordLogin: String = loginPassword.text.toString().trim()

            if (usernameLogin.isNotEmpty()) {

                database = FirebaseDatabase.getInstance().getReference("Users")
                database.child(usernameLogin).get().addOnSuccessListener {

                    if (it.exists()) {
                        val userEmail = it.child("email").value
                        val username = it.child("username").value
                        val password = it.child("password").value

                        if (password == passwordLogin) {
                            // Save login credentials in shared preferences
                            val editor = sharedPreferences.edit()
                            editor.putString("username", username.toString())
                            editor.putString("password", password.toString())
                            editor.putString("email",userEmail.toString())
                            editor.apply()

                            Toast.makeText(this, "Welcome to Krishi Mitra", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(this, "Password is incorrect", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, SignUpActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                Toast.makeText(this, "Please enter the username", Toast.LENGTH_SHORT).show()
            }
        }

        val registerTxt = findViewById<TextView>(R.id.register_txt)
        registerTxt.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Check if login credentials exist in shared preferences
        val savedUsername = sharedPreferences.getString("username", null)
        val savedPassword = sharedPreferences.getString("password", null)
        val savedemail = sharedPreferences.getString("email", null)

        if (savedUsername != null && savedPassword != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signInGoogle() {
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
                val intent = Intent(this,HomeActivity::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)

//
                startActivity(intent)
                finish()

                docRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val email = documentSnapshot.getString("email")
                            if (email == authEmail){
                                val intent = Intent(this,HomeActivity::class.java)

                                val sharedPreferences = getSharedPreferences("USER_PREF", MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putBoolean("isUserLoggedIn", true)
                                editor.apply()

                                intent.putExtra("email", account.email)
                                intent.putExtra("name", account.displayName)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            val intent = Intent(this, SignUpActivity::class.java)
                            //  EmailData()
                            intent.putExtra("Gemail", account.email)
                            intent.putExtra("Gname", account.displayName)
                            startActivity(intent)
                            Log.d("Success","Successful")
                            Log.d(ContentValues.TAG, "Document does not exist")
                            finish()
                        }
                    }

            } else {
                Log.d("Error",it.exception.toString())
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

            }
        }

    }

}