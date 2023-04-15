package com.example.krishimitra.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
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
import java.util.*

class GreetFragment : Fragment()  {

    private lateinit var binding : FragmentGreetBinding
    private lateinit var phoneNumberWithoutCountryCode : String
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth : FirebaseAuth
    private lateinit var authName : String
    private lateinit var database: DatabaseReference
    private lateinit var authEmail : String
    private lateinit var date : String
    private lateinit var authnumber : String
    private lateinit var time : String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGreetBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment

//
        val view = inflater.inflate(R.layout.fragment_greet, container, false)
        database = Firebase.database.reference
        getRecylerView()
        binding.mapIv.setOnClickListener {
            val intent = Intent(activity, MapActivity::class.java)
            startActivity(intent)
        }

        binding.addToDo.setOnClickListener {
            showbottomsheet()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        auth = FirebaseAuth.getInstance()
        authName = auth.currentUser!!.displayName.toString()
        authEmail = auth.currentUser!!.email.toString()
        authnumber = auth.currentUser!!.phoneNumber.toString()
        phoneNumberWithoutCountryCode = authnumber?.replace("^\\+\\d{1,2}".toRegex(), "").toString()


        if(auth.currentUser!!.email != null){
          //  EmailData()
           binding.username.text  = authName
        }else{
            phoneData()
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
        //    return inflater.inflate(R.layout.fragment_greet, container, false)
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
                    // Set the selected time to the TextView
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

            // Create a DatePickerDialog and show it
            val datePicker = activity?.let { it1 ->
                DatePickerDialog(
                    it1,
                    { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                        // Set the selected date to a TextView or some other view
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
                    db.collection(auth.currentUser!!.email!!).document(key).set(usertask)
                    database.child("tasks").child(auth.currentUser!!.uid).child(key).setValue(task)
                }

            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        bottomSheetDialog.show()

    }

    private fun getRecylerView() {
        auth = FirebaseAuth.getInstance()
        val path = auth.currentUser!!.uid

        val db = Firebase.firestore
       val email = auth.currentUser!!.email.toString()

        // set Up recyclerview

        db.collection(email).get()
            .addOnSuccessListener { documents ->
                val tasks = mutableListOf<TaskItem>()
                for (document in documents) {
                    val task = document.toObject(TaskItem::class.java)
                    tasks.add(task)
                }
                val adapter = TaskAdapter(tasks,email)
                binding.todoListRecyclerView.adapter = adapter
                binding.todoListRecyclerView.layoutManager = LinearLayoutManager(context)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }

    }


    private fun phoneData(){
        val db = Firebase.firestore
        val docRef = db.collection("User")
        val searchvalue = phoneNumberWithoutCountryCode

        docRef.whereEqualTo("mobileNumber",searchvalue)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents){
                    val name = document.getString("name")

                    binding.username.text = name
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting document", exception)
            }
    }





}