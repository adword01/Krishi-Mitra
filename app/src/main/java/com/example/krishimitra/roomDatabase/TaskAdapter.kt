package com.example.krishimitra.roomDatabase

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.krishimitra.R
import com.example.krishimitra.models.TaskItem
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TaskAdapter( private var tasks: MutableList<TaskItem>,private var path : String) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.nameTextView.text = task.description
        holder.dateTextView.text = task.date
        holder.timeTextView.text = task.time

        holder.moreBtn.setOnClickListener {
            holder.btnDelete.visibility=View.VISIBLE
            holder.moreBtn.visibility=View.INVISIBLE
        }

        holder.btnDelete.setOnClickListener {
            val db = Firebase.firestore
            val docRef = db.collection(path).document(task.id)
            docRef.delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting document", e)
                }
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
        val moreBtn:ImageView=itemView.findViewById(R.id.more_btn)
        val btnDelete: ImageView = itemView.findViewById(R.id.deleteButton)
    }
}



