package com.example.monitorgeofences.broadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.monitorgeofences.MapsActivity
import com.example.monitorgeofences.R
import com.example.monitorgeofences.service.AlarmService
import com.example.monitorgeofences.utils.Constants
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeoFenceBroadcastReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { createNotificationChannel(it) }

        val geofenceEvent = GeofencingEvent.fromIntent(intent!!)
        val transitionEvent = geofenceEvent.geofenceTransition



         val notIntent = Intent(context, MapsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


         }
         val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, notIntent, 0)

        val builder = NotificationCompat.Builder(context!!, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("GEOFENCE")
            .setContentText("GEOFENCE WORKING")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)



        when(transitionEvent){
            Geofence.GEOFENCE_TRANSITION_ENTER -> {

                with(NotificationManagerCompat.from(context!!)) {

                    notify(1, builder.build())
                }
                val serviceIntent = Intent(context,AlarmService::class.java)
                context.startService(serviceIntent)


            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {println("HALA İÇERDE")}
            Geofence.GEOFENCE_TRANSITION_EXIT -> {println("ÇIKIS YAPILDI")}


        }
        if (geofenceEvent.hasError()){
            Log.e(TAG,"onReceive: Error receiving geofence event...")
        }

    }




    fun createNotificationChannel(context: Context) {

        val name = "CHANNEL NAME"
        val descriptionText = "CHANNEL DESCRIPTION"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}


