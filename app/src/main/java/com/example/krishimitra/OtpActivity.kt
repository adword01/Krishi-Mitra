package com.example.krishimitra

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class OtpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var verifyBtn: Button
    private lateinit var resend:TextView
    private lateinit var otp1:EditText
    private lateinit var otp2:EditText
    private lateinit var otp3:EditText
    private lateinit var otp4:EditText
    private lateinit var otp5:EditText
    private lateinit var otp6:EditText

    private lateinit var OTP:String
    private lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
    private lateinit var phone:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        OTP = intent.getStringExtra("otp").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phone=intent.getStringExtra("phone")!!

        auth=FirebaseAuth.getInstance()
        addTextChangeListener()


        verifyBtn.setOnClickListener {
            val typedOTP = (otp1.text.toString() +
                    otp2.text.toString() + otp3.text.toString() +
                    otp4.text.toString() + otp5.text.toString() +
                    otp6.text.toString())
            if (typedOTP.isNotEmpty()){
                if (typedOTP.length==6){
                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        OTP,typedOTP)
                    signInWithPhoneAuthCredential(credential)
                }else{
                    Toast.makeText(this,"Please enter correct OTP", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Error!!! \nPlease enter OTP",Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    Toast.makeText(this,"Authentication Succeeded",Toast.LENGTH_SHORT).show()
                    sendToMain()
                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun sendToMain(){
        startActivity(Intent(this,HomeActivity::class.java))
    }

    private fun addTextChangeListener(){
        otp1.addTextChangedListener(EditTextWatcher(otp1))
        otp2.addTextChangedListener(EditTextWatcher(otp2))
        otp3.addTextChangedListener(EditTextWatcher(otp3))
        otp4.addTextChangedListener(EditTextWatcher(otp4))
        otp5.addTextChangedListener(EditTextWatcher(otp5))
        otp6.addTextChangedListener(EditTextWatcher(otp6))
    }

    inner class EditTextWatcher(private val view:View): TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val text=s.toString()
            when(view.id){
                R.id.otpEditText1 -> if (text.length==1) otp2.requestFocus()
                R.id.otpEditText2 -> if (text.length==1) otp3.requestFocus() else if (text.isEmpty()) otp1.requestFocus()
                R.id.otpEditText3 -> if (text.length==1) otp4.requestFocus() else if (text.isEmpty()) otp2.requestFocus()
                R.id.otpEditText4 -> if (text.length==1) otp5.requestFocus() else if (text.isEmpty()) otp3.requestFocus()
                R.id.otpEditText5 -> if (text.length==1) otp6.requestFocus() else if (text.isEmpty()) otp4.requestFocus()
                R.id.otpEditText6 -> if (text.length==1) otp5.requestFocus()
            }
        }
    }
}