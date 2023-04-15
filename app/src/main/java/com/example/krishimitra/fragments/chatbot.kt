package com.example.krishimitra.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.krishimitra.databinding.FragmentChatbotBinding
import com.example.krishimitra.models.chatMessage
import com.example.krishimitra.roomDatabase.ChatAdapter
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class chatbot : Fragment() {

    private val client = OkHttpClient()
    private lateinit var binding: FragmentChatbotBinding
    private val chatMessages = mutableListOf<chatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatbotBinding.inflate(inflater,container,false)
        chatAdapter = ChatAdapter(chatMessages)

        binding.rvMessages.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.btnSend.setOnClickListener {
            val question = binding.etMessage.text.toString().trim()
            if (question.isNotEmpty()) {
                chatMessages.add(chatMessage(question, isBotMessage = false))
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                getResponse(question) { response ->
                    requireActivity().runOnUiThread {
                        chatMessages.add(chatMessage(response, isBotMessage = true))
                        chatAdapter.notifyItemInserted(chatMessages.size - 1)
                        binding.etMessage.text.clear()
                    }
                }
            }


            // Inflate the layout for this fragment

        }
        return binding.root
    }

    fun getResponse(question: String, callback: (String) -> Unit){
        val apiKey="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"
        val url="https://api.openai.com/v1/engines/text-davinci-003/completions"

        val requestBody="""
            {
            "prompt": "$question",
            "max_tokens": 500,
            "temperature": 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error","API failed",e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body=response.body?.string()
                if (body != null) {
                    Log.v("data",body)
                }
                else{
                    Log.v("data","empty")
                }
                val jsonObject= JSONObject(body)
                val jsonArray: JSONArray =jsonObject.getJSONArray("choices")
                val textResult=jsonArray.getJSONObject(0).getString("text")
                callback(textResult)
            }
        })
    }


}