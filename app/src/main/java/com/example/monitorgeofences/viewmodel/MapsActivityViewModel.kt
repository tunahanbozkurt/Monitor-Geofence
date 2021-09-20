package com.example.monitorgeofences.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

class MapsActivityViewModel:ViewModel() {



    @SuppressLint("MissingPermission")
    fun lastKnownLocation(context: Context):LatLng?{
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val ltlng = location?.let { LatLng(it.latitude,location.longitude) }
        return ltlng
    }

    @SuppressLint("MissingPermission")
    fun currentLocation(context: Context){
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val ltlng = location?.let { LatLng(it.latitude,location.longitude) }



    }






}





