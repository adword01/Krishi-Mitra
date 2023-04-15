package com.example.krishimitra.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.krishimitra.databinding.FragmentPredictCropBinding
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPredictCropBinding.inflate(inflater, container, false)



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



}