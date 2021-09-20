package com.example.monitorgeofences.utils
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.example.monitorgeofences.broadcast.GeoFenceBroadcastReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng


class GeofenceHelper(base: Context?) :ContextWrapper(base) {

     fun getGeofencingRequest(geofence:Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geofence)
        }.build()

    }

    fun getGeofence(id:String,latLng: LatLng,radius:Double,transitionTypes:Int): Geofence {
        return Geofence.Builder()

            .setRequestId(id)
            .setCircularRegion(
                latLng.latitude,
                latLng.longitude,
                radius.toFloat()
            )
            .setLoiteringDelay(5000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(transitionTypes)
            .build()
    }

     val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeoFenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }




}