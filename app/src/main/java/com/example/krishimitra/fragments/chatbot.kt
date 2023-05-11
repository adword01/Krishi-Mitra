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
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
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

//    fun getResponse(question: String, callback: (String) -> Unit) {
//        val client = OkHttpClient()
//
//        val mediaType = "application/json".toMediaTypeOrNull()
//        val body = RequestBody.create(
//            mediaType,
//            """
//        {
//            "model": "gpt-3.5-turbo",
//            "messages": [
//                {
//                    "role": "user",
//                    "content": "$question"
//                }
//            ]
//        }
//        """.trimIndent()
//        )
//        val request = Request.Builder()
//            .url("https://openai80.p.rapidapi.com/chat/completions")
//            .post(body)
//            .addHeader("content-type", "application/json")
//            .addHeader("X-RapidAPI-Key", "6d81e91983msha29f724ed180833p1c98a4jsnb9dddd5f4933")
//            .addHeader("X-RapidAPI-Host", "openai80.p.rapidapi.com")
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                // Handle failure
//            //    Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
//              //  Log.e("error","API failed",e)
//                callback(e.message ?: "API failed")
//                //hideProgressBar()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val responseBody = response.body?.string()
//                if (responseBody != null) {
//                    val jsonObject = JSONObject(responseBody)
//                    val choices = jsonObject.getJSONArray("choices")
//                    if (choices.length() > 0) {
//                        val contentList = mutableListOf<String>()
//                        for (i in 0 until choices.length()) {
//                            val choice = choices.getJSONObject(i)
//                            val message = choice.getJSONObject("message")
//                            val content = message.getString("content")
//                            contentList.add(content)
//                        }
//                        val joinedContent = contentList.joinToString("\n")
//                        callback(joinedContent)
//                    } else {
//                        callback("No response received")
//                    }
//                } else {
//                    callback("Empty response")
//                }
//            }
//        })
//    }




    //smart gpt
//    fun getResponse(question: String, callback: (String) -> Unit) {
//        val client = OkHttpClient()
//
//        val mediaType = "application/json".toMediaTypeOrNull()
//        val body = RequestBody.create(mediaType, "{\r\n    \"query\": \"$question\"\r\n}")
//        val request = Request.Builder()
//            .url("https://smartgpt-api.p.rapidapi.com/ask")
//            .post(body)
//            .addHeader("content-type", "application/json")
//            .addHeader("X-RapidAPI-Key", "14679fcd16mshc1c1a5e7d60b28fp142d66jsnffa6a46e7d9f")
//            .addHeader("X-RapidAPI-Host", "smartgpt-api.p.rapidapi.com")
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                Log.e("error", "API failed", e)
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val body = response.body?.string()
//                if (body != null) {
//                    Log.v("data", body)
//                    val jsonObject = JSONObject(body)
//                    val result = jsonObject.getString("response")
//                    callback(result)
//                } else {
//                    Log.v("data", "empty")
//                }
//            }
//        })
//    }

    fun getResponse(question: String, callback: (String) -> Unit) {
        val client = OkHttpClient()

        val mediaType = "application/json".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, "{\n    \"question\": \"$question\"\n}")
        val request = Request.Builder()
            .url("https://simple-chatgpt-api.p.rapidapi.com/ask")
            .post(body)
            .addHeader("content-type", "application/json")
            .addHeader("X-RapidAPI-Key", "62b65d0695msh95412d851639d59p107fafjsncaa9096bce7b")
            .addHeader("X-RapidAPI-Host", "simple-chatgpt-api.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                callback("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val jsonObject = JSONObject(responseBody)
                    val result = jsonObject.getString("answer")
                   // val result = jsonObject.getString("choices")
                    callback(result)
                } else {
                    callback("Error: Empty response")
                }
            }
        })
    }

//    fun getResponse(question: String, callback: (String) -> Unit) {
//        val client = OkHttpClient()
//
//        val mediaType = "application/json".toMediaTypeOrNull()
//        val body = RequestBody.create(mediaType, "{\n    \"query\": \"$question\"\n}")
//        val request = Request.Builder()
//            .url("https://chatgpt-gpt4-ai-chatbot.p.rapidapi.com/ask")
//            .post(body)
//            .addHeader("content-type", "application/json")
//            .addHeader("X-RapidAPI-Key", "6d81e91983msha29f724ed180833p1c98a4jsnb9dddd5f4933")
//            .addHeader("X-RapidAPI-Host", "chatgpt-gpt4-ai-chatbot.p.rapidapi.com")
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                // Handle failure
//                callback("Error: ${e.message}")
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val responseBody = response.body?.string()
//                if (responseBody != null) {
//                    val jsonObject = JSONObject(responseBody)
//                    callback(jsonObject.toString())
//                } else {
//                    callback("Error: Empty response")
//                }
//            }
//        })
//    }


//    fun getResponse(question: String, callback: (String) -> Unit){
//        val apiKey="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"
//        // API_KEY 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
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
//               callback("No response")
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