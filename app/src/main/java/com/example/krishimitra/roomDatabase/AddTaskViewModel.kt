package com.example.krishimitra.roomDatabase

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.krishimitra.models.TaskItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class AddTaskViewModel : ViewModel() {

    private lateinit var database: DatabaseReference
    private val _selectedTime = MutableLiveData<String>()
    val selectedTime: LiveData<String>
        get() = _selectedTime

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String>
        get() = _selectedDate

  //  private lateinit var context: Context


    fun saveTask(description: String, time: String, date: String) {
        database = Firebase.database.reference

        val task = TaskItem(description = description, time = time, date = date)
        database.child("tasks").push().setValue(task)
    }
    fun setContext(context: Context) {
    }

    // Function to show the TimePicker dialog
    fun showTimePicker(context : Context) {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)
        val timePicker = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                // Set the selected time to the TextView
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                _selectedTime.value = formattedTime
            },
            hour,
            minute,
            true
        )
        timePicker.show()
    }

    // Function to show the DatePicker dialog
    fun showDatePicker(context: Context) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Set the selected date to the TextView
                val formattedDate = String.format("%02d/%02d/%04d", selectedDayOfMonth, selectedMonth + 1, selectedYear)
                _selectedDate.value = formattedDate
            },
            year,
            month,
            dayOfMonth
        )
        datePicker.show()
    }
}
