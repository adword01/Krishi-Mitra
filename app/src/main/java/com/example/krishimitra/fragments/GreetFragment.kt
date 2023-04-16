package com.example.krishimitra.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.krishimitra.MapActivity
import com.example.krishimitra.R
import com.example.krishimitra.databinding.FragmentGreetBinding
import com.example.krishimitra.models.TaskItem
import com.example.krishimitra.roomDatabase.TaskAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class GreetFragment : Fragment()  {

    private lateinit var binding : FragmentGreetBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth : FirebaseAuth
    private lateinit var authName : String
    private lateinit var database: DatabaseReference
    private lateinit var authEmail : String
    private lateinit var date : String
    private lateinit var authnumber : String
    private lateinit var time : String
    private lateinit var messaging: FirebaseMessaging

    private lateinit var path : String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGreetBinding.inflate(inflater,container,false)

        val view = inflater.inflate(R.layout.fragment_greet, container, false)
        database = Firebase.database.reference
        binding.mapIv.setOnClickListener {
            val intent = Intent(activity, MapActivity::class.java)
            startActivity(intent)
        }



        binding.addToDo.setOnClickListener {
            lifecycleScope.launch {
                showbottomsheet()
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())

        messaging = FirebaseMessaging.getInstance()
        lifecycleScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                // Log and toast
                Log.d(TAG, "FCM registration token: $token")

//                Toast.makeText(requireContext(), "FCM registration token: $token", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Error getting FCM registration token", e)
            }

        }


        val sharedPreferences = requireContext().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        val email = sharedPreferences.getString("email","email")

        if (username == null){
            auth = FirebaseAuth.getInstance()
            authName = auth.currentUser!!.displayName.toString()
            authEmail = auth.currentUser!!.email.toString()
            binding.username.text  = authName
            Toast.makeText(activity,authName,Toast.LENGTH_SHORT).show()
            getRecylerView(authEmail)
            path = authEmail
        }else{
            Toast.makeText(activity,username,Toast.LENGTH_SHORT).show()
            binding.username.text=username
            getRecylerView(email!!)
            Toast.makeText(activity,email,Toast.LENGTH_SHORT).show()
            path = email
        }


        val date = Date()
        val cal: Calendar = Calendar.getInstance()
        cal.time = date
        when(cal.get(Calendar.HOUR_OF_DAY)){
            in 0..12 ->{
                binding.greettext.text = "Good Morning!!"
                binding.greetIv.setImageResource(R.drawable.morning)
            }
            in 12..17 ->{
                binding.greettext.text = "Good Afternoon!!"
                binding.greetIv.setImageResource(R.drawable.noon)

            }
            in 17..21 ->{
                binding.greettext.text = "Good evening!!"
                binding.greetIv.setImageResource(R.drawable.evening)

            }
            else -> {
                binding.greettext.text = "Good Night!!"
                binding.greetIv.setImageResource(R.drawable.night)

            }
        }
        return binding.root
    }

    private fun showbottomsheet() {

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottomsheet, null)
        bottomSheetDialog.setContentView(view)

        val btndate = view.findViewById<FloatingActionButton>(R.id.btndatepicker)
        val savebtn = view.findViewById<Button>(R.id.btnSave)
        val btntime = view.findViewById<FloatingActionButton>(R.id.tvTime)

        btntime.setOnClickListener {

            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)
            val timePicker = TimePickerDialog(
                context,
                { _, selectedHour, selectedMinute ->
                    time = String.format("%02d:%02d", selectedHour, selectedMinute)
                    view.findViewById<EditText>(R.id.selectedtime).setText(time)

                },
                hour,
                minute,
                true
            )
            timePicker.show()
        }
        btndate.setOnClickListener {

            val currentDate = Calendar.getInstance()
            val year = currentDate.get(Calendar.YEAR)
            val month = currentDate.get(Calendar.MONTH)
            val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)

            val datePicker = activity?.let { it1 ->
                DatePickerDialog(
                    it1,
                    { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                        date = String.format(
                            "%02d/%02d/%04d",
                            selectedDayOfMonth,
                            selectedMonth + 1,
                            selectedYear
                        )
                        Toast.makeText(activity,"$date",Toast.LENGTH_SHORT).show()
                        view.findViewById<EditText>(R.id.selecteddate).setText(date)
                    },
                    year,
                    month,
                    dayOfMonth
                )
            }
            datePicker!!.show()
        }

       savebtn.setOnClickListener {

           bottomSheetDialog.dismiss()
           FirebaseMessaging.getInstance().subscribeToTopic("tasks")

           val description = view.findViewById<EditText>(R.id.etDescription).text.toString()

            if (description.isNotBlank() ) {
                val key = database.push().key // generate a new key for the task item
                val ID = key // set the key as the ID field
                val task = TaskItem(id = ID!!, description = description, time = time, date = date)
                key?.let {
                    val db = Firebase.firestore
                    val usertask = hashMapOf(
                        "id" to ID.toString(),
                        "description" to description,
                        "time" to time,
                        "date" to date
                    )
                    db.collection(path).document(key).set(usertask)
                    bottomSheetDialog.dismiss()
                }

            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        bottomSheetDialog.show()

    }

    private fun getRecylerView(path : String) {


        val db = Firebase.firestore

        db.collection(path).get()
            .addOnSuccessListener { documents ->
                val tasks = mutableListOf<TaskItem>()
                for (document in documents) {
                    val task = document.toObject(TaskItem::class.java)
                    tasks.add(task)
                }
                val adapter = TaskAdapter(tasks,path)
                binding.todoListRecyclerView.adapter = adapter
                binding.todoListRecyclerView.layoutManager = LinearLayoutManager(context)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }
}