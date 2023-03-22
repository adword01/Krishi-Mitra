package com.example.krishimitra

import android.app.Dialog
import android.app.ProgressDialog.show
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.airbnb.lottie.LottieAnimationView
import com.example.krishimitra.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)

        var binding: ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        val btn = findViewById<Button>(R.id.button)



        show()
    }
    private fun show(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.login_dialog)

        val animationView = dialog.findViewById<LottieAnimationView>(R.id.animationView)
        animationView.playAnimation()

//        animationView.setOnFocusChangeListener{
//            val intent = Intent(this,MainActivity::class.java)
//            startActivity(intent)
//        }

        val login_btn = findViewById<Button>(R.id.closeButton)



        val closeButton = dialog.findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }
}