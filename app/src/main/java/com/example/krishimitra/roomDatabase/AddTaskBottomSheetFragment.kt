package com.example.krishimitra.roomDatabase

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.krishimitra.R
import com.example.krishimitra.databinding.FragmentAddTaskBottomSheetBinding
import com.example.krishimitra.models.TaskItem
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*


class AddTaskBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddTaskBottomSheetBinding
    private lateinit var database: DatabaseReference
    private lateinit var date : String
    private lateinit var adapter: TaskAdapter

    // Get a reference to the ViewModel
    private val viewModel: AddTaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTaskBottomSheetBinding.inflate(inflater, container, false)
        database = Firebase.database.reference

        // Set onClickListeners for the date and time TextViews
        binding.tvDate.setOnClickListener {
           // viewModel.showDatePicker()
            // Get the current date
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
                        // For example, if you have a TextView with ID tvDate:
                        // val tvDate = findViewById<TextView>(R.id.tv_date)
                        // tvDate.text = formattedDate
                    },
                    year,
                    month,
                    dayOfMonth
                )
            }
            datePicker!!.show()
        }

        binding.tvTime.setOnClickListener {
            viewModel.showTimePicker()
        }

        // Observe the selected date and time LiveData objects and update the TextViews when they change
        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            binding.tvDate.text = date
        }

        viewModel.selectedTime.observe(viewLifecycleOwner) { time ->
            binding.tvTime.text = time
        }

        binding.btnSave.setOnClickListener {
            val description = binding.etDescription.text.toString()
            val time = binding.tvTime.text.toString()
            val date = binding.tvDate.text.toString()

          //  val date = binding.tvDate.text.toString()

            if (description.isNotBlank() && time.isNotBlank() && date.isNotBlank()) {
                val key = database.push().key // generate a new key for the task item
                val ID = key // set the key as the ID field
                val task = TaskItem(id = ID!!, description = description, time = time, date = date)
                key?.let {
                    database.child("tasks").child(key).setValue(task)
                }
            //    database.child("tasks").push().setValue(task)
//                val key = database.push().key // generate a new key for the task item
//                val ID = key // set the key as the ID field

                dismiss()
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        // In the onCreateView of the Greet fragment

//        val database = FirebaseDatabase.getInstance()
//        val tasksRef = database.getReference("tasks")
//
//        // set Up recyclerview
//
//        tasksRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val tasks = mutableListOf<Task>()
//                for (taskSnapshot in dataSnapshot.children) {
//                    val task = taskSnapshot.getValue(Task::class.java)
//                    task?.let { tasks.add(it) }
//                }
//                adapter.setTasks(tasks)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle errors
//            }
//        })


        return binding.root
    }


}
