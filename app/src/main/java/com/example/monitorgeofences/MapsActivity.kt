package com.example.monitorgeofences

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.monitorgeofences.utils.Constants.GEOFENCE_ID
import com.example.monitorgeofences.databinding.ActivityMapsBinding
import com.example.monitorgeofences.service.AlarmService
import com.example.monitorgeofences.utils.AlertDialogHelper
import com.example.monitorgeofences.utils.GeofenceHelper
import com.example.monitorgeofences.viewmodel.MapsActivityViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.*
import android.location.Geocoder
import java.util.*


@SuppressLint("UnspecifiedImmutableFlag")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener,GoogleMap.OnCameraMoveListener{

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper
    private lateinit var viewModel: MapsActivityViewModel
    private lateinit var guideCircle:Circle
    private lateinit var circle:Circle
    private lateinit var marker:Marker



    private var lastKnownLocation:LatLng? = null


    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val stopIntent = Intent(this, AlarmService::class.java)
        stopService(stopIntent)

        mapInitialize()
        viewModel = ViewModelProvider(this)[MapsActivityViewModel::class.java]

        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)

        binding.button.setOnClickListener{saveButton()}


        seekBar()







    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        requestPermission()
        AlertDialogHelper.alertDialog(this)
        mMap.setOnMapLongClickListener(this)
        mMap.setOnCameraMoveListener(this)

        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) { lastKnownLocation = viewModel.lastKnownLocation(this)
            if (lastKnownLocation != null) {
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            lastKnownLocation!!.latitude,
                            lastKnownLocation!!.longitude
                        ), 15f
                    )
                )
            }
        }

        guideCircle(0)





    }

    override fun onMapLongClick(p0: LatLng) {
        /*
        mMap.clear()
        addMarker(p0)
        addCircle(p0,guideCircle.radius)
        val location = viewModel.address(this, p0)
        setLocation(this, location, p0,guideCircle.radius)

         */







    }



    @SuppressLint("MissingPermission")
    private fun addGeofence(latLng: LatLng, radius: Double) {
        val geofence = geofenceHelper.getGeofence(
            GEOFENCE_ID,
            latLng,
            radius,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL
        )
        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent = geofenceHelper.geofencePendingIntent

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                println("çalıştı")
            }
            .addOnFailureListener {
                println(it.toString())
            }


    }

    private fun addMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        marker = mMap.addMarker(markerOptions)

    }

    private fun addCircle(latLng: LatLng, radius: Double) {
        val circleOptions = CircleOptions()
        circleOptions.center(latLng)
        circleOptions.radius(radius)
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions.strokeWidth(4f)
        circle = mMap.addCircle(circleOptions)
    }

    private fun mapInitialize() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    private fun requestPermission() {

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                Toast.makeText(this, "PERMISSION'S ALREADY GRANTED", Toast.LENGTH_LONG).show()
                mMap.isMyLocationEnabled = true

            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
            }

        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.isMyLocationEnabled = true
                } else {
                    finish()
                }

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


    }

    private fun setLocation(context: Context,address:String, latLng: LatLng,radius: Double) {

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setMessage("Destination will be saved as $address")
            .setCancelable(false)
            .setPositiveButton("YES") { _, _ ->
                addGeofence(latLng, radius)
            }

            .setNegativeButton("NO") { dialog, _ ->
                marker.remove()
                circle.remove()
                guideCircle(1)
                dialog.cancel()
            }
        val alert: AlertDialog? = builder.create()
        alert?.show()


    }

    override fun onCameraMove() {

        guideCircle.center = mMap.cameraPosition.target


    }

    private fun saveButton(){

        mMap.clear()
        addCircle(guideCircle.center,guideCircle.radius)
        addMarker(guideCircle.center)
        val address = getAddress(this,guideCircle.center)
        setLocation(this,address,guideCircle.center,guideCircle.radius)






    }




    private fun seekBar(){
        binding.seekBar.max = 10000
        binding.seekBar.min = 100

        binding.seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                guideCircle.radius = progress.toDouble()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                println("")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                println("")
            }


        })
    }

    private fun guideCircle(status:Int){
        val circleOptions = CircleOptions()
        if (lastKnownLocation!= null){
            if (status == 0){
                circleOptions.center(lastKnownLocation!!)
            }
            if (status == 1){
                circleOptions.center(mMap.cameraPosition.target)
            }

        }
        else{
            circleOptions.center(mMap.cameraPosition.target)
        }

        circleOptions.radius(100.0)
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions.strokeWidth(4f)
        guideCircle = mMap.addCircle(circleOptions)
    }


    fun getAddress(context: Context,latLng: LatLng):String{
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1)
        return addresses[0].getAddressLine(0)

    }


}
