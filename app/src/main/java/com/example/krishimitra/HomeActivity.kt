package com.example.krishimitra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.krishimitra.databinding.ActivityHomeBinding
import com.example.krishimitra.fragments.GreetFragment
import com.example.krishimitra.fragments.UserProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    lateinit var bottomNav : BottomNavigationView

  private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(GreetFragment())
        auth = FirebaseAuth.getInstance()

        val email = intent.getStringExtra("email")
        val displayName = intent.getStringExtra("name")

//        binding.textEmail.text = email
//        binding.textName.text = displayName
        setUpNavigationview()
    }
    private fun loadFragment(fragment: Fragment) {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

    private fun setUpNavigationview() {
       bottomNav=findViewById(R.id.bottom_nav) as BottomNavigationView
        bottomNav.setOnItemSelectedListener{
            when(it.itemId){
                R.id.Home -> {
//                    loadFragment(GreetFragment())
                    val intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                finish()
                }

                R.id.sale -> {
                  Toast.makeText(this@HomeActivity,"Sale selected",Toast.LENGTH_SHORT).show()
                }

                R.id.status -> {
                    Toast.makeText(this@HomeActivity,"Status selected",Toast.LENGTH_SHORT).show()
                }

                R.id.recommend -> {
                    Toast.makeText(this@HomeActivity,"Recommendation",Toast.LENGTH_SHORT).show()
                }
                R.id.usrprofile ->{
                    loadFragment(UserProfileFragment())
//                    true
//                    val intent = Intent(this,UserProfileFragment::class.java)
//                    startActivity(intent)

//                    val myFragment = UserProfileFragment()
//                    val transaction = supportFragmentManager.beginTransaction()
//                    transaction.add(R.id.profileFragment, myFragment)
//                    transaction.commit()
                }
            }

            true
        }
    }
}