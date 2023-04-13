package com.example.krishimitra.roomDatabase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.krishimitra.R
import com.example.krishimitra.models.TaskItem
import com.google.firebase.database.FirebaseDatabase

//class TaskAdapter(private val tasks: List<Task>) :
//    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
//
//    private lateinit var database: DatabaseReference
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
//        private val timetxt: TextView = itemView.findViewById(R.id.timetxt)
//        private val datetxt: TextView = itemView.findViewById(R.id.datetxt)
//        private val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
//
//        fun bind(task: Task) {
//            nameTextView.text = task.description
//            timetxt.text = task.time
//            datetxt.text = task.date
//            deleteButton.setOnClickListener { //database.child("tasks").child(task.id!!).removeValue() }
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_task, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val task = tasks[position]
//        holder.bind(task)
//    }
//
//    override fun getItemCount(): Int = tasks.size
//}

//class TaskAdapter : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
//
//    private var tasks = mutableListOf<TaskItem>()
//    val database = FirebaseDatabase.getInstance()
//    val tasksRef = database.getReference("tasks")
//
//    fun setTasks(tasks: List<TaskItem>) {
//        this.tasks = tasks.toMutableList()
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_task, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val task = tasks[position]
//        holder.tvDescription.text = task.description
//        holder.tvDate.text = task.date
//        holder.tvTime.text = task.time
//
//        holder.btnDelete.setOnClickListener {
//            val taskId = task.id ?: ""
//            tasksRef.child(taskId).removeValue()
//        }
//    }
//
//
//
//    override fun getItemCount(): Int {
//        return tasks.size
//    }
//
//    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val tvDescription: TextView = view.findViewById(R.id.nameTextView)
//        val tvDate: TextView = view.findViewById(R.id.datetxt)
//        val tvTime: TextView = view.findViewById(R.id.timetxt)
//        val btnDelete: ImageButton = view.findViewById(R.id.deleteButton)
//    }
//}

class TaskAdapter( private var tasks: MutableList<TaskItem>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.nameTextView.text = task.description
        holder.dateTextView.text = task.date
        holder.timeTextView.text = task.time

        holder.btnDelete.setOnClickListener {
            val dbRef = FirebaseDatabase.getInstance().getReference("tasks").child(task.id)
            dbRef.removeValue().addOnSuccessListener {
              //  Toast.makeText(Context,"Quiz Removed Successfully",Toast.LENGTH_SHORT).show()
            }
           // tasksRef.child(taskId).removeValue()
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun setTasks(tasks: MutableList<TaskItem>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.datetxt)
        val timeTextView: TextView = itemView.findViewById(R.id.timetxt)
        val btnDelete: ImageView = itemView.findViewById(R.id.deleteButton)
    }
}



