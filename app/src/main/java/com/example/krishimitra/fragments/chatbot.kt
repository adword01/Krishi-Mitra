package com.example.krishimitra.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.example.krishimitra.databinding.FragmentChatbotBinding
import com.example.krishimitra.models.chatMessage
import com.example.krishimitra.roomDatabase.ChatAdapter
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException


class chatbot : Fragment() {

    private val client = OkHttpClient()
    private lateinit var binding: FragmentChatbotBinding
    private val chatMessages = mutableListOf<chatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var threedots : LottieAnimationView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatbotBinding.inflate(inflater,container,false)
        chatAdapter = ChatAdapter(chatMessages)

        threedots = binding.dotpgbar


        binding.rvMessages.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.btnSend.setOnClickListener {
            val question = binding.etMessage.text.toString().trim()
            showProgressBar()
            if (question.isNotEmpty()) {
                chatMessages.add(chatMessage(question, isBotMessage = false))
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                getResponse(question) { response ->
                    requireActivity().runOnUiThread {
                        chatMessages.add(chatMessage(response, isBotMessage = true))
                        chatAdapter.notifyItemInserted(chatMessages.size - 1)
                        hideProgressBar()
                        chatAdapter.notifyItemInserted(chatMessages.size - 1)
                        binding.rvMessages.scrollToPosition(chatMessages.size - 1)
                        binding.etMessage.text.clear()
                    }
                }
            }else{
                Toast.makeText(activity,"Ask me anything",Toast.LENGTH_SHORT).show()
                hideProgressBar()
            }

        // Inflate the layout for this fragment

        }


        return binding.root
    }

    private fun showProgressBar() {
        threedots.visibility = View.VISIBLE

    }
    private fun hideProgressBar() {
        threedots.visibility = View.GONE
    }

    fun getResponse(question: String, callback: (String) -> Unit) {
        val client = OkHttpClient()

        val mediaType = "application/json".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, "{\r\n    \"query\": \"$question\"\r\n}")
        val request = Request.Builder()
            .url("https://smartgpt-api.p.rapidapi.com/ask")
            .post(body)
            .addHeader("content-type", "application/json")
            .addHeader("X-RapidAPI-Key", "14679fcd16mshc1c1a5e7d60b28fp142d66jsnffa6a46e7d9f")
            .addHeader("X-RapidAPI-Host", "smartgpt-api.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "API failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    Log.v("data", body)
                    val jsonObject = JSONObject(body)
                    val result = jsonObject.getString("response")
                    callback(result)
                } else {
                    Log.v("data", "empty")
                }
            }
        })
    }


//    fun getResponse(question: String, callback: (String) -> Unit){
//        val apiKey="API KEY"
//        val url="https://api.openai.com/v1/engines/text-davinci-003/completions"
//
//        val requestBody="""
//            {
//            "prompt": "$question",
//            "max_tokens": 500,
//            "temperature": 0
//            }
//        """.trimIndent()
//
//        val request = Request.Builder()
//            .url(url)
//            .addHeader("Content-Type", "application/json")
//            .addHeader("Authorization", "Bearer $apiKey")
//            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                Log.e("error","API failed",e)
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val body=response.body?.string()
//                if (body != null) {
//                    Log.v("data",body)
//                }
//                else{
//                    Log.v("data","empty")
//                }
//                val jsonObject= JSONObject(body)
//                val jsonArray: JSONArray =jsonObject.getJSONArray("choices")
//                val textResult=jsonArray.getJSONObject(0).getString("text")
//                callback(textResult)
//            }
//        })
//    }


}