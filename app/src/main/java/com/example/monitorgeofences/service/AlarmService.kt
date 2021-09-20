package com.example.monitorgeofences.service

import android.app.Service
import android.content.Intent

import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder

import com.example.monitorgeofences.utils.MediaPlayer


class AlarmService:Service() {

  private lateinit var mp: android.media.MediaPlayer
  private lateinit var  ringtoneUri: Uri


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mp.apply {
            setDataSource(applicationContext,ringtoneUri)
            prepare()
            start()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        mp.reset()
    }

    override fun onCreate() {

        mp = MediaPlayer.MP
        ringtoneUri =  RingtoneManager.getActualDefaultRingtoneUri(applicationContext,RingtoneManager.TYPE_ALARM)


    }
}