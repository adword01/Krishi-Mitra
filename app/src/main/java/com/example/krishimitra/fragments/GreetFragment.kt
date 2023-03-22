package com.example.krishimitra.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.krishimitra.R
import com.example.krishimitra.databinding.FragmentGreetBinding
import java.util.*

class GreetFragment : Fragment() {

    private lateinit var binding : FragmentGreetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGreetBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        val date = Date()
        val cal: Calendar = Calendar.getInstance()
        cal.time = date
        when(cal.get(Calendar.HOUR_OF_DAY)){
            in 0..12 ->{
                binding.greetText.text = "Good Morning!!"
            }
            in 12..17 ->{
                binding.greetText.text = "Good Afternoon!!"
            }
            in 17..21 ->{
                binding.greetText.text = "Good evening!!"
            }
            else -> {
                binding.greetText.text = "Good Night!!"
            }
        }

        //    return inflater.inflate(R.layout.fragment_greet, container, false)

        return binding.root


    }


}