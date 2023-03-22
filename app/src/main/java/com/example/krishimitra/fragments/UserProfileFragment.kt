package com.example.krishimitra.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.krishimitra.EditProfileActivity
import com.example.krishimitra.R
import com.example.krishimitra.databinding.FragmentGreetBinding
import com.example.krishimitra.databinding.FragmentUserProfileBinding

class UserProfileFragment : Fragment() {


    private lateinit var Editbutton : Button
    private lateinit var binding : FragmentUserProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUserProfileBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_user_profile, container, false)

        binding.editprofilebtn.setOnClickListener {
           val intent = Intent(activity,EditProfileActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }


}