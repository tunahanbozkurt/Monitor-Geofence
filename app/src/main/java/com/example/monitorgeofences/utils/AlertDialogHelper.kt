package com.example.monitorgeofences.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.text.Html


class AlertDialogHelper {

    companion object {

        fun alertDialog(context: Context) {
            val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                buildAlertMessageNoGps(context)
            }


        }


        private fun buildAlertMessageNoGps(context: Context) {
            val builder:AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes"
                ) { dialog, id -> context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                .setNegativeButton("No"
                ) { dialog, id ->
                    dialog.cancel()
                    /*CANCEL METHOD*/ }

            val alert: AlertDialog? = builder.create()
            alert?.show()

        }


    }



}