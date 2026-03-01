package com.example.umbrella

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.example.umbrella.data.AccelerometerDataStore
import com.example.umbrella.models.AccelerometerReading

class AccelerometerForegroundService : Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    override fun onCreate() {
        super.onCreate()

        AccelerometerNotificationHelper.createChannel(this)

        val notification = AccelerometerNotificationHelper.buildNotification(
            this,
            "Collecting accelerometer data..."
        )

        ServiceCompat.startForeground(
            this,
            AccelerometerNotificationHelper.NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
        )

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        accelerometer?.let { sensor ->
            sensorManager?.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        sensorManager?.unregisterListener(this)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        val reading = AccelerometerReading(
            x = event.values[0],
            y = event.values[1],
            z = event.values[2],
            timestamp = System.currentTimeMillis()
        )

        AccelerometerDataStore.update(reading)

        val text = "x=${"%.2f".format(reading.x)}  y=${"%.2f".format(reading.y)}  z=${"%.2f".format(reading.z)}"
        AccelerometerNotificationHelper.updateNotification(this, text)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AccelerometerForegroundService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, AccelerometerForegroundService::class.java)
            context.stopService(intent)
        }
    }
}