package com.example.krishimitra.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.krishimitra.R
import com.example.krishimitra.databinding.FragmentGreetBinding
import com.example.krishimitra.models.TaskItem
import com.example.krishimitra.roomDatabase.AddTaskBottomSheetFragment
import com.example.krishimitra.roomDatabase.AddTaskViewModel
import com.example.krishimitra.roomDatabase.TaskAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class GreetFragment : Fragment()  {

    private lateinit var binding : FragmentGreetBinding
    private lateinit var phoneNumberWithoutCountryCode : String
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth : FirebaseAuth
    private lateinit var authName : String
    private lateinit var authEmail : String
    private lateinit var authnumber : String
   // private lateinit var tasks: ArrayList<TaskItem>
    private lateinit var viewModel: AddTaskViewModel
    private lateinit var taskAdapter: TaskAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGreetBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment

//        val args = this.arguments
//        val inputData = args?.get("name")
//        binding.profileName.text = inputData.toString()
//
        val view = inflater.inflate(R.layout.fragment_greet, container, false)

        getRecylerView()


        binding.addToDo.setOnClickListener {
            val bottomSheetFragment = AddTaskBottomSheetFragment()
            bottomSheetFragment.show(parentFragmentManager, "AddTaskBottomSheetFragment")

        }
//        viewModel = ViewModelProvider(this).get(AddTaskViewModel::class.java)
//        viewModel.tasks.observe(viewLifecycleOwner, { tasks ->
//            adapter.tasks = tasks
//            adapter.notifyDataSetChanged()
//        })
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
            }
            in 12..17 ->{
                binding.greettext.text = "Good Afternoon!!"
            }
            in 17..21 ->{
                binding.greettext.text = "Good evening!!"
            }
            else -> {
                binding.greettext.text = "Good Night!!"
            }
        }


        //    return inflater.inflate(R.layout.fragment_greet, container, false)
        return binding.root
    }

    private fun getRecylerView() {
       // tasks = arrayListOf<TaskItem>()
        val database = FirebaseDatabase.getInstance()
        val tasksRef = database.getReference("tasks")


       // val recyclerView = requireView().findViewById<RecyclerView>(R.id.todoListRecyclerView)


        // set Up recyclerview

        tasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                 val tasks = mutableListOf<TaskItem>()
                for (taskSnapshot in dataSnapshot.children) {
                    val task = taskSnapshot.getValue(TaskItem::class.java)
                    tasks.add(task!!)
                    //   task?.let { tasks.add(it) }
                    //           Toast.makeText(activity, task.description,Toast.LENGTH_SHORT).show()
//                    Toast.makeText(activity, task.id,Toast.LENGTH_SHORT).show()
//                    Toast.makeText(activity, task.time,Toast.LENGTH_SHORT).show()
//                    Toast.makeText(activity, task.date,Toast.LENGTH_SHORT).show()
                }
                val Tadapter = TaskAdapter(tasks)
                binding.todoListRecyclerView.adapter = Tadapter
                Tadapter!!.setTasks(tasks)
                binding.todoListRecyclerView.layoutManager = LinearLayoutManager(context)
                Log.d("task", tasks.toString())
                //    Toast.makeText(activity,"Not found",Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"Not found",Toast.LENGTH_SHORT).show()
                // Handle errors
            }
        })
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