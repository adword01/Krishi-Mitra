package com.example.krishimitra

import android.app.Dialog
import android.app.ProgressDialog.show
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.airbnb.lottie.LottieAnimationView
import com.example.krishimitra.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)

        val binding: ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            binding.verifyBtn.setVisibility(View.VISIBLE)
            binding.verifyTxt.setVisibility(View.VISIBLE)
            binding.loginButton.setVisibility(View.INVISIBLE)

            show()
        }
        binding.verifyBtn.setOnClickListener {
            val intent =Intent(this,HomeActivity::class.java)
            startActivity(intent)
        }


//        val btn = findViewById<Button>(R.id.button)



//        show()
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
}