package com.example.krishimitra.roomDatabase

import android.content.Context
import android.os.Build.VERSION_CODES.N
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.krishimitra.R
import com.example.krishimitra.models.CropData

class CropDataAdapter(private val cropList: List<CropData>) :
    RecyclerView.Adapter<CropDataAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cropNameTextView: TextView = itemView.findViewById(R.id.crop_name_text_view)
        val kTextView: TextView = itemView.findViewById(R.id.k_text_view)
        val nTextView: TextView = itemView.findViewById(R.id.n_text_view)
        val pTextView: TextView = itemView.findViewById(R.id.p_text_view)
        val humidityTextView: TextView = itemView.findViewById(R.id.humidity_text_view)
        val phTextView: TextView = itemView.findViewById(R.id.ph_text_view)
        val rainfallTextView: TextView = itemView.findViewById(R.id.rainfall_text_view)
        val temperatureTextView: TextView = itemView.findViewById(R.id.temp_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_crop_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cropData = cropList[position]
        // Bind the data to the views
        holder.cropNameTextView.text = cropData.cropName
        holder.kTextView.text = "Potassium: " + cropData.K
        holder.nTextView.text = "Nitrogen: " + cropData.N
        holder.pTextView.text = "Phosphorous: " + cropData.P
        holder.humidityTextView.text = "Humidity: " + cropData.humidity
        holder.phTextView.text = "PH value: " + cropData.ph
        holder.rainfallTextView.text = "Rainfall: " + cropData.rainfall + "mm"
        holder.temperatureTextView.text = "Temp : " + cropData.temperature + "°C"


          //  Log.d("CropDataAdapter", "Crop Name: ${cropData.cropName}")


    }

    override fun getItemCount(): Int {
        return cropList.size
    }
}
