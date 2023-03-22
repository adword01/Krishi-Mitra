package com.example.krishimitra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.krishimitra.databinding.ActivityHomeBinding
import com.example.krishimitra.fragments.GreetFragment
import com.example.krishimitra.fragments.UserProfileFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(GreetFragment())


        setUpNavigationview()
    }
    private fun loadFragment(fragment: Fragment) {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

    private fun setUpNavigationview() {
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.Home -> { val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                finish()}

                R.id.sale -> {
                  Toast.makeText(this@HomeActivity,"Sale selected",Toast.LENGTH_SHORT).show()
                }

                R.id.status -> {
                    Toast.makeText(this@HomeActivity,"Sale selected",Toast.LENGTH_SHORT).show()
                }
                R.id.usrprofile ->{
//                    val intent = Intent(this,UserProfileFragment::class.java)
//                    startActivity(intent)

                    val myFragment = UserProfileFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.add(R.id.profileFragment, myFragment)
                    transaction.commit()
                }
            }

            true
        }
    }
}