package com.example.kotlinfirebasenotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.icu.text.CaseMap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.kotlinfirebasenotification.config.Config
import com.example.kotlinfirebasenotification.config.Config.CHANNEL_ID
import com.example.kotlinfirebasenotification.config.Config.STR_PUSH

class MainActivity : AppCompatActivity() {

    var mRegistrationBroadcastReceiver: BroadcastReceiver? = null

    override fun onPause() {
        mRegistrationBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                it
            )
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mRegistrationBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).registerReceiver(
                it,
                IntentFilter("registrationComplete")
            )
            LocalBroadcastManager.getInstance(this).registerReceiver(
                it,
                IntentFilter(Config.STR_PUSH)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        mRegistrationBroadcastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent!!.action == STR_PUSH){
                    val message = intent.getStringExtra("message")
                    showNotification("Dev777Popov", message)
                }
            }
        }
    }

    private fun showNotification(title: String, message: String?) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(getNextNotificationId(), builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getNextNotificationId() : Int {
        val sp = getSharedPreferences("notifyID", MODE_PRIVATE)
        val id = sp.getInt("notification_id_key", 0)
        sp.edit().putInt("notification_id_key", (id + 1) % Int.MAX_VALUE).apply()
        return id
    }

}
