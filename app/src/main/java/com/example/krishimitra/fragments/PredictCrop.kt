package com.example.krishimitra.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.example.krishimitra.R
import com.example.krishimitra.databinding.FragmentPredictCropBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException

class PredictCrop : Fragment() {


    private lateinit var binding : FragmentPredictCropBinding
    private lateinit var threedots : LottieAnimationView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPredictCropBinding.inflate(inflater, container, false)

        binding.backBtn.setOnClickListener {
            val previousFragment = GreetFragment() // Create a new instance of the previous fragment
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, previousFragment)
            fragmentTransaction.commit()
//            val fragmentManager = requireActivity().supportFragmentManager
//
//                fragmentManager.popBackStack()
        }

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.cropdialogbox)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)

        threedots = dialog.findViewById(R.id.dotpgbar)
       // val cropDescription = dialog.findViewById<Tex
        // tView>(R.id.cropdescription)
        val closeButton = dialog.findViewById<Button>(R.id.closedialog)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }



        binding.predictbtn.setOnClickListener {
            // getData()

            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()

            val url = "https://krishimitra-0102.ue.r.appspot.com//predict"
            val requestBody = FormBody.Builder()
                .add("N", binding.Nitrogen.text.toString())
                .add("P", binding.Phosphorous.text.toString())
                .add("K", binding.Potassium.text.toString())
                .add("temperature", binding.Temperature.text.toString())
                .add("humidity", binding.Humidity.text.toString())
                .add("ph", binding.pH.text.toString())
                .add("rainfall", binding.rain.text.toString())
                .build()


            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()


            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody!!)
                    val cropName = jsonObject.getString("crop")

                    requireActivity().runOnUiThread {
                        Toast.makeText(activity, "Predicted crop: $cropName", Toast.LENGTH_SHORT)
                            .show()
                        dialog.show()
                        showProgressBar()
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.findViewById<TextView>(R.id.cropName).setText(cropName)
                        val db = FirebaseDatabase.getInstance()
                        db.reference.child("crops").child("English").get().addOnSuccessListener {
                            if (it.exists()){

                                val cropdescription = it.child(cropName).value

                                val storageRef = FirebaseStorage.getInstance().reference.child("$cropName.png")

                                storageRef.downloadUrl.addOnSuccessListener { uri ->
                                    hideProgressBar()
                                    Picasso.get().load(uri).into(dialog.findViewById<ImageView>(R.id.cropImg))
                                    dialog.findViewById<TextView>(R.id.cropdescription).visibility = View.VISIBLE
                                    dialog.findViewById<TextView>(R.id.cropdescription).setText(cropdescription.toString())
                                }.addOnFailureListener {
                                    // Handle any errors here
                                    hideProgressBar()
                                    Toast.makeText(requireContext(),"Failed to load image",Toast.LENGTH_SHORT).show()
                                }



                            }




                        }
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    requireActivity().runOnUiThread {
                        Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }



        }
        return binding.root
    }

    private fun showProgressBar() {
        threedots.visibility = View.VISIBLE

    }
    private fun hideProgressBar() {
        threedots.visibility = View.GONE
    }


}