package com.example.krishimitra.fragments

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.krishimitra.R
import com.example.krishimitra.databinding.FragmentGreetBinding
import com.example.krishimitra.models.TaskItem
import com.example.krishimitra.roomDatabase.TaskAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.sunayanpradhan.weatherapptutorial.Models.WeatherModel
import com.sunayanpradhan.weatherapptutorial.Utilites.ApiUtilities
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.checkerframework.checker.units.qual.s
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import kotlin.math.roundToInt

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
    private lateinit var userDay : String
    private lateinit var userYear : String
    private lateinit var UserMonth : String

    private lateinit var path : String

    //weather
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private val LOCATION_REQUEST_CODE = 101

    private val apiKey="f70ca239bf30695349b25a9bb3361c69"



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGreetBinding.inflate(inflater,container,false)

        val view = inflater.inflate(R.layout.fragment_greet, container, false)
        database = Firebase.database.reference

        fusedLocationProvider= LocationServices.getFusedLocationProviderClient(requireContext())
        getCurrentLocation()

        binding.currentLocation.setOnClickListener {

            getCurrentLocation()

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
            getCardData(path)
        }else{
//            Toast.makeText(activity,username,Toast.LENGTH_SHORT).show()
            binding.username.text=username
            getRecylerView(email!!)
//            Toast.makeText(activity,email,Toast.LENGTH_SHORT).show()
            path = email
            getCardData(path)
        }


        binding.citySearch.setOnEditorActionListener { textView, i, keyEvent ->

            if (i == EditorInfo.IME_ACTION_SEARCH) {

                getCityWeather(binding.citySearch.text.toString())

                val view = activity?.currentFocus

                if (view != null) {
                    val imm =
                        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    binding.citySearch.clearFocus()
                }

                return@setOnEditorActionListener true

            } else {

                return@setOnEditorActionListener false
            }

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
                        userDay = selectedDayOfMonth.toString()
                        userYear = selectedYear.toString()
                        UserMonth = (selectedMonth+1).toString()

                        Toast.makeText(activity,UserMonth,Toast.LENGTH_SHORT).show()



                      //  Toast.makeText(activity,"$date",Toast.LENGTH_SHORT).show()
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

    private fun getCityWeather(city: String) {

      //  binding.progressBar.visibility= View.VISIBLE

        ApiUtilities.getApiInterface()?.getCityWeatherData(city,apiKey)
            ?.enqueue(object : Callback<WeatherModel> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {
                    if (response.isSuccessful){



                       // binding.progressBar.visibility= View.GONE

                        response.body()?.let {
                            setData(it)
                        }

                    }
                    else{

                        Toast.makeText(activity, "No City Found",
                            Toast.LENGTH_SHORT).show()

                      //  binding.progressBar.visibility= View.GONE

                    }

                }

                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {


                }


            })


    }

    private fun getCardData(cropPath: String){
        val dbRef = Firebase.firestore
        dbRef.collection("cropsdata").document(cropPath).get()
            .addOnSuccessListener {documentSnapshot ->
                if (documentSnapshot.exists()){
                    val crop = documentSnapshot.getString("Crop Name")
                    val temp = documentSnapshot.getString("temperature")
                    val n = documentSnapshot.getString("N")
                    val p = documentSnapshot.getString("P")
                    val k = documentSnapshot.getString("K")
                    val humidity = documentSnapshot.getString("humidity")
                    val ph = documentSnapshot.getString("ph")
                    val rain = documentSnapshot.getString("rainfall")



                    binding.rainfallTextView.text = "Rainfall : $rain mm"
                    binding.cropNameTextView.text = "$crop"
                    binding.tempTextView.text = "Temp : $temp °C"
                    binding.nTextView.text = "Nitrogen: $n"
                    binding.pTextView.text = "Phosphorous : $p"
                    binding.kTextView.text = "Potassium : $k"
                    binding.humidityTextView.text = "Humidity : $humidity %"
                    binding.phTextView.text = "PH value : $ph"
                }

            }
    }

    private fun fetchCurrentLocationWeather(latitude: String, longitude: String) {

        ApiUtilities.getApiInterface()?.getCurrentWeatherData(latitude,longitude,apiKey)
            ?.enqueue(object :Callback<WeatherModel>{
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {

                    if (response.isSuccessful){

                       // binding.progressBar.visibility= View.GONE

                        response.body()?.let {
                            setData(it)
                        }

                    }


                }

                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {


                }

            })


    }

    private fun getCurrentLocation(){

        if (checkPermissions()){

            if (isLocationEnabled()){

                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    requestPermission()

                    return
                }
                fusedLocationProvider.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            currentLocation = location

                          //  binding.progressBar.visibility = View.VISIBLE

                            fetchCurrentLocationWeather(
                                location.latitude.toString(),
                                location.longitude.toString()
                            )


                        }
                    }

            }
            else{

                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)

                startActivity(intent)


            }


        }
        else{

            requestPermission()

        }


    }

    private fun requestPermission() {

        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_REQUEST_CODE
        )
    }

    private fun isLocationEnabled(): Boolean {

        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE)
                as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun checkPermissions(): Boolean {

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==LOCATION_REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation()
            }
            else{
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData(body:WeatherModel){

        binding.apply {

            val currentDate= SimpleDateFormat("dd/MM/yyyy hh:mm").format(Date())

            temp.text=""+k2c(body?.main?.temp!!)+"°"
            weatherTitle.text=body.weather[0].main
            sunriseValue.text=ts2td(body.sys.sunrise.toLong())
            sunsetValue.text=ts2td(body.sys.sunset.toLong())
            pressureValue.text=body.main.pressure.toString()
            humidityValue.text=body.main.humidity.toString()+"%"
            tempFValue.text=""+(k2c(body.main.temp).times(1.8)).plus(32)
                .roundToInt()+"°"
            feelsLike.text= ""+k2c(body.main.feels_like)+"°"
            windValue.text=body.wind.speed.toString()+"m/s" }
        updateUI(body.weather[0].id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ts2td(ts:Long):String{

        val localTime=ts.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
        }
        return localTime.toString()
    }

    private fun k2c(t:Double):Double{

        var intTemp=t

        intTemp=intTemp.minus(273)

        return intTemp.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }

    private fun updateUI(id: Int) {
        binding.apply {
            when (id) {
                in 200..232 -> {
                    weatherImg.setImageResource(R.drawable.ic_storm_weather)
                    optionsLayout.background= ContextCompat
                        .getDrawable(requireContext(), R.drawable.thunderstrom_bg)
                }
                in 300..321 -> {
                    weatherImg.setImageResource(R.drawable.ic_few_clouds)
                    optionsLayout.background= ContextCompat
                        .getDrawable(requireContext(), R.drawable.drizzle_bg)
                }
                in 500..531 -> {
                    weatherImg.setImageResource(R.drawable.ic_rainy_weather)
                    optionsLayout.background= ContextCompat
                        .getDrawable(requireContext(), R.drawable.rain_bg)
                }
                in 600..622 -> {
                    weatherImg.setImageResource(R.drawable.ic_snow_weather)
                    optionsLayout.background= ContextCompat
                        .getDrawable(requireContext(), R.drawable.snow_bg)
                }
                in 701..781 -> {
                    weatherImg.setImageResource(R.drawable.ic_broken_clouds)
                    optionsLayout.background= ContextCompat
                        .getDrawable(requireContext(), R.drawable.atmosphere_bg)
                }
                800 -> {
                    weatherImg.setImageResource(R.drawable.ic_clear_day)
                    optionsLayout.background= ContextCompat
                        .getDrawable(requireContext(), R.drawable.clear_bg)
                }
                in 801..804 -> {
                    weatherImg.setImageResource(R.drawable.ic_cloudy_weather)
                    optionsLayout.background= ContextCompat
                        .getDrawable(requireContext(), R.drawable.clouds_bg)
                }
                else->{
                    weatherImg.setImageResource(R.drawable.ic_unknown)
                    optionsLayout.background= ContextCompat
                        .getDrawable(requireContext(), R.drawable.unknown_bg)
                }
            }
        }
    }
}