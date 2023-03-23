package com.example.krishimitra

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding
    private lateinit var sendOTPBtn : Button
    private lateinit var auth : FirebaseAuth
    private var currentuser : FirebaseUser?=null
    private lateinit var phoneNumberET : EditText
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc : GoogleSignInClient
    private lateinit var number : String
    private lateinit var authEmail : String

    private lateinit var mProgressBar : ProgressBar

    companion object {
        private const val RC_SIGN_IN = 123
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)



        auth = FirebaseAuth.getInstance()
        currentuser = auth.currentUser
        auth = FirebaseAuth.getInstance()



        init()
        binding.otpbtn.setOnClickListener {
//            binding.verifyBtn.setVisibility(View.VISIBLE)
//            binding.verifyTxt.setVisibility(View.VISIBLE)
            binding.otpbtn.setVisibility(View.INVISIBLE)
            show()
            number = phoneNumberET.text.trim().toString()
            if (number.isNotEmpty()){
                if (number.length == 10){
                    number = "+91$number"
                  //  mProgressBar.visibility = View.VISIBLE
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)

                }else{
                    Toast.makeText(this , "Please Enter correct Number" , Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this , "Please Enter Number" , Toast.LENGTH_SHORT).show()

            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)



        binding.signIn.setOnClickListener {
            //signIn()
            signInGoogle()
//            val signInIntent = mGoogleSignInClient.signInIntent
//            startActivityForResult(signInIntent, RC_SIGN_IN)

        }

    //        binding.verifyBtn.setOnClickListener {
//            val intent =Intent(this,HomeActivity::class.java)
//            startActivity(intent)
//        }


//        val btn = findViewById<Button>(R.id.button)



//        show()
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
                val intent = Intent(this, HomeActivity::class.java)
              //  EmailData()
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)
                startActivity(intent)
                Log.d("Success","Successful")
            } else {
                Log.d("Error",it.exception.toString())
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

            }
        }

    }

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

    private fun init(){
        mProgressBar = findViewById(R.id.phoneProgressBar)
        mProgressBar.visibility = View.INVISIBLE
        sendOTPBtn = binding.otpbtn
        phoneNumberET = binding.phoneNumber
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this , "Authenticate Successfully" , Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this , HomeActivity::class.java))
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
               // mProgressBar.visibility = View.INVISIBLE
            }
    }


    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            }
        //    mProgressBar.visibility = View.VISIBLE
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            val intent = Intent(this@LoginActivity , OTPActivity::class.java)
            intent.putExtra("OTP" , verificationId)
            intent.putExtra("resendToken" , token)
            intent.putExtra("phoneNumber" , number)
            startActivity(intent)
        //    mProgressBar.visibility = View.INVISIBLE
        }
    }
    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            startActivity(Intent(this , HomeActivity::class.java))
        }
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
//                    intent.putExtra("Email",email)
//                    intent.putExtra("location",location)
//                    intent.putExtra("mobile",mobNumber)
//                    intent.putExtra("name",name)
                    val bundle = Bundle().apply {
                        putString("Email",email)
                        putString("location",location)
                        putString("mobile",mobNumber)
                        putString("name",name)
                    }

                    val fragment = UserProfileFragment()
                    fragment.arguments = bundle

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit()


                    Log.d(TAG, "Email: $email")
                    Log.d(TAG, "Email: $location")
                    Log.d(TAG, "Email: $mobNumber")
                    Log.d(TAG, "Email: $name")
                } else {
                    Log.d(TAG, "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting document", exception)
            }

    }
}