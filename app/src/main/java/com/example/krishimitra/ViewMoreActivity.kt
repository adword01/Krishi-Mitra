package com.example.krishimitra

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.krishimitra.models.CropData
import com.example.krishimitra.roomDatabase.CropDataAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewMoreActivity : AppCompatActivity() {

    private lateinit var authEmail : String
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_more)


        val sharedPreferences = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email",null)

        if (email == null){
            auth = FirebaseAuth.getInstance()
            authEmail = auth.currentUser!!.email.toString()
            // Toast.makeText(activity,authName,Toast.LENGTH_SHORT).show()

        }else{

            authEmail = email
        }

        val db = FirebaseFirestore.getInstance()

        val collectionRef = db.collection("cropsdata")
        val query = collectionRef.document(authEmail).collection("crops")


        query.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val resultList = ArrayList<CropData>()
                    for (document in task.result) {
                        val cropData = document.toObject(CropData::class.java)
                        resultList.add(cropData)
                    }
                    // Pass resultList to your RecyclerView adapter and update the UI
                    val recyclerView: RecyclerView = findViewById(R.id.cropRecyclerView)
                    val layoutManager = LinearLayoutManager(this)
                    recyclerView.layoutManager = layoutManager

                    val adapter = CropDataAdapter(resultList)
                    recyclerView.adapter = adapter

                    // Display the number of items added
//                    val itemCount = resultList.size
//                    val toastMessage = "Added $itemCount items to the RecyclerView"
//                    Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
                } else {
                    // Handle task.exception if query fails
                }
            }




    }
}