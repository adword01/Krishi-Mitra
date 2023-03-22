package com.example.krishimitra

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.krishimitra.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding

    private lateinit var auth : FirebaseAuth
    private var currentuser : FirebaseUser?=null

    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc : GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()
        currentuser = auth.currentUser
        auth = FirebaseAuth.getInstance()


        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)

        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            navigateToSecondActivity()
        }


        binding.signIn.setOnClickListener {
            signIn()
        }
        binding.loginButton.setOnClickListener {
          //  binding.verifyBtn.setVisibility(View.VISIBLE)
         //   binding.verifyTxt.setVisibility(View.VISIBLE)
         //   binding.loginButton.setVisibility(View.INVISIBLE)

            show()
        }

//        binding.verifyBtn.setOnClickListener {
//            val intent =Intent(this,HomeActivity::class.java)
//            startActivity(intent)
//        }


//        val btn = findViewById<Button>(R.id.button)



//        show()
    }


    private fun signIn() {
        val signInIntent = gsc.signInIntent
        startActivityForResult(signInIntent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                task.getResult(ApiException::class.java)
                navigateToSecondActivity()
            } catch (e: ApiException) {
                Log.d("Error",e.toString())
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun navigateToSecondActivity() {
        finish()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }



//    private fun signInGoogle(){
//        val signInIntent = googleSignInClient.signInIntent
//        launcher.launch(signInIntent)
//    }
//
//    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//            result ->
//        if (result.resultCode == Activity.RESULT_OK){
//
//            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//            handleResults(task)
//        }
//    }
//    private fun handleResults(task: Task<GoogleSignInAccount>) {
//        if (task.isSuccessful){
//            val account : GoogleSignInAccount? = task.result
//            if (account != null){
//                updateUI(account)
//            }
//        }else{
//            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//    private fun updateUI(account: GoogleSignInAccount) {
//        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//        auth.signInWithCredential(credential).addOnCompleteListener {
//            if (it.isSuccessful) {
//                val intent = Intent(this, HomeActivity::class.java)
//                intent.putExtra("email", account.email)
//                intent.putExtra("name", account.displayName)
//                startActivity(intent)
//            } else {
//                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
//
//            }
//        }
//
//    }

    private fun show(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.login_dialog)

        val animationView = dialog.findViewById<LottieAnimationView>(R.id.animationView)
        animationView.playAnimation()

//        animationView.addAnimatorUpdateListener {
////            dialog.dismiss()
////            binding.verifyBtn.setVisibility(View.VISIBLE)
////            binding.verifyTxt.setVisibility(View.VISIBLE)
//
////            val intent =Intent(this,LoginActivity::class.java)
////            startActivity(intent)
//        }

        val login_btn = findViewById<Button>(R.id.closeButton)


        val closeButton = dialog.findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }
}