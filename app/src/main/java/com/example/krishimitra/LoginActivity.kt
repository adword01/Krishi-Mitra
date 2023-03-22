package com.example.krishimitra

import android.app.Dialog
import android.app.ProgressDialog.show
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.airbnb.lottie.LottieAnimationView

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)





        show()
    }
    private fun show(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.login_dialog)

        val animationView = dialog.findViewById<LottieAnimationView>(R.id.animationView)
        animationView.playAnimation()

        val closeButton = dialog.findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }
}