package com.example.krishimitra

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

fun scheduleAlarm(context: Context, time: Calendar) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Create an intent for the broadcast receiver
    val intent = Intent(context, AlarmBroadcastReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)

    // Set the alarm to trigger at the specified time
    alarmManager.set(AlarmManager.RTC_WAKEUP, time.timeInMillis, pendingIntent)
}
